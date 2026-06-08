package pl.edu.tcs.tcsball.net.discovery;

import pl.edu.tcs.tcsball.model.lobby.LobbyState;

public class DiscoveredHost {
    private final String lobbyId;
    private final String hostName;
    private final String hostAddress;
    private final int gamePort;
    private final int playersCount;
    private final LobbyState lobbyState;

    public DiscoveredHost(String lobbyId, String hostName, String hostAddress,
                          int gamePort, int playersCount, LobbyState lobbyState) {
        this.lobbyId = lobbyId;
        this.hostName = hostName;
        this.hostAddress = hostAddress;
        this.gamePort = gamePort;
        this.playersCount = playersCount;
        this.lobbyState = lobbyState;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public String getHostName() {
        return hostName;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public int getGamePort() {
        return gamePort;
    }

    public int getPlayersCount() {
        return playersCount;
    }

    public LobbyState getLobbyState() {
        return lobbyState;
    }

    public String getStatusLabel() {
        return switch (lobbyState) {
            case WAITING_FOR_PLAYER -> "czeka na gracza";
            case WAITING_FOR_READY -> playersCount + "/2 graczy";
            case READY_TO_START -> "gotowe do startu";
            case IN_GAME -> "w trakcie gry";
            case CLOSED -> "zamknięte";
        };
    }

    public boolean isJoinable() {
        return lobbyState == LobbyState.WAITING_FOR_PLAYER
                || lobbyState == LobbyState.WAITING_FOR_READY;
    }
}
