package pl.edu.tcs.tcsball.net.connection;

import pl.edu.tcs.tcsball.model.player.PlayerProfile;
import pl.edu.tcs.tcsball.net.discovery.DiscoveredHost;
import pl.edu.tcs.tcsball.net.protocol.NetworkMessage;

import java.io.IOException;
import java.util.List;

public class GameClient implements AutoCloseable {
    private DiscoveredHost host;
    private PlayerProfile profile;
    private NetworkConnection connection;
    private boolean connected;

    public void connect(DiscoveredHost host, PlayerProfile profile) throws IOException {
        // TODO: polaczyc sie z wybranym hostem i wyslac profil gracza.
        throw new UnsupportedOperationException("TODO");
    }

    public void send(NetworkMessage message) throws IOException {
        // TODO: wyslac wiadomosc do hosta.
        throw new UnsupportedOperationException("TODO");
    }

    public List<NetworkMessage> drainIncomingMessages() {
        // TODO: odebrac wiadomosci od hosta.
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isConnected() {
        // TODO: zwrocic, czy klient jest polaczony.
        throw new UnsupportedOperationException("TODO");
    }

    public void disconnect() {
        // TODO: rozlaczyc klienta.
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void close() {
        // TODO: zwolnic zasoby klienta.
        throw new UnsupportedOperationException("TODO");
    }
}
