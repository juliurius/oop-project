package pl.edu.tcs.tcsball.controller;

import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.model.lobby.Lobby;
import pl.edu.tcs.tcsball.model.lobby.LobbyPlayer;
import pl.edu.tcs.tcsball.model.lobby.LobbyState;
import pl.edu.tcs.tcsball.model.player.PlayerFlag;
import pl.edu.tcs.tcsball.model.player.PlayerProfile;
import pl.edu.tcs.tcsball.model.player.PlayerSide;
import pl.edu.tcs.tcsball.net.connection.GameClient;
import pl.edu.tcs.tcsball.net.connection.GameHostServer;
import pl.edu.tcs.tcsball.net.discovery.DiscoveredHost;
import pl.edu.tcs.tcsball.net.discovery.DiscoveryMessage;
import pl.edu.tcs.tcsball.net.discovery.LanHostAnnouncer;
import pl.edu.tcs.tcsball.net.discovery.LanHostScanner;
import pl.edu.tcs.tcsball.net.protocol.MessageType;
import pl.edu.tcs.tcsball.net.protocol.NetworkMessage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LobbyManager {
    private static final int PROFILE_FIELD_COUNT = 4;
    private static final int HOST_PROFILE_START = 4;
    private static final int GUEST_PROFILE_START = HOST_PROFILE_START + PROFILE_FIELD_COUNT;
    private static final int LOBBY_STATE_FIELD_COUNT = GUEST_PROFILE_START + PROFILE_FIELD_COUNT;

    private Lobby lobby;
    private LanHostAnnouncer announcer;
    private LanHostScanner scanner;
    private GameHostServer hostServer;
    private GameClient client;
    private String lobbyId;
    private PlayerProfile localProfile;
    private PlayerSide localSide;
    private boolean startRequested;

    public Optional<Lobby> getLobby() {
        return Optional.ofNullable(lobby);
    }

    public Optional<PlayerSide> getLocalSide() {
        return Optional.ofNullable(localSide);
    }

    public void hostLobby(PlayerProfile hostProfile) throws IOException {
        leaveLobby();

        lobby = new Lobby(hostProfile);
        lobbyId = UUID.randomUUID().toString();
        localProfile = hostProfile;
        localSide = PlayerSide.LEFT;
        startRequested = false;

        hostServer = new GameHostServer();
        hostServer.start(lobby);

        announcer = new LanHostAnnouncer();
        announcer.start(createDiscoveryMessage());
    }

    public void joinLobby(DiscoveredHost host, PlayerProfile guestProfile) throws IOException {
        leaveLobby();

        localProfile = guestProfile;
        localSide = PlayerSide.RIGHT;
        startRequested = false;

        client = new GameClient();
        client.connect(host);
        client.send(createProfileMessage(MessageType.JOIN_REQUEST, guestProfile));
    }

    public void leaveLobby() {
        sendQuitQuietly();
        stopScanner();

        if (announcer != null) {
            announcer.stop();
            announcer = null;
        }

        if (hostServer != null) {
            hostServer.stop();
            hostServer = null;
        }

        if (client != null) {
            client.disconnect();
            client = null;
        }

        if (lobby != null) {
            lobby.close();
        }

        lobby = null;
        lobbyId = null;
        localProfile = null;
        localSide = null;
        startRequested = false;
    }

    public void updateProfile(PlayerSide side, PlayerProfile profile) {
        if (lobby == null) {
            return;
        }
        lobby.updateProfile(side, profile);
    }

    public void setLocalReady(boolean ready) throws IOException {
        if (lobby == null || localSide == null) {
            return;
        }

        lobby.setReady(localSide, ready);
        if (localSide == PlayerSide.LEFT) {
            sendLobbyState();
            refreshAnnouncement();
        } else if (client != null && client.isConnected()) {
            client.send(NetworkMessage.of(MessageType.PLAYER_READY, Boolean.toString(ready)));
        }
    }

    public void setReady(PlayerSide side, boolean ready) {
        if (lobby != null) {
            lobby.setReady(side, ready);
        }
    }

    public boolean canStartGame() {
        return localSide == PlayerSide.LEFT && lobby != null && lobby.canStart();
    }

    public void startGame() throws IOException {
        if (!canStartGame()) {
            return;
        }

        lobby.startGame();
        refreshAnnouncement();
        sendLobbyState();

        if (hostServer != null && hostServer.hasClientConnection()) {
            hostServer.sendToClient(NetworkMessage.of(MessageType.START_GAME));
        }
    }

    public boolean consumeStartRequested() {
        boolean requested = startRequested;
        startRequested = false;
        return requested;
    }

    public boolean updateNetwork() throws IOException {
        boolean changed = false;

        if (hostServer != null && lobby != null && lobby.getGuest().isPresent()
                && !hostServer.hasClientConnection()) {
            lobby.removeGuest();
            changed = true;
        }

        for (NetworkMessage message : drainNetworkMessages()) {
            try {
                if (hostServer != null) {
                    changed |= handleHostMessage(message);
                } else if (client != null) {
                    changed |= handleClientMessage(message);
                }
            } catch (IllegalArgumentException ignored) {
                // Uszkodzone komunikaty ignorujemy, tak jak obce pakiety discovery.
            }
        }

        if (changed) {
            refreshAnnouncement();
        }
        return changed;
    }

    public void startScanningHosts() throws IOException {
        if (scanner != null && scanner.isRunning()) {
            return;
        }

        scanner = new LanHostScanner();
        scanner.start();
    }

    public List<DiscoveredHost> getDiscoveredHosts() {
        if (scanner == null) {
            return List.of();
        }
        scanner.clearExpiredHosts();
        return scanner.getDiscoveredHosts();
    }

    public void stopScanner() {
        if (scanner != null) {
            scanner.stop();
            scanner = null;
        }
    }

    public List<NetworkMessage> drainNetworkMessages() {
        if (hostServer != null) {
            return hostServer.drainIncomingMessages();
        }
        if (client != null) {
            return client.drainIncomingMessages();
        }
        return List.of();
    }

    private boolean handleHostMessage(NetworkMessage message) throws IOException {
        return switch (message.type()) {
            case JOIN_REQUEST -> handleJoinRequest(message);
            case PLAYER_READY -> handleGuestReady(message);
            case PLAYER_PROFILE_UPDATED -> handleGuestProfileUpdated(message);
            case QUIT -> handleGuestQuit();
            default -> false;
        };
    }

    private boolean handleClientMessage(NetworkMessage message) {
        return switch (message.type()) {
            case LOBBY_STATE -> applyLobbyState(message);
            case START_GAME -> {
                startRequested = true;
                yield true;
            }
            case QUIT -> {
                if (lobby != null) {
                    lobby.close();
                }
                yield true;
            }
            default -> false;
        };
    }

    private boolean handleJoinRequest(NetworkMessage message) throws IOException {
        if (lobby == null || lobby.getGuest().isPresent()) {
            return false;
        }

        lobby.addGuest(readProfile(message, 0));
        sendLobbyState();
        return true;
    }

    private boolean handleGuestReady(NetworkMessage message) throws IOException {
        if (lobby == null || lobby.getGuest().isEmpty() || message.fields().isEmpty()) {
            return false;
        }

        lobby.setReady(PlayerSide.RIGHT, Boolean.parseBoolean(message.fields().get(0)));
        sendLobbyState();
        return true;
    }

    private boolean handleGuestProfileUpdated(NetworkMessage message) throws IOException {
        if (lobby == null || lobby.getGuest().isEmpty()) {
            return false;
        }

        lobby.updateProfile(PlayerSide.RIGHT, readProfile(message, 0));
        sendLobbyState();
        return true;
    }

    private boolean handleGuestQuit() throws IOException {
        if (lobby == null || lobby.getGuest().isEmpty()) {
            return false;
        }

        lobby.removeGuest();
        sendLobbyState();
        return true;
    }

    private boolean applyLobbyState(NetworkMessage message) {
        List<String> fields = message.fields();
        if (fields.size() < LOBBY_STATE_FIELD_COUNT) {
            throw new IllegalArgumentException("Invalid lobby state message");
        }

        LobbyState receivedState = LobbyState.valueOf(fields.get(0));
        boolean hostReady = Boolean.parseBoolean(fields.get(1));
        boolean guestPresent = Boolean.parseBoolean(fields.get(2));
        boolean guestReady = Boolean.parseBoolean(fields.get(3));

        PlayerProfile hostProfile = readProfile(message, HOST_PROFILE_START);
        if (lobby == null) {
            lobby = new Lobby(hostProfile);
        } else {
            lobby.updateProfile(PlayerSide.LEFT, hostProfile);
        }

        if (guestPresent) {
            PlayerProfile guestProfile = readProfile(message, GUEST_PROFILE_START);
            if (lobby.getGuest().isEmpty()) {
                lobby.addGuest(guestProfile);
            } else {
                lobby.updateProfile(PlayerSide.RIGHT, guestProfile);
            }
            lobby.setReady(PlayerSide.RIGHT, guestReady);
        } else if (lobby.getGuest().isPresent()) {
            lobby.removeGuest();
        }

        lobby.setReady(PlayerSide.LEFT, hostReady);

        if (receivedState == LobbyState.IN_GAME && lobby.canStart()) {
            lobby.startGame();
            startRequested = true;
        } else if (receivedState == LobbyState.CLOSED) {
            lobby.close();
        }

        return true;
    }

    private void sendLobbyState() throws IOException {
        if (hostServer == null || !hostServer.hasClientConnection() || lobby == null) {
            return;
        }
        hostServer.sendToClient(createLobbyStateMessage());
    }

    private void refreshAnnouncement() {
        if (announcer != null && announcer.isRunning() && lobby != null) {
            announcer.update(createDiscoveryMessage());
        }
    }

    private DiscoveryMessage createDiscoveryMessage() {
        String hostName = lobby != null
                ? lobby.getHost().getProfile().name()
                : System.getProperty("user.name", "Host");
        int playersCount = lobby != null && lobby.getGuest().isPresent() ? 2 : 1;
        LobbyState lobbyState = lobby != null ? lobby.getState() : LobbyState.CLOSED;

        return new DiscoveryMessage(
                lobbyId,
                hostName,
                GameConfig.NETWORK_GAME_PORT,
                playersCount,
                lobbyState
        );
    }

    private NetworkMessage createLobbyStateMessage() {
        LobbyPlayer host = lobby.getHost();
        LobbyPlayer guest = lobby.getGuest().orElse(null);

        return NetworkMessage.of(MessageType.LOBBY_STATE,
                lobby.getState().name(),
                Boolean.toString(host.isReady()),
                Boolean.toString(guest != null),
                Boolean.toString(guest != null && guest.isReady()),
                host.getProfile().name(),
                host.getProfile().pawnFlag().code(),
                host.getProfile().pawnFlag().displayName(),
                host.getProfile().formationId(),
                guest != null ? guest.getProfile().name() : "",
                guest != null ? guest.getProfile().pawnFlag().code() : "",
                guest != null ? guest.getProfile().pawnFlag().displayName() : "",
                guest != null ? guest.getProfile().formationId() : ""
        );
    }

    private static NetworkMessage createProfileMessage(MessageType type, PlayerProfile profile) {
        return NetworkMessage.of(type,
                profile.name(),
                profile.pawnFlag().code(),
                profile.pawnFlag().displayName(),
                profile.formationId()
        );
    }

    private static PlayerProfile readProfile(NetworkMessage message, int startIndex) {
        List<String> fields = message.fields();
        if (fields.size() < startIndex + PROFILE_FIELD_COUNT) {
            throw new IllegalArgumentException("Invalid player profile message");
        }

        return new PlayerProfile(
                fields.get(startIndex),
                new PlayerFlag(fields.get(startIndex + 1), fields.get(startIndex + 2)),
                fields.get(startIndex + 3)
        );
    }

    private void sendQuitQuietly() {
        try {
            if (hostServer != null && hostServer.hasClientConnection()) {
                hostServer.sendToClient(NetworkMessage.of(MessageType.QUIT));
            } else if (client != null && client.isConnected()) {
                client.send(NetworkMessage.of(MessageType.QUIT));
            }
        } catch (IOException ignored) {
            // Przy wychodzeniu z lobby zamykamy polaczenie niezaleznie od bledu wysylki.
        }
    }
}
