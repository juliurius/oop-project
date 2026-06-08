package pl.edu.tcs.tcsball.net.connection;

import pl.edu.tcs.tcsball.model.player.PlayerProfile;
import pl.edu.tcs.tcsball.net.discovery.DiscoveredHost;
import pl.edu.tcs.tcsball.net.protocol.MessageType;
import pl.edu.tcs.tcsball.net.protocol.NetworkMessage;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameClient implements AutoCloseable {
    private DiscoveredHost host;
    private PlayerProfile profile;
    private NetworkConnection connection;
    private volatile boolean connected;

    public void connect(DiscoveredHost host, PlayerProfile profile) throws IOException {
        this.host = Objects.requireNonNull(host, "host");
        this.profile = Objects.requireNonNull(profile, "profile");

        Socket socket = new Socket(host.hostAddress(), host.gamePort());
        connection = new NetworkConnection(socket);
        connected = true;

        send(NetworkMessage.of(MessageType.JOIN_REQUEST));
    }

    public void send(NetworkMessage message) throws IOException {
        if (connection == null || !connection.isOpen()) {
            throw new IOException("Client is not connected");
        }
        connection.send(message);
    }

    public List<NetworkMessage> drainIncomingMessages() {
        if (connection == null) {
            return new ArrayList<>();
        }
        return connection.drainIncomingMessages();
    }

    public boolean isConnected() {
        return connected && connection != null && connection.isOpen();
    }

    public void disconnect() {
        connected = false;

        if (connection == null) {
            return;
        }

        try {
            connection.close();
        } catch (IOException ignored) {
            // Rozlaczamy klienta, wiec ignorujemy blad sprzatania polaczenia.
        }
        connection = null;
    }

    @Override
    public void close() {
        disconnect();
    }
}
