package pl.edu.tcs.tcsball.controller;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.model.*;
import pl.edu.tcs.tcsball.model.formation.FormationFactory;
import pl.edu.tcs.tcsball.model.lobby.Lobby;
import pl.edu.tcs.tcsball.model.lobby.LobbyPlayer;
import pl.edu.tcs.tcsball.model.player.FlagCatalog;
import pl.edu.tcs.tcsball.model.player.PlayerFlag;
import pl.edu.tcs.tcsball.model.player.PlayerProfile;
import pl.edu.tcs.tcsball.model.player.PlayerSide;
import pl.edu.tcs.tcsball.net.discovery.DiscoveredHost;
import pl.edu.tcs.tcsball.net.protocol.MessageType;
import pl.edu.tcs.tcsball.net.protocol.NetworkMessage;
import pl.edu.tcs.tcsball.view.element.FlagColors;
import pl.edu.tcs.tcsball.view.element.ScoreBoardRenderer;
import pl.edu.tcs.tcsball.view.screen.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class GameManager implements LobbyView, CustomizationView {
    private static final int GAME_STATE_HEADER_FIELDS = 10;
    private static final int BODY_FIELD_COUNT = 4;

    private final Match match;
    private final PhysicsEngine physics;
    private final LobbyManager lobbyManager = new LobbyManager();

    private final FormationFactory formationFactory = new FormationFactory();
    private final CustomizationManager customization;

    private GameState gameState = GameState.MENU;

    private Pawn selectedPawn = null;
    private final Vector2D tensionVector = new Vector2D(0, 0);
    private double mouseX = 0, mouseY = 0;

    private final EnumSet<DomainEvent> pendingEvents = EnumSet.noneOf(DomainEvent.class);
    private final InputDelta inputDelta = new InputDelta();

    private final List<DiscoveredHost> discoveredHosts = new ArrayList<>();
    private DiscoveredHost joinedHost = null;
    private long lastGameStateSentMillis = 0;

    public GameManager(double width, double height) {
        FlagCatalog flags = new FlagCatalog();
        PlayerProfile defaultProfile = CustomizationManager.defaultProfile(flags, formationFactory);

        customization = new CustomizationManager(defaultProfile, flags.all(), formationFactory.getAvailableIds());
        match = new Match(formationFactory, defaultProfile, defaultProfile);
        physics = new PhysicsEngine(width, height);
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
            boolean forceSync = pendingEvents.contains(DomainEvent.SCORE_CHANGED)
                    || pendingEvents.contains(DomainEvent.TURN_CHANGED);
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

        boolean changed = CustomizationScreen.handleClick(x, y);   // ustawia fokus pola nazwy

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

    // --- Akcje customizacji: cyklowanie i edycja nazwy delegują do CustomizationManager ---

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

        lobbyManager.sendToPeer(createGameStateMessage());
        lastGameStateSentMillis = now;
    }

    private NetworkMessage createGameStateMessage() {
        List<String> fields = new ArrayList<>();
        fields.add(gameState.name());
        fields.add(Integer.toString(match.getPlayerTurn()));
        fields.add(Integer.toString(match.getTeamScore(1)));
        fields.add(Integer.toString(match.getTeamScore(2)));

        Ball ball = match.getBall();
        addBodyFields(fields, ball);
        fields.add(Double.toString(ball.getSpin()));
        fields.add(Double.toString(ball.getAngle()));

        for (Pawn pawn : match.getPawns()) {
            addBodyFields(fields, pawn);
        }

        return new NetworkMessage(MessageType.GAME_STATE, fields);
    }

    private boolean applyGameState(NetworkMessage message) {
        List<String> fields = message.fields();
        int expectedFields = GAME_STATE_HEADER_FIELDS + match.getPawns().size() * BODY_FIELD_COUNT;
        if (fields.size() < expectedFields) {
            throw new IllegalArgumentException("Invalid game state message");
        }

        GameState receivedState = GameState.valueOf(fields.get(0));
        int oldTurn = match.getPlayerTurn();
        int oldScore1 = match.getTeamScore(1);
        int oldScore2 = match.getTeamScore(2);

        match.setPlayerTurn(Integer.parseInt(fields.get(1)));
        match.setScores(Integer.parseInt(fields.get(2)), Integer.parseInt(fields.get(3)));

        int index = applyBodyFields(match.getBall(), fields, 4);
        match.getBall().setSpin(Double.parseDouble(fields.get(index++)));
        match.getBall().setAngle(Double.parseDouble(fields.get(index++)));

        for (Pawn pawn : match.getPawns()) {
            index = applyBodyFields(pawn, fields, index);
        }

        selectedPawn = null;
        tensionVector.setX(0);
        tensionVector.setY(0);
        if (oldTurn != match.getPlayerTurn()) {
            pendingEvents.add(DomainEvent.TURN_CHANGED);
        }
        if (oldScore1 != match.getTeamScore(1) || oldScore2 != match.getTeamScore(2)) {
            pendingEvents.add(DomainEvent.SCORE_CHANGED);
        }
        transitionTo(receivedState);
        inputDelta.markAimingChanged();
        return true;
    }

    private void addBodyFields(List<String> fields, PhysicsBody body) {
        fields.add(Double.toString(body.getPosition().getX()));
        fields.add(Double.toString(body.getPosition().getY()));
        fields.add(Double.toString(body.getVelocity().getX()));
        fields.add(Double.toString(body.getVelocity().getY()));
    }

    private int applyBodyFields(PhysicsBody body, List<String> fields, int index) {
        body.setPosition(new Vector2D(
                Double.parseDouble(fields.get(index)),
                Double.parseDouble(fields.get(index + 1))
        ));
        body.setVelocity(new Vector2D(
                Double.parseDouble(fields.get(index + 2)),
                Double.parseDouble(fields.get(index + 3))
        ));
        return index + BODY_FIELD_COUNT;
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
        if (selectedPawn == null) return;

        int pawnIndex = match.getPawns().indexOf(selectedPawn);
        Vector2D shotForce = new Vector2D(tensionVector.getX(), tensionVector.getY());

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
        selectedPawn = null;
        tensionVector.setX(0);
        tensionVector.setY(0);
        inputDelta.markAimingChanged();
    }

    public void startAiming(double x, double y) {
        if (!physics.isEverythingStopped(match.getPawns(), match.getBall())) {
            return;
        }
        if (isMultiplayerGame() && match.getPlayerTurn() != getLocalTeam()) {
            return;
        }

        List<Pawn> pawns = match.getPawns();

        for (Pawn pawn : pawns) {
            Vector2D position = pawn.getPosition();
            double pawnX = position.getX(), pawnY = position.getY(), pawnR = pawn.getRadius();
            double distance = Math.sqrt(Math.pow((pawnX - x), 2) + Math.pow((pawnY - y), 2));

            if (pawnR >= distance && pawn.getTeam() == match.getPlayerTurn()) {
                selectedPawn = pawn;
                inputDelta.markAimingChanged();
                break;
            }
        }
    }

    public void updateMousePosition(double x, double y) {
        if (selectedPawn == null) return;

        mouseX = x;
        mouseY = y;

        double newX = selectedPawn.getPosition().getX() - mouseX;
        double newY = selectedPawn.getPosition().getY() - mouseY;

        Vector2D newTension = new Vector2D(newX, newY);

        if (newTension.length() > GameConfig.MAX_PULL_DISTANCE) {
            newTension = newTension.normalized().multiply(GameConfig.MAX_PULL_DISTANCE);
        }

        tensionVector.setX(newTension.getX());
        tensionVector.setY(newTension.getY());
        inputDelta.markAimingChanged();
    }

    public Pawn getAimingPawn() { return selectedPawn; }

    public double getArrowX() {
        if (selectedPawn != null)
            return selectedPawn.getPosition().getX() + tensionVector.getX();
        return 0;
    }

    public double getArrowY() {
        if (selectedPawn != null)
            return selectedPawn.getPosition().getY() + tensionVector.getY();
        return 0;
    }

    public List<Pawn> getPawns() { return match.getPawns(); }

    public int getTeamScore(int team) { return match.getTeamScore(team); }

    public GameState getGameState() { return gameState; }

    public void dismissGoal() {
        if (isMultiplayerGame() && !isLocalPlayerHost()) {
            return;
        }

        match.resetPitch();
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
        this.mouseX = x;
        this.mouseY = y;
        inputDelta.markMouseMoved();
    }

    @Override
    public double getActualMouseX() { return mouseX; }

    @Override
    public double getActualMouseY() { return mouseY; }

    @Override
    public boolean isEverythingStopped() {
        return physics.isEverythingStopped(match.getPawns(), match.getBall());
    }

    @Override
    public String getTeamPawnColor(int team) {
        PlayerProfile profile = team == 1 ? match.getLeftProfile() : match.getRightProfile();
        String code = profile.pawnFlag().code();

        if (team == 2 && sameFlag(match.getLeftProfile(), match.getRightProfile())) {
            return FlagColors.localPlayTeam2Color(code);
        }
        return FlagColors.forCode(code);
    }

    @Override
    public String getTeamPawnInnerColor(int team) {
        return FlagColors.innerForHex(getTeamPawnColor(team));
    }

    private static boolean sameFlag(PlayerProfile left, PlayerProfile right) {
        return left.pawnFlag().code().equals(right.pawnFlag().code());
    }

    @Override
    public List<DiscoveredHost> getDiscoveredHosts() {
        return List.copyOf(discoveredHosts);
    }

    @Override
    public DiscoveredHost getJoinedHost() {
        return joinedHost;
    }

    private boolean isMultiplayerGame() {
        return lobbyManager.getLocalSide().isPresent();
    }

    private int getLocalTeam() {
        return isLocalPlayerHost() ? 1 : 2;
    }

    @Override
    public boolean isLocalPlayerHost() {
        return lobbyManager.getLocalSide()
                .map(side -> side == PlayerSide.LEFT)
                .orElse(gameState == GameState.HOST_LOBBY);
    }

    @Override
    public String getLocalPlayerName() {
        return getLocalProfile().name();
    }

    @Override
    public String getLocalPlayerFlagName() {
        return getLocalProfile().pawnFlag().displayName();
    }

    @Override
    public String getLocalPlayerFlagColor() {
        return CustomizationScreen.flagColor(getLocalProfile().pawnFlag().code());
    }

    @Override
    public boolean hasOpponent() {
        if (isLocalPlayerHost()) {
            return lobbyManager.getLobby()
                    .flatMap(Lobby::getGuest)
                    .isPresent();
        }
        return getOpponentPlayer() != null || joinedHost != null;
    }

    @Override
    public String getOpponentName() {
        LobbyPlayer opponent = getOpponentPlayer();
        if (opponent != null) {
            return opponent.getProfile().name();
        }
        if (gameState == GameState.CLIENT_LOBBY && joinedHost != null) {
            return joinedHost.getHostName();
        }
        return null;
    }

    @Override
    public String getOpponentFlagName() {
        LobbyPlayer opponent = getOpponentPlayer();
        if (opponent != null) {
            return opponent.getProfile().pawnFlag().displayName();
        }
        return gameState == GameState.CLIENT_LOBBY ? "?" : "";
    }

    @Override
    public String getOpponentFlagColor() {
        LobbyPlayer opponent = getOpponentPlayer();
        if (opponent != null) {
            return CustomizationScreen.flagColor(opponent.getProfile().pawnFlag().code());
        }
        return gameState == GameState.CLIENT_LOBBY ? "#4682b4" : "#666666";
    }

    @Override
    public boolean isLocalPlayerReady() {
        Lobby lobby = lobbyManager.getLobby().orElse(null);
        PlayerSide side = lobbyManager.getLocalSide().orElse(null);
        if (lobby == null || side == null) {
            return false;
        }
        return side == PlayerSide.LEFT
                ? lobby.getHost().isReady()
                : lobby.getGuest().map(LobbyPlayer::isReady).orElse(false);
    }

    @Override
    public boolean isOpponentReady() {
        LobbyPlayer opponent = getOpponentPlayer();
        return opponent != null && opponent.isReady();
    }

    @Override
    public boolean canStartGame() {
        return gameState == GameState.HOST_LOBBY && lobbyManager.canStartGame();
    }

    private PlayerProfile getLocalProfile() {
        Lobby lobby = lobbyManager.getLobby().orElse(null);
        PlayerSide side = lobbyManager.getLocalSide().orElse(null);
        if (lobby == null || side == null) {
            return customization.getCurrentProfile();
        }
        if (side == PlayerSide.LEFT) {
            return lobby.getHost().getProfile();
        }
        return lobby.getGuest()
                .map(LobbyPlayer::getProfile)
                .orElse(customization.getCurrentProfile());
    }

    private LobbyPlayer getOpponentPlayer() {
        Lobby lobby = lobbyManager.getLobby().orElse(null);
        PlayerSide side = lobbyManager.getLocalSide().orElse(null);
        if (lobby == null || side == null) {
            return null;
        }
        if (side == PlayerSide.LEFT) {
            return lobby.getGuest().orElse(null);
        }
        return lobby.getHost();
    }
}
