package pl.edu.tcs.tcsball.controller;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.model.*;
import pl.edu.tcs.tcsball.model.formation.FormationFactory;
import pl.edu.tcs.tcsball.model.lobby.Lobby;
import pl.edu.tcs.tcsball.model.player.FlagCatalog;
import pl.edu.tcs.tcsball.model.player.PlayerFlag;
import pl.edu.tcs.tcsball.model.player.PlayerProfile;
import pl.edu.tcs.tcsball.net.discovery.DiscoveredHost;
import pl.edu.tcs.tcsball.net.protocol.MessageType;
import pl.edu.tcs.tcsball.net.protocol.NetworkMessage;
import pl.edu.tcs.tcsball.view.element.ScoreBoardRenderer;
import pl.edu.tcs.tcsball.view.screen.*;

import java.io.IOException;
import java.util.ArrayList;
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
    private final LobbyPresenter lobbyPresenter;

    private GameState gameState = GameState.MENU;

    private final AimController aim = new AimController();

    private final EnumSet<DomainEvent> pendingEvents = EnumSet.noneOf(DomainEvent.class);
    private final InputDelta inputDelta = new InputDelta();

    private final List<DiscoveredHost> discoveredHosts = new ArrayList<>();
    private DiscoveredHost joinedHost = null;
    private long lastGameStateSentMillis = 0;
    private boolean lastPhysicsActive = false;

    public GameManager(double width, double height) {
        FlagCatalog flags = new FlagCatalog();
        PlayerProfile defaultProfile = CustomizationManager.defaultProfile(flags, formationFactory);

        customization = new CustomizationManager(defaultProfile, flags.all(), formationFactory.getAvailableIds());
        match = new Match(formationFactory, defaultProfile, defaultProfile);
        physics = new PhysicsEngine(width, height);
        lobbyPresenter = new LobbyPresenter(lobbyManager, customization, match, discoveredHosts,
                () -> gameState, () -> joinedHost);
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
                syncDiscoveredHosts();
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

    public void handleMenuClick(double x, double y) {
        if (MenuScreen.isButtonHit(x, y, MenuScreen.LOCAL_PLAY_BTN_Y)) {
            startLocalGame();
        } else if (MenuScreen.isButtonHit(x, y, MenuScreen.HOST_BTN_Y)) {
            openHostLobby();
        } else if (MenuScreen.isButtonHit(x, y, MenuScreen.JOIN_BTN_Y)) {
            openJoinLobby();
        } else if (MenuScreen.isButtonHit(x, y, MenuScreen.CUSTOMIZATION_BTN_Y)) {
            openCustomization();
        }
    }

    public void handleCustomizationClick(double x, double y) {
        if (CustomizationScreen.isBackButtonHit(x, y)) {
            quitToMenu();
            return;
        }

        boolean changed = CustomizationScreen.handleClick(x, y);

        if (CustomizationScreen.isPrevArrowHit(x, y, CustomizationScreen.Field.FLAG)) {
            cycleFlag(-1);
            changed = true;
        } else if (CustomizationScreen.isNextArrowHit(x, y, CustomizationScreen.Field.FLAG)) {
            cycleFlag(1);
            changed = true;
        } else if (CustomizationScreen.isPrevArrowHit(x, y, CustomizationScreen.Field.FORMATION)) {
            cycleFormation(-1);
            changed = true;
        } else if (CustomizationScreen.isNextArrowHit(x, y, CustomizationScreen.Field.FORMATION)) {
            cycleFormation(1);
            changed = true;
        }

        if (changed) {
            inputDelta.markMouseMoved();
        }
    }

    public void handleCustomizationKey(KeyEvent event) {
        if (!CustomizationScreen.isNameFieldFocused()) {
            return;
        }

        if (event.getCode() == KeyCode.BACK_SPACE) {
            backspaceName();
            inputDelta.markMouseMoved();
            return;
        }

        if (event.getEventType() != KeyEvent.KEY_TYPED) {
            return;
        }

        String text = event.getCharacter();
        if (text == null || text.isEmpty() || text.charAt(0) < ' ') {
            return;
        }

        char ch = text.charAt(0);
        if (!Character.isLetterOrDigit(ch) && ch != ' ' && ch != '-' && ch != '_') {
            return;
        }

        typeNameChar(ch);
        inputDelta.markMouseMoved();
    }

    public void cycleFlag(int direction) {
        List<PlayerFlag> flags = customization.getAvailableFlags();
        int index = flags.indexOf(customization.getCurrentProfile().pawnFlag());
        customization.setPawnFlag(flags.get(Math.floorMod(index + direction, flags.size())));
    }

    public void cycleFormation(int direction) {
        List<String> ids = customization.getAvailableFormationIds();
        int index = ids.indexOf(customization.getCurrentProfile().formationId());
        customization.setFormationId(ids.get(Math.floorMod(index + direction, ids.size())));
    }

    public void typeNameChar(char ch) {
        String name = customization.getCurrentProfile().name();
        if (name.length() < CustomizationScreen.NAME_MAX_LENGTH) {
            customization.setName(name + ch);
        }
    }

    public void backspaceName() {
        String name = customization.getCurrentProfile().name();
        if (!name.isEmpty()) {
            customization.setName(name.substring(0, name.length() - 1));
        }
    }

    // --- CustomizationView: odczyt dla ekranu customizacji ---

    @Override
    public String getPlayerName() {
        return customization.getCurrentProfile().name();
    }

    @Override
    public String getCurrentFlagCode() {
        return customization.getCurrentProfile().pawnFlag().code();
    }

    @Override
    public String getCurrentFlagName() {
        return customization.getCurrentProfile().pawnFlag().displayName();
    }

    @Override
    public String getCurrentFormationName() {
        return formationFactory.getDefinition(customization.getCurrentProfile().formationId()).displayName();
    }

    public void handleHostLobbyClick(double x, double y) {
        if (HostLobbyScreen.isBackButtonHit(x, y)) {
            leaveLobby();
            return;
        }

        if (HostLobbyScreen.isReadyButtonHit(x, y)) {
            toggleLocalReady();
            return;
        }

        if (HostLobbyScreen.isStartButtonHit(x, y, canStartGame())) {
            startMultiplayerFromLobby();
        }
    }

    public void handleJoinLobbyClick(double x, double y) {
        if (JoinLobbyScreen.isBackButtonHit(x, y)) {
            quitToMenu();
            return;
        }

        if (JoinLobbyScreen.isRefreshButtonHit(x, y)) {
            refreshDiscoveredHosts();
            return;
        }

        int index = JoinLobbyScreen.hostIndexAt(x, y, discoveredHosts.size());
        if (index >= 0) {
            joinHost(index);
        }
    }

    public void handleClientLobbyClick(double x, double y) {
        if (ClientLobbyScreen.isBackButtonHit(x, y)) {
            leaveClientLobby();
            return;
        }

        if (ClientLobbyScreen.isReadyButtonHit(x, y)) {
            toggleLocalReady();
        }
    }

    public boolean handleBackToMenuClick(double x, double y) {
        if (x >= ScoreBoardRenderer.BACK_BTN_X && x <= ScoreBoardRenderer.BACK_BTN_X + ScoreBoardRenderer.BACK_BTN_WIDTH &&
                y >= ScoreBoardRenderer.BACK_BTN_Y && y <= ScoreBoardRenderer.BACK_BTN_Y + ScoreBoardRenderer.BACK_BTN_HEIGHT) {

            quitToMenu();
            return true;
        }
        return false;
    }

    public void startLocalGame() {
        PlayerProfile me = customization.getCurrentProfile();
        match.setProfiles(me, me);
        match.resetGame();
        pendingEvents.add(DomainEvent.MATCH_RESET);
        transitionTo(GameState.PLAYING);
    }

    public void openHostLobby() {
        try {
            joinedHost = null;
            discoveredHosts.clear();
            lobbyManager.hostLobby(customization.getCurrentProfile());
            transitionTo(GameState.HOST_LOBBY);
        } catch (IOException exception) {
            quitToMenu();
        }
    }

    public void openJoinLobby() {
        joinedHost = null;
        discoveredHosts.clear();
        try {
            lobbyManager.leaveLobby();
            lobbyManager.startScanningHosts();
            syncDiscoveredHosts();
        } catch (IOException ignored) {
            // Gdy skanowanie LAN sie nie uda, ekran pokaze pusta liste.
        }
        transitionTo(GameState.JOIN_LOBBY);
    }

    public void refreshDiscoveredHosts() {
        syncDiscoveredHosts();
        inputDelta.markMouseMoved();
    }

    public void joinHost(int index) {
        if (index < 0 || index >= discoveredHosts.size()) {
            return;
        }

        DiscoveredHost host = discoveredHosts.get(index);
        if (!host.isJoinable()) {
            return;
        }

        joinedHost = host;
        try {
            lobbyManager.joinLobby(host, customization.getCurrentProfile());
            transitionTo(GameState.CLIENT_LOBBY);
        } catch (IOException exception) {
            joinedHost = null;
        }
    }

    public void leaveClientLobby() {
        leaveLobby();
    }

    private void leaveLobby() {
        lobbyManager.leaveLobby();
        joinedHost = null;
        discoveredHosts.clear();
        transitionTo(GameState.MENU);
    }

    private void toggleLocalReady() {
        try {
            lobbyManager.setLocalReady(!isLocalPlayerReady());
            inputDelta.markMouseMoved();
        } catch (IOException exception) {
            leaveLobby();
        }
    }

    private void startMultiplayerFromLobby() {
        if (!lobbyManager.canStartGame()) {
            return;
        }

        try {
            lobbyManager.startGame();
            beginMultiplayerMatch();
        } catch (IOException exception) {
            leaveLobby();
        }
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

    private void syncDiscoveredHosts() {
        List<DiscoveredHost> currentHosts = lobbyManager.getDiscoveredHosts();
        if (currentHosts.equals(discoveredHosts)) {
            return;
        }

        discoveredHosts.clear();
        discoveredHosts.addAll(currentHosts);
        inputDelta.markMouseMoved();
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

    public Ball getBall() { return match.getBall(); }

    public void shootPawn() {
        if (!aim.hasSelection()) return;

        int pawnIndex = match.getPawns().indexOf(aim.getSelectedPawn());
        Vector2D shotForce = aim.getTension();

        if (isMultiplayerGame() && !isLocalPlayerHost()) {
            try {
                lobbyManager.sendToPeer(NetworkMessage.of(MessageType.SHOT,
                        Integer.toString(pawnIndex),
                        Double.toString(shotForce.getX()),
                        Double.toString(shotForce.getY())
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

    public Pawn getAimingPawn() { return aim.getSelectedPawn(); }

    public double getArrowX() { return aim.getArrowX(); }

    public double getArrowY() { return aim.getArrowY(); }

    public List<Pawn> getPawns() { return match.getPawns(); }

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

    public void quitToMenu () {
        lobbyManager.leaveLobby();
        joinedHost = null;
        discoveredHosts.clear();
        transitionTo(GameState.MENU);
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
    public String getTeamPawnColor(int team) {
        return lobbyPresenter.getTeamPawnColor(team);
    }

    @Override
    public String getTeamPawnInnerColor(int team) {
        return lobbyPresenter.getTeamPawnInnerColor(team);
    }

    @Override
    public List<DiscoveredHost> getDiscoveredHosts() {
        return lobbyPresenter.getDiscoveredHosts();
    }

    @Override
    public DiscoveredHost getJoinedHost() {
        return lobbyPresenter.getJoinedHost();
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
    public String getLocalPlayerFlagColor() {
        return lobbyPresenter.getLocalPlayerFlagColor();
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
    public String getOpponentFlagColor() {
        return lobbyPresenter.getOpponentFlagColor();
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
