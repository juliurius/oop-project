package pl.edu.tcs.tcsball.net.connection;

import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.model.lobby.Lobby;
import pl.edu.tcs.tcsball.net.protocol.NetworkMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameHostServer implements AutoCloseable {
    private int gamePort = GameConfig.NETWORK_GAME_PORT;
    private Lobby lobby;
    private NetworkConnection clientConnection;
    private ServerSocket serverSocket;
    private Thread acceptThread;
    private volatile boolean running;

    public void start(Lobby lobby) throws IOException {
        Objects.requireNonNull(lobby, "lobby");

        if (running) {
            return;
        }

        this.lobby = lobby;
        serverSocket = new ServerSocket(gamePort);
        running = true;

        acceptThread = new Thread(this::acceptClient, "tcsball-host-server");
        acceptThread.setDaemon(true);
        acceptThread.start();
    }

    public void sendToClient(NetworkMessage message) throws IOException {
        if (clientConnection == null || !clientConnection.isOpen()) {
            throw new IOException("Client is not connected");
        }
        clientConnection.send(message);
    }

    public List<NetworkMessage> drainIncomingMessages() {
        if (clientConnection == null) {
            return new ArrayList<>();
        }
        return clientConnection.drainIncomingMessages();
    }

    public boolean isRunning() {
        return running;
    }

    public boolean hasClientConnection() {
        return clientConnection != null && clientConnection.isOpen();
    }

    public void stop() {
        running = false;

        if (acceptThread != null) {
            acceptThread.interrupt();
        }

        closeClientConnection();
        closeServerSocket();
    }

    @Override
    public void close() {
        stop();
    }

    private void acceptClient() {
        try {
            Socket clientSocket = serverSocket.accept();
            if (running) {
                clientConnection = new NetworkConnection(clientSocket);
            } else {
                clientSocket.close();
            }
        } catch (IOException exception) {
            if (running) {
                running = false;
            }
        }
    }

    private void closeClientConnection() {
        if (clientConnection == null) {
            return;
        }

        try {
            clientConnection.close();
        } catch (IOException ignored) {
            // Zamykamy serwer, wiec ignorujemy blad sprzatania polaczenia.
        }
        clientConnection = null;
    }

    private void closeServerSocket() {
        if (serverSocket == null) {
            return;
        }

        try {
            serverSocket.close();
        } catch (IOException ignored) {
            // Zamykamy serwer, wiec ignorujemy blad sprzatania socketu.
        }
        serverSocket = null;
    }
}
