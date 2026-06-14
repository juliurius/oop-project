package pl.edu.tcs.tcsball.controller;

import javafx.scene.input.KeyEvent;
import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.model.*;
import pl.edu.tcs.tcsball.model.formation.FormationFactory;
import pl.edu.tcs.tcsball.model.lobby.Lobby;
import pl.edu.tcs.tcsball.model.player.FlagCatalog;
import pl.edu.tcs.tcsball.model.player.PlayerProfile;
import pl.edu.tcs.tcsball.net.discovery.DiscoveredHost;
import pl.edu.tcs.tcsball.net.protocol.MessageType;
import pl.edu.tcs.tcsball.net.protocol.NetworkMessage;
import pl.edu.tcs.tcsball.controller.customization.CustomizationController;
import pl.edu.tcs.tcsball.controller.customization.CustomizationManager;
import pl.edu.tcs.tcsball.controller.lobby.LobbyFlowController;
import pl.edu.tcs.tcsball.controller.lobby.LobbyManager;
import pl.edu.tcs.tcsball.controller.lobby.LobbyPresenter;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class GameManager implements LobbyView, CustomizationView {
    private final Match match;
    private final PhysicsEngine physics;
    private final LobbyManager lobbyManager = new LobbyManager();
    private final GameStateCodec gameStateCodec = new GameStateCodec();

    private final FormationFactory formationFactory = new FormationFactory();
    private final CustomizationManager customization;
    private final CustomizationController customizationController;
    private final LobbyPresenter lobbyPresenter;
    private final LobbyFlowController lobbyFlow;

    private GameState gameState = GameState.MENU;

    private final AimController aim = new AimController();

    private final EnumSet<DomainEvent> pendingEvents = EnumSet.noneOf(DomainEvent.class);
    private final InputDelta inputDelta = new InputDelta();

    private long lastGameStateSentMillis = 0;
    private boolean lastPhysicsActive = false;

    public GameManager(double width, double height) {
        FlagCatalog flags = new FlagCatalog();
        PlayerProfile defaultProfile = CustomizationManager.defaultProfile(flags, formationFactory);

        customization = new CustomizationManager(defaultProfile, flags.all(), formationFactory.getAvailableIds());
        customizationController = new CustomizationController(customization, formationFactory, inputDelta);
        match = new Match(formationFactory, defaultProfile, defaultProfile);
        physics = new PhysicsEngine(width, height);
        lobbyFlow = new LobbyFlowController(lobbyManager, customization, inputDelta,
                this::transitionTo, this::beginMultiplayerMatch, this::isLocalPlayerReady);
        lobbyPresenter = new LobbyPresenter(lobbyManager, customization, match,
                lobbyFlow::getDiscoveredHosts, () -> gameState, lobbyFlow::getJoinedHost);
    }

    public FrameDelta update(double deltaTime) {
        if (gameState != GameState.PLAYING && gameState != GameState.GOAL_SCORED) {
            updateLobbyNetwork();
        }

        if (isMultiplayerGame() && (gameState == GameState.PLAYING || gameState == GameState.GOAL_SCORED)) {
            return updateMultiplayerGame(deltaTime);
        }

        if (gameState == GameState.PLAYING) {
            return updateLocalPhysics(deltaTime);
        }

        return FrameDelta.idle();
    }

    private FrameDelta updateLocalPhysics(double deltaTime) {
        FrameDelta delta = physics.update(match.getPawns(), match.getBall(), deltaTime);

        if (physics.wasGoalScored()) {
            scoreGoal(physics.getLastGoalScoredByTeam());
        }

        return delta;
    }

    private FrameDelta updateMultiplayerGame(double deltaTime) {
        try {
            boolean receivedState = handleMultiplayerMessages();

            if (!isLocalPlayerHost()) {
                return receivedState ? new FrameDelta(true, false) : FrameDelta.idle();
            }

            if (gameState != GameState.PLAYING) {
                return FrameDelta.idle();
            }

            FrameDelta delta = updateLocalPhysics(deltaTime);
            boolean physicsJustSettled = lastPhysicsActive && !delta.physicsActive();
            lastPhysicsActive = delta.physicsActive();
            boolean forceSync = pendingEvents.contains(DomainEvent.SCORE_CHANGED)
                    || pendingEvents.contains(DomainEvent.TURN_CHANGED)
                    || physicsJustSettled;
            syncGameState(delta, forceSync);
            return delta;
        } catch (IOException exception) {
            leaveLobby();
            return FrameDelta.idle();
        }
    }

    private void updateLobbyNetwork() {
        try {
            boolean lobbyChanged = lobbyManager.updateNetwork();
            if (gameState == GameState.JOIN_LOBBY) {
                lobbyFlow.syncDiscoveredHosts();
                lobbyFlow.updatePendingJoin();
            }
            if (lobbyManager.consumeStartRequested()) {
                beginMultiplayerMatch();
                lobbyChanged = true;
            }
            if (lobbyChanged) {
                inputDelta.markMouseMoved();
            }
        } catch (IOException exception) {
            leaveLobby();
        }
    }

    private boolean handleMultiplayerMessages() throws IOException {
        boolean changed = false;

        for (NetworkMessage message : lobbyManager.drainNetworkMessages()) {
            try {
                changed |= handleMultiplayerMessage(message);
            } catch (IllegalArgumentException ignored) {
                // Uszkodzony komunikat meczu ignorujemy, zeby nie wywracac gry.
            }
        }

        return changed;
    }

    private boolean handleMultiplayerMessage(NetworkMessage message) throws IOException {
        return switch (message.type()) {
            case SHOT -> handleRemoteShot(message);
            case GAME_STATE -> applyGameState(message);
            case QUIT -> {
                leaveLobby();
                yield true;
            }
            default -> false;
        };
    }

    private boolean handleRemoteShot(NetworkMessage message) throws IOException {
        if (!isLocalPlayerHost() || message.fields().size() < 3) {
            return false;
        }

        int pawnIndex = Integer.parseInt(message.fields().get(0));
        double forceX = Double.parseDouble(message.fields().get(1));
        double forceY = Double.parseDouble(message.fields().get(2));

        boolean applied = applyShot(pawnIndex, new Vector2D(forceX, forceY));
        if (applied) {
            syncGameState(FrameDelta.idle(), true);
        }
        return applied;
    }

    public Set<DomainEvent> consumeEvents() {
        Set<DomainEvent> events = EnumSet.copyOf(pendingEvents);
        pendingEvents.clear();
        return events;
    }

    public InputDelta consumeInputDelta() {
        return inputDelta.consume();
    }

    public void handleCustomizationKey(KeyEvent event) {
        customizationController.handleKey(event);
    }

    public void cycleFlag(int direction) {
        customizationController.cycleFlag(direction);
    }

    public void cycleFormation(int direction) {
        customizationController.cycleFormation(direction);
    }

    // --- CustomizationView: odczyt dla ekranu customizacji ---

    @Override
    public String getPlayerName() {
        return customizationController.getPlayerName();
    }

    @Override
    public String getCurrentFlagCode() {
        return customizationController.getCurrentFlagCode();
    }

    @Override
    public String getCurrentFlagName() {
        return customizationController.getCurrentFlagName();
    }

    @Override
    public String getCurrentFormationName() {
        return customizationController.getCurrentFormationName();
    }

    public void startLocalGame() {
        PlayerProfile me = customization.getCurrentProfile();
        match.setProfiles(me, me);
        match.resetGame();
        pendingEvents.add(DomainEvent.MATCH_RESET);
        transitionTo(GameState.PLAYING);
    }

    public void openHostLobby() {
        lobbyFlow.openHostLobby();
    }

    public void openJoinLobby() {
        lobbyFlow.openJoinLobby();
    }

    public void refreshDiscoveredHosts() {
        lobbyFlow.refreshDiscoveredHosts();
    }

    public void joinHost(int index) {
        lobbyFlow.joinHost(index);
    }

    public void backFromJoinLobby() {
        lobbyFlow.backFromJoinLobby();
    }

    public void leaveClientLobby() {
        lobbyFlow.leaveClientLobby();
    }

    public void leaveLobby() {
        lobbyFlow.leaveLobby();
    }

    public void toggleLocalReady() {
        lobbyFlow.toggleLocalReady();
    }

    public void startMultiplayerFromLobby() {
        lobbyFlow.startMultiplayerFromLobby();
    }

    public void openCustomization() {
        transitionTo(GameState.CUSTOMIZATION);
    }

    private void beginMultiplayerMatch() {
        Lobby lobby = lobbyManager.getLobby().orElse(null);
        if (lobby == null || lobby.getGuest().isEmpty()) {
            return;
        }

        match.setProfiles(lobby.getHost().getProfile(), lobby.getGuest().get().getProfile());
        match.resetGame();
        lastGameStateSentMillis = 0;
        lastPhysicsActive = false;
        pendingEvents.add(DomainEvent.MATCH_RESET);
        transitionTo(GameState.PLAYING);
        sendGameStateQuietly();
    }

    private void syncGameState(FrameDelta delta, boolean force) throws IOException {
        if (!isMultiplayerGame() || !isLocalPlayerHost()) {
            return;
        }

        long now = System.currentTimeMillis();
        if (!force && now - lastGameStateSentMillis < GameConfig.NETWORK_STATE_SYNC_INTERVAL_MILLIS) {
            return;
        }
        if (!force && !delta.anyBodyMoved() && !delta.physicsActive()) {
            return;
        }

        lobbyManager.sendToPeer(gameStateCodec.encode(gameState, match));
        lastGameStateSentMillis = now;
    }

    private boolean applyGameState(NetworkMessage message) {
        boolean wasStopped = physics.isEverythingStopped(match.getPawns(), match.getBall());
        GameStateCodec.Decoded decoded = gameStateCodec.decode(message, match);

        aim.clear();
        if (decoded.turnChanged()) {
            pendingEvents.add(DomainEvent.TURN_CHANGED);
        }
        if (decoded.scoreChanged()) {
            pendingEvents.add(DomainEvent.SCORE_CHANGED);
        }
        if (wasStopped != physics.isEverythingStopped(match.getPawns(), match.getBall())) {
            inputDelta.markMouseMoved();
        }
        transitionTo(decoded.state());
        inputDelta.markAimingChanged();
        return true;
    }

    private void sendGameStateQuietly() {
        if (!isMultiplayerGame() || !isLocalPlayerHost()) {
            return;
        }

        try {
            syncGameState(FrameDelta.idle(), true);
        } catch (IOException exception) {
            leaveLobby();
        }
    }

    public ReadOnlyBall getBall() { return match.getBall(); }

    public void shootPawn() {
        if (!aim.hasSelection()) return;

        int pawnIndex = match.getPawns().indexOf(aim.getSelectedPawn());
        Vector2D shotForce = aim.getTension();

        if (isMultiplayerGame() && !isLocalPlayerHost()) {
            try {
                lobbyManager.sendToPeer(NetworkMessage.of(MessageType.SHOT,
                        Integer.toString(pawnIndex),
                        Double.toString(shotForce.x()),
                        Double.toString(shotForce.y())
                ));
            } catch (IOException exception) {
                leaveLobby();
            }
            clearAiming();
            return;
        }

        applyShot(pawnIndex, shotForce);
    }

    private boolean applyShot(int pawnIndex, Vector2D force) {
        if (pawnIndex < 0 || pawnIndex >= match.getPawns().size()) {
            return false;
        }
        if (!physics.isEverythingStopped(match.getPawns(), match.getBall())) {
            return false;
        }

        Pawn pawn = match.getPawns().get(pawnIndex);
        if (pawn.getTeam() != match.getPlayerTurn()) {
            return false;
        }

        pawn.applyForce(force);
        clearAiming();
        match.changeTurn();
        pendingEvents.add(DomainEvent.TURN_CHANGED);
        inputDelta.markAimingChanged();
        return true;
    }

    private void clearAiming() {
        aim.clear();
        inputDelta.markAimingChanged();
    }

    public void startAiming(double x, double y) {
        if (!physics.isEverythingStopped(match.getPawns(), match.getBall())) {
            return;
        }
        if (isMultiplayerGame() && match.getPlayerTurn() != getLocalTeam()) {
            return;
        }

        if (aim.selectPawnAt(match.getPawns(), x, y, match.getPlayerTurn())) {
            inputDelta.markAimingChanged();
        }
    }

    public void updateMousePosition(double x, double y) {
        if (aim.aimTo(x, y)) {
            inputDelta.markAimingChanged();
        }
    }

    public ReadOnlyPawn getAimingPawn() { return aim.getSelectedPawn(); }

    public double getArrowX() { return aim.getArrowX(); }

    public double getArrowY() { return aim.getArrowY(); }

    public List<? extends ReadOnlyPawn> getPawns() { return List.copyOf(match.getPawns()); }

    public int getTeamScore(int team) { return match.getTeamScore(team); }

    public GameState getGameState() { return gameState; }

    public void dismissGoal() {
        if (isMultiplayerGame() && !isLocalPlayerHost()) {
            return;
        }

        match.resetPitch();
        lastPhysicsActive = false;
        pendingEvents.add(DomainEvent.MATCH_RESET);
        transitionTo(GameState.PLAYING);
        sendGameStateQuietly();
    }

    public void scoreGoal(int team) {
        if (isMultiplayerGame() && !isLocalPlayerHost()) {
            return;
        }

        match.updateScore(team);
        pendingEvents.add(DomainEvent.SCORE_CHANGED);
        transitionTo(GameState.GOAL_SCORED);
    }

    public void quitToMenu() {
        lobbyFlow.quitToMenu();
    }

    private void transitionTo(GameState nextState) {
        gameState = nextState;
    }

    @Override
    public int getCurrentTurn() {
        return match.getPlayerTurn();
    }

    public void updateActualMousePosition(double x, double y) {
        aim.setMousePosition(x, y);
        inputDelta.markMouseMoved();
    }

    @Override
    public double getActualMouseX() { return aim.getMouseX(); }

    @Override
    public double getActualMouseY() { return aim.getMouseY(); }

    @Override
    public boolean isEverythingStopped() {
        return physics.isEverythingStopped(match.getPawns(), match.getBall());
    }

    @Override
    public String getTeamPawnFlagCode(int team) {
        return lobbyPresenter.getTeamPawnFlagCode(team);
    }

    @Override
    public List<DiscoveredHost> getDiscoveredHosts() {
        return lobbyPresenter.getDiscoveredHosts();
    }

    @Override
    public DiscoveredHost getJoinedHost() {
        return lobbyPresenter.getJoinedHost();
    }

    @Override
    public boolean isJoinPending() {
        return lobbyFlow.isJoinPending();
    }

    @Override
    public String getJoinStatusMessage() {
        return lobbyFlow.getJoinStatusMessage();
    }

    private boolean isMultiplayerGame() {
        return lobbyManager.getLocalSide().isPresent();
    }

    private int getLocalTeam() {
        return isLocalPlayerHost() ? 1 : 2;
    }

    @Override
    public boolean isLocalPlayerHost() {
        return lobbyPresenter.isLocalPlayerHost();
    }

    @Override
    public String getLocalPlayerName() {
        return lobbyPresenter.getLocalPlayerName();
    }

    @Override
    public String getLocalPlayerFlagName() {
        return lobbyPresenter.getLocalPlayerFlagName();
    }

    @Override
    public String getLocalPlayerFlagCode() {
        return lobbyPresenter.getLocalPlayerFlagCode();
    }

    @Override
    public boolean hasOpponent() {
        return lobbyPresenter.hasOpponent();
    }

    @Override
    public String getOpponentName() {
        return lobbyPresenter.getOpponentName();
    }

    @Override
    public String getOpponentFlagName() {
        return lobbyPresenter.getOpponentFlagName();
    }

    @Override
    public String getOpponentFlagCode() {
        return lobbyPresenter.getOpponentFlagCode();
    }

    @Override
    public boolean isLocalPlayerReady() {
        return lobbyPresenter.isLocalPlayerReady();
    }

    @Override
    public boolean isOpponentReady() {
        return lobbyPresenter.isOpponentReady();
    }

    @Override
    public boolean canStartGame() {
        return lobbyPresenter.canStartGame();
    }
}
