package pl.edu.tcs.tcsball.controller;

import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.model.*;
import pl.edu.tcs.tcsball.model.lobby.LobbyState;
import pl.edu.tcs.tcsball.net.discovery.DiscoveredHost;
import pl.edu.tcs.tcsball.view.element.ScoreBoardRenderer;
import pl.edu.tcs.tcsball.view.screen.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class GameManager implements LobbyView {
    private final Match match;
    private final PhysicsEngine physics;

    private GameState gameState = GameState.MENU;

    private Pawn selectedPawn = null;
    private final Vector2D tensionVector = new Vector2D(0, 0);
    private double mouseX = 0, mouseY = 0;

    private final EnumSet<DomainEvent> pendingEvents = EnumSet.noneOf(DomainEvent.class);
    private final InputDelta inputDelta = new InputDelta();

    // MOCK: zastąpić LanHostScanner + LobbyManager przy prawdziwym multiplayerze
    private final List<DiscoveredHost> discoveredHosts = new ArrayList<>();
    // MOCK: ustawiane lokalnie w joinHost(); docelowo po GameClient.connect()
    private DiscoveredHost joinedHost = null;
    // MOCK: przełącznik wariantów listy testowej w refreshDiscoveredHosts()
    private int mockHostVariant = 0;

    public GameManager(double width, double height) {
        match = new Match();
        physics = new PhysicsEngine(width, height);
    }

    public FrameDelta update(double deltaTime) {
        if (gameState != GameState.PLAYING) {
            return FrameDelta.idle();
        }

        FrameDelta delta = physics.update(match.getPawns(), match.getBall(), deltaTime);

        if (physics.wasGoalScored()) {
            scoreGoal(physics.getLastGoalScoredByTeam());
        }

        return delta;
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

        if (CustomizationScreen.handleClick(x, y) || CustomizationScreen.handleArrowClick(x, y)) {
            inputDelta.markMouseMoved();
        }
    }

    public void handleCustomizationKey(javafx.scene.input.KeyEvent event) {
        if (CustomizationScreen.handleKey(event)) {
            inputDelta.markMouseMoved();
        }
    }

    public void handleHostLobbyClick(double x, double y) {
        if (HostLobbyScreen.isBackButtonHit(x, y)) {
            quitToMenu();
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
        match.resetGame();
        pendingEvents.add(DomainEvent.MATCH_RESET);
        transitionTo(GameState.PLAYING);
    }

    // MOCK: docelowo LobbyManager.hostLobby() + GameHostServer + LanHostAnnouncer
    public void openHostLobby() {
        transitionTo(GameState.HOST_LOBBY);
    }

    public void openJoinLobby() {
        joinedHost = null;
        refreshDiscoveredHosts();
        transitionTo(GameState.JOIN_LOBBY);
    }

    // MOCK: docelowo LanHostScanner.getDiscoveredHosts()
    public void refreshDiscoveredHosts() {
        mockHostVariant = (mockHostVariant + 1) % 2;
        discoveredHosts.clear();
        discoveredHosts.addAll(createMockHosts(mockHostVariant));
        inputDelta.markMouseMoved();
    }

    // MOCK: sztywna lista hostów do testów UI — usunąć po podpięciu sieci
    private List<DiscoveredHost> createMockHosts(int variant) {
        if (variant == 0) {
            return List.of(
                    new DiscoveredHost("lobby-1", "Janek", "192.168.0.10", 7777, 1, LobbyState.WAITING_FOR_PLAYER),
                    new DiscoveredHost("lobby-2", "TCS-Room", "192.168.0.22", 7777, 2, LobbyState.WAITING_FOR_READY),
                    new DiscoveredHost("lobby-3", "QuickMatch", "192.168.0.5", 7777, 1, LobbyState.WAITING_FOR_PLAYER)
            );
        }
        return List.of(
                new DiscoveredHost("lobby-4", "Kuba", "192.168.0.15", 7777, 1, LobbyState.WAITING_FOR_PLAYER),
                new DiscoveredHost("lobby-5", "PO-Projekt", "192.168.0.30", 7777, 2, LobbyState.IN_GAME)
        );
    }

    // MOCK: docelowo LobbyManager.joinLobby() + GameClient.connect()
    public void joinHost(int index) {
        if (index < 0 || index >= discoveredHosts.size()) {
            return;
        }

        DiscoveredHost host = discoveredHosts.get(index);
        if (!host.isJoinable()) {
            return;
        }

        joinedHost = host;
        transitionTo(GameState.CLIENT_LOBBY);
    }

    // MOCK: docelowo LobbyManager.leaveLobby() + zamknięcie połączenia
    public void leaveClientLobby() {
        joinedHost = null;
        quitToMenu();
    }

    public void openCustomization() {
        transitionTo(GameState.CUSTOMIZATION);
    }

    public Ball getBall() { return match.getBall(); }

    public void shootPawn() {
        if (selectedPawn == null) return;

        selectedPawn.applyForce(tensionVector);
        selectedPawn = null;
        tensionVector.setX(0);
        tensionVector.setY(0);

        match.changeTurn();
        pendingEvents.add(DomainEvent.TURN_CHANGED);
        inputDelta.markAimingChanged();
    }

    public void startAiming(double x, double y) {
        if (!physics.isEverythingStopped(match.getPawns(), match.getBall())) {
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
        match.resetPitch();
        pendingEvents.add(DomainEvent.MATCH_RESET);
        transitionTo(GameState.PLAYING);
    }

    public void scoreGoal(int team) {
        match.updateScore(team);
        pendingEvents.add(DomainEvent.SCORE_CHANGED);
        transitionTo(GameState.GOAL_SCORED);
    }

    public void quitToMenu () {
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
    public List<DiscoveredHost> getDiscoveredHosts() {
        return List.copyOf(discoveredHosts);
    }

    @Override
    public DiscoveredHost getJoinedHost() {
        return joinedHost;
    }
}
