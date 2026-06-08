package pl.edu.tcs.tcsball.net.discovery;

import pl.edu.tcs.tcsball.model.lobby.LobbyState;

public class DiscoveredHost {
    private String lobbyId;
    private String hostName;
    private String hostAddress;
    private int gamePort;
    private int playersCount;
    private LobbyState lobbyState;
    private long lastSeenMillis;

    // TODO: trzymac id lobby.
    // TODO: trzymac nazwe hosta.
    // TODO: trzymac adres hosta.
    // TODO: trzymac port gry.
    // TODO: trzymac liczbe graczy i stan lobby.

    public String getLobbyId() {
        // TODO: zwrocic id lobby.
        throw new UnsupportedOperationException("TODO");
    }

    public String getHostName() {
        // TODO: zwrocic nazwe hosta.
        throw new UnsupportedOperationException("TODO");
    }

    public String getHostAddress() {
        // TODO: zwrocic adres hosta.
        throw new UnsupportedOperationException("TODO");
    }

    public int getGamePort() {
        // TODO: zwrocic port gry.
        throw new UnsupportedOperationException("TODO");
    }

    public LobbyState getLobbyState() {
        // TODO: zwrocic stan lobby.
        throw new UnsupportedOperationException("TODO");
    }
}
