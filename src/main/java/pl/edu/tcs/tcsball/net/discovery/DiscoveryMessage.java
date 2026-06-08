package pl.edu.tcs.tcsball.net.discovery;

import pl.edu.tcs.tcsball.model.lobby.LobbyState;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public record DiscoveryMessage(
        String lobbyId,
        String hostName,
        int gamePort,
        int playersCount,
        LobbyState lobbyState
) {
    private static final String PREFIX = "TCSBALL_DISCOVERY";
    private static final String VERSION = "1";
    private static final String SEPARATOR = "|";
    private static final int EXPECTED_PARTS = 7;
    private static final int MAX_PORT = 65_535;

    public DiscoveryMessage {
        lobbyId = requireText(lobbyId, "lobbyId");
        hostName = requireText(hostName, "hostName");
        gamePort = requirePort(gamePort);
        playersCount = requirePlayersCount(playersCount);
        lobbyState = Objects.requireNonNull(lobbyState, "lobbyState");
    }

    public String toPayload() {
        return String.join(SEPARATOR,
                PREFIX,
                VERSION,
                encode(lobbyId),
                encode(hostName),
                Integer.toString(gamePort),
                Integer.toString(playersCount),
                lobbyState.name());
    }

    public DiscoveredHost toDiscoveredHost(String hostAddress, long lastSeenMillis) {
        return new DiscoveredHost(
                lobbyId,
                hostName,
                hostAddress,
                gamePort,
                playersCount,
                lobbyState,
                lastSeenMillis
        );
    }

    public static DiscoveryMessage fromPayload(String payload) {
        String[] parts = requireText(payload, "payload").split("\\|", -1);
        if (parts.length != EXPECTED_PARTS || !PREFIX.equals(parts[0]) || !VERSION.equals(parts[1])) {
            throw new IllegalArgumentException("Invalid discovery message");
        }

        return new DiscoveryMessage(
                decode(parts[2]),
                decode(parts[3]),
                parseInt(parts[4], "gamePort"),
                parseInt(parts[5], "playersCount"),
                LobbyState.valueOf(parts[6])
        );
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

    private static int parseInt(String value, String fieldName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid " + fieldName + ": " + value, exception);
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
