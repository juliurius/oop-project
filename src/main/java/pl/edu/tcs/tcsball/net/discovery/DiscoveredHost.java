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
