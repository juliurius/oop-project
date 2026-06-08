package pl.edu.tcs.tcsball.net.discovery;

import pl.edu.tcs.tcsball.model.lobby.LobbyState;

public class DiscoveryMessage {
    private String lobbyId;
    private String hostName;
    private int gamePort;
    private int playersCount;
    private LobbyState lobbyState;

    // TODO: opisac wiadomosc rozglaszana przez hosta w LAN.

    public String getLobbyId() {
        // TODO: zwrocic id lobby.
        throw new UnsupportedOperationException("TODO");
    }

    public String getHostName() {
        // TODO: zwrocic nazwe hosta.
        throw new UnsupportedOperationException("TODO");
    }

    public int getGamePort() {
        // TODO: zwrocic port gry.
        throw new UnsupportedOperationException("TODO");
    }

    public int getPlayersCount() {
        // TODO: zwrocic liczbe graczy.
        throw new UnsupportedOperationException("TODO");
    }

    public LobbyState getLobbyState() {
        // TODO: zwrocic stan lobby.
        throw new UnsupportedOperationException("TODO");
    }
}
