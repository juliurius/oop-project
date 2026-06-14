package pl.edu.tcs.tcsball.controller.lobby;

import pl.edu.tcs.tcsball.controller.GameState;
import pl.edu.tcs.tcsball.controller.InputDelta;
import pl.edu.tcs.tcsball.controller.customization.CustomizationManager;

import pl.edu.tcs.tcsball.net.discovery.DiscoveredHost;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Nawigacja po ekranach lobby (host/join), maszyna stanu probu dolaczenia
 * ("pending join") oraz lista odkrytych hostow w LAN. Zmiany stanu aplikacji i
 * start meczu deleguje przez hooki ({@code transitionTo}, {@code beginMatch}) do
 * {@link GameManager}, dzieki czemu nie zna rdzenia gry ani synchronizacji sieci.
 */
public class LobbyFlowController {
    private static final long JOIN_PENDING_TIMEOUT_MILLIS = 3_000;

    private final LobbyManager lobbyManager;
    private final CustomizationManager customization;
    private final InputDelta inputDelta;
    private final Consumer<GameState> transitionTo;
    private final Runnable beginMatch;
    private final BooleanSupplier localPlayerReady;

    private final List<DiscoveredHost> discoveredHosts = new ArrayList<>();
    private DiscoveredHost joinedHost = null;
    private boolean pendingJoin = false;
    private long pendingJoinStartedMillis = 0;
    private String joinStatusMessage = null;

    public LobbyFlowController(LobbyManager lobbyManager, CustomizationManager customization, InputDelta inputDelta,
                              Consumer<GameState> transitionTo, Runnable beginMatch, BooleanSupplier localPlayerReady) {
        this.lobbyManager = lobbyManager;
        this.customization = customization;
        this.inputDelta = inputDelta;
        this.transitionTo = transitionTo;
        this.beginMatch = beginMatch;
        this.localPlayerReady = localPlayerReady;
    }

    public void openHostLobby() {
        try {
            joinedHost = null;
            discoveredHosts.clear();
            lobbyManager.hostLobby(customization.getCurrentProfile());
            transitionTo.accept(GameState.HOST_LOBBY);
        } catch (IOException exception) {
            quitToMenu();
        }
    }

    public void openJoinLobby() {
        pendingJoin = false;
        pendingJoinStartedMillis = 0;
        joinStatusMessage = null;
        joinedHost = null;
        discoveredHosts.clear();
        try {
            lobbyManager.leaveLobby();
            lobbyManager.startScanningHosts();
            syncDiscoveredHosts();
        } catch (IOException ignored) {
            // Gdy skanowanie LAN sie nie uda, ekran pokaze pusta liste.
        }
        transitionTo.accept(GameState.JOIN_LOBBY);
    }

    public void refreshDiscoveredHosts() {
        joinStatusMessage = null;
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
        joinStatusMessage = null;
        try {
            lobbyManager.joinLobby(host, customization.getCurrentProfile());
            pendingJoin = true;
            pendingJoinStartedMillis = System.currentTimeMillis();
            inputDelta.markMouseMoved();
        } catch (IOException exception) {
            joinedHost = null;
            joinStatusMessage = "Nie udało się połączyć z hostem";
            inputDelta.markMouseMoved();
        }
    }

    public void backFromJoinLobby() {
        if (pendingJoin) {
            cancelPendingJoin(null);
        } else {
            quitToMenu();
        }
    }

    public void updatePendingJoin() {
        if (!pendingJoin) {
            return;
        }

        if (lobbyManager.isGuestLobbyConfirmed()) {
            confirmJoinSuccess();
        } else if (System.currentTimeMillis() - pendingJoinStartedMillis > JOIN_PENDING_TIMEOUT_MILLIS) {
            cancelPendingJoin("Host nie odpowiedział — spróbuj ponownie");
        }
    }

    private void confirmJoinSuccess() {
        pendingJoin = false;
        pendingJoinStartedMillis = 0;
        joinStatusMessage = null;
        transitionTo.accept(GameState.CLIENT_LOBBY);
        inputDelta.markMouseMoved();
    }

    private void cancelPendingJoin(String message) {
        pendingJoin = false;
        pendingJoinStartedMillis = 0;
        joinedHost = null;
        joinStatusMessage = message;
        lobbyManager.leaveLobby();
        try {
            lobbyManager.startScanningHosts();
            syncDiscoveredHosts();
        } catch (IOException ignored) {
            // Skanowanie moze sie nie udac; ekran pokaze pusta liste.
        }
        inputDelta.markMouseMoved();
    }

    public void leaveClientLobby() {
        leaveLobby();
    }

    public void leaveLobby() {
        lobbyManager.leaveLobby();
        joinedHost = null;
        discoveredHosts.clear();
        transitionTo.accept(GameState.MENU);
    }

    public void toggleLocalReady() {
        try {
            lobbyManager.setLocalReady(!localPlayerReady.getAsBoolean());
            inputDelta.markMouseMoved();
        } catch (IOException exception) {
            leaveLobby();
        }
    }

    public void startMultiplayerFromLobby() {
        if (!lobbyManager.canStartGame()) {
            return;
        }

        try {
            lobbyManager.startGame();
            beginMatch.run();
        } catch (IOException exception) {
            leaveLobby();
        }
    }

    public void quitToMenu() {
        pendingJoin = false;
        pendingJoinStartedMillis = 0;
        joinStatusMessage = null;
        lobbyManager.leaveLobby();
        joinedHost = null;
        discoveredHosts.clear();
        transitionTo.accept(GameState.MENU);
    }

    public void syncDiscoveredHosts() {
        List<DiscoveredHost> currentHosts = lobbyManager.getDiscoveredHosts();
        if (currentHosts.equals(discoveredHosts)) {
            return;
        }

        discoveredHosts.clear();
        discoveredHosts.addAll(currentHosts);
        inputDelta.markMouseMoved();
    }

    // --- LobbyView: odczyt stanu doboru hosta ---

    public List<DiscoveredHost> getDiscoveredHosts() {
        return List.copyOf(discoveredHosts);
    }

    public DiscoveredHost getJoinedHost() {
        return joinedHost;
    }

    public boolean isJoinPending() {
        return pendingJoin;
    }

    public String getJoinStatusMessage() {
        return joinStatusMessage;
    }
}
