package pl.edu.tcs.tcsball.net.discovery;

import pl.edu.tcs.tcsball.model.lobby.LobbyState;

import java.util.Objects;

public record DiscoveredHost(
        String lobbyId,
        String hostName,
        String hostAddress,
        int gamePort,
        int playersCount,
        LobbyState lobbyState,
        long lastSeenMillis
) {
    private static final int MAX_PORT = 65_535;

    public DiscoveredHost {
        lobbyId = requireText(lobbyId, "lobbyId");
        hostName = requireText(hostName, "hostName");
        hostAddress = requireText(hostAddress, "hostAddress");
        gamePort = requirePort(gamePort);
        playersCount = requirePlayersCount(playersCount);
        lobbyState = Objects.requireNonNull(lobbyState, "lobbyState");
    }

    public DiscoveredHost(String lobbyId, String hostName, String hostAddress,
                          int gamePort, int playersCount, LobbyState lobbyState) {
        this(lobbyId, hostName, hostAddress, gamePort, playersCount, lobbyState, System.currentTimeMillis());
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
            case CLOSED -> "zamkniete";
        };
    }

    public boolean isJoinable() {
        return lobbyState == LobbyState.WAITING_FOR_PLAYER
                || lobbyState == LobbyState.WAITING_FOR_READY;
    }

    public boolean isExpired(long nowMillis, long timeoutMillis) {
        if (timeoutMillis < 0) {
            throw new IllegalArgumentException("timeoutMillis cannot be negative");
        }
        return nowMillis - lastSeenMillis > timeoutMillis;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value;
    }

    private static int requirePort(int port) {
        if (port <= 0 || port > MAX_PORT) {
            throw new IllegalArgumentException("gamePort must be between 1 and " + MAX_PORT);
        }
        return port;
    }

    private static int requirePlayersCount(int playersCount) {
        if (playersCount < 0) {
            throw new IllegalArgumentException("playersCount cannot be negative");
        }
        return playersCount;
    }
}
