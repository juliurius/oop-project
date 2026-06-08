package pl.edu.tcs.tcsball.net.connection;

import pl.edu.tcs.tcsball.net.protocol.NetworkMessage;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Queue;

public class NetworkConnection implements AutoCloseable {
    private Socket socket;
    private Queue<NetworkMessage> incomingMessages;
    private boolean open;
    private Thread readerThread;

    public void send(NetworkMessage message) throws IOException {
        // TODO: wyslac wiadomosc do drugiej strony polaczenia.
        throw new UnsupportedOperationException("TODO");
    }

    public List<NetworkMessage> drainIncomingMessages() {
        // TODO: zwrocic odebrane wiadomosci i oproznic kolejke.
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isOpen() {
        // TODO: zwrocic, czy polaczenie jest aktywne.
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void close() throws IOException {
        // TODO: zamknac sockety i watki polaczenia.
        throw new UnsupportedOperationException("TODO");
    }
}
