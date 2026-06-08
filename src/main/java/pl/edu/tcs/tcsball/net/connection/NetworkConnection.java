package pl.edu.tcs.tcsball.net.connection;

import pl.edu.tcs.tcsball.net.protocol.NetworkMessage;
import pl.edu.tcs.tcsball.net.protocol.NetworkProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkConnection implements AutoCloseable {
    private final Socket socket;
    private final NetworkProtocol protocol = new NetworkProtocol();
    private final Queue<NetworkMessage> incomingMessages = new ConcurrentLinkedQueue<>();
    private final BufferedReader reader;
    private final PrintWriter writer;

    private volatile boolean open = true;
    private final Thread readerThread;

    public NetworkConnection(Socket socket) throws IOException {
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);

        readerThread = new Thread(this::readLoop, "tcsball-network-reader");
        readerThread.setDaemon(true);
        readerThread.start();
    }

    public void send(NetworkMessage message) throws IOException {
        if (!open) {
            throw new IOException("Connection is closed");
        }
        writer.println(protocol.serialize(message));
    }

    public List<NetworkMessage> drainIncomingMessages() {
        List<NetworkMessage> drained = new ArrayList<>();
        NetworkMessage message;

        while ((message = incomingMessages.poll()) != null) {
            drained.add(message);
        }

        return drained;
    }

    public boolean isOpen() {
        return open && !socket.isClosed();
    }

    @Override
    public void close() throws IOException {
        open = false;
        readerThread.interrupt();
        socket.close();
    }

    private void readLoop() {
        while (open) {
            try {
                String line = reader.readLine();
                if (line == null) {
                    open = false;
                    break;
                }
                incomingMessages.add(protocol.parse(line));
            } catch (IOException | IllegalArgumentException exception) {
                open = false;
            }
        }
    }
}
