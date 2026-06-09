package pl.edu.tcs.tcsball.model;

import pl.edu.tcs.tcsball.net.discovery.DiscoveredHost;

import java.util.List;

public interface LobbyView extends GameView {

    // --- wybór hosta (JOIN_LOBBY) ---

    List<DiscoveredHost> getDiscoveredHosts();

    DiscoveredHost getJoinedHost();

    boolean isJoinPending();

    /** Komunikat statusu na ekranie JOIN (np. błąd); null gdy brak. */
    String getJoinStatusMessage();

    // --- aktywne lobby (HOST_LOBBY / CLIENT_LOBBY) ---
    // GameManager implementuje te metody; docelowo dane z LobbyManager + Lobby.

    boolean isLocalPlayerHost();

    String getLocalPlayerName();

    String getLocalPlayerFlagName();

    String getLocalPlayerFlagColor();

    boolean hasOpponent();

    /** null gdy {@link #hasOpponent()} == false */
    String getOpponentName();

    String getOpponentFlagName();

    String getOpponentFlagColor();

    boolean isLocalPlayerReady();

    boolean isOpponentReady();

    /** host: obaj gracze gotowi; klient: zawsze false */
    boolean canStartGame();
}
