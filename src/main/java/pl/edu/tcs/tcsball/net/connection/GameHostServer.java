package pl.edu.tcs.tcsball.net.connection;

import pl.edu.tcs.tcsball.model.lobby.Lobby;
import pl.edu.tcs.tcsball.net.protocol.NetworkMessage;

import java.io.IOException;
import java.util.List;

public class GameHostServer implements AutoCloseable {
    private int gamePort;
    private Lobby lobby;
    private NetworkConnection clientConnection;
    private boolean running;

    public void start(Lobby lobby) throws IOException {
        // TODO: uruchomic serwer hosta i czekac na klienta.
        throw new UnsupportedOperationException("TODO");
    }

    public void sendToClient(NetworkMessage message) throws IOException {
        // TODO: wyslac wiadomosc do klienta.
        throw new UnsupportedOperationException("TODO");
    }

    public List<NetworkMessage> drainIncomingMessages() {
        // TODO: odebrac wiadomosci od klienta.
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isRunning() {
        // TODO: zwrocic, czy serwer hosta dziala.
        throw new UnsupportedOperationException("TODO");
    }

    public void stop() {
        // TODO: zatrzymac serwer hosta.
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void close() {
        // TODO: zwolnic zasoby serwera.
        throw new UnsupportedOperationException("TODO");
    }
}
