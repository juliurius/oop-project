package pl.edu.tcs.tcsball.controller;

import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.model.lobby.Lobby;
import pl.edu.tcs.tcsball.model.lobby.LobbyState;
import pl.edu.tcs.tcsball.model.player.PlayerProfile;
import pl.edu.tcs.tcsball.model.player.PlayerSide;
import pl.edu.tcs.tcsball.net.connection.GameClient;
import pl.edu.tcs.tcsball.net.connection.GameHostServer;
import pl.edu.tcs.tcsball.net.discovery.DiscoveredHost;
import pl.edu.tcs.tcsball.net.discovery.DiscoveryMessage;
import pl.edu.tcs.tcsball.net.discovery.LanHostAnnouncer;
import pl.edu.tcs.tcsball.net.discovery.LanHostScanner;
import pl.edu.tcs.tcsball.net.protocol.NetworkMessage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LobbyManager {
    private Lobby lobby;
    private LanHostAnnouncer announcer;
    private LanHostScanner scanner;
    private GameHostServer hostServer;
    private GameClient client;
    private String lobbyId;

    public Optional<Lobby> getLobby() {
        return Optional.ofNullable(lobby);
    }

    public void hostLobby(PlayerProfile hostProfile) throws IOException {
        leaveLobby();

        lobby = new Lobby(hostProfile);
        lobbyId = UUID.randomUUID().toString();

        hostServer = new GameHostServer();
        hostServer.start(lobby);

        announcer = new LanHostAnnouncer();
        announcer.start(createDiscoveryMessage(1, LobbyState.WAITING_FOR_PLAYER));
    }

    public void joinLobby(DiscoveredHost host, PlayerProfile guestProfile) throws IOException {
        leaveLobby();

        client = new GameClient();
        client.connect(host, guestProfile);
    }

    public void leaveLobby() {
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

        lobby = null;
        lobbyId = null;
    }

    public void updateProfile(PlayerSide side, PlayerProfile profile) {
        if (lobby == null) {
            return;
        }
        lobby.updateProfile(side, profile);
    }

    public void setReady(PlayerSide side, boolean ready) {
        if (lobby == null) {
            return;
        }
        lobby.setReady(side, ready);
    }

    public boolean canStartGame() {
        return lobby != null && lobby.canStart();
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

    private DiscoveryMessage createDiscoveryMessage(int playersCount, LobbyState lobbyState) {
        String hostName = System.getProperty("user.name", "Host");
        return new DiscoveryMessage(
                lobbyId,
                hostName,
                GameConfig.NETWORK_GAME_PORT,
                playersCount,
                lobbyState
        );
    }
}
