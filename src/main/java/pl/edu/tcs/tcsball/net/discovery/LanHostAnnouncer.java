package pl.edu.tcs.tcsball.net.discovery;

import pl.edu.tcs.tcsball.GameConfig;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class LanHostAnnouncer implements AutoCloseable {
    private static final String BROADCAST_ADDRESS = "255.255.255.255";

    private final int discoveryPort = GameConfig.NETWORK_DISCOVERY_PORT;
    private final long broadcastIntervalMillis = GameConfig.NETWORK_BROADCAST_INTERVAL_MILLIS;

    private volatile boolean running;
    private volatile DiscoveryMessage currentMessage;
    private DatagramSocket socket;
    private Thread announcerThread;

    public void start(DiscoveryMessage message) throws IOException {
        Objects.requireNonNull(message, "message");

        if (running) {
            update(message);
            return;
        }

        currentMessage = message;
        socket = new DatagramSocket();
        socket.setBroadcast(true);
        running = true;

        announcerThread = new Thread(this::announceLoop, "tcsball-lan-announcer");
        announcerThread.setDaemon(true);
        announcerThread.start();
    }

    public void update(DiscoveryMessage message) {
        currentMessage = Objects.requireNonNull(message, "message");
    }

    public void stop() {
        running = false;

        if (announcerThread != null) {
            announcerThread.interrupt();
        }

        if (socket != null) {
            socket.close();
            socket = null;
        }
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void close() {
        stop();
    }

    private void announceLoop() {
        while (running) {
            try {
                sendCurrentMessage();
            } catch (IOException ignored) {
                // Przy kolejnym obiegu petli sprobuje wyslac ponownie.
            }

            try {
                Thread.sleep(broadcastIntervalMillis);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    private void sendCurrentMessage() throws IOException {
        DatagramSocket activeSocket = socket;
        DiscoveryMessage message = currentMessage;

        if (activeSocket == null || message == null) {
            return;
        }

        byte[] payload = message.toPayload().getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(
                payload,
                payload.length,
                InetAddress.getByName(BROADCAST_ADDRESS),
                discoveryPort
        );

        activeSocket.send(packet);
    }
}
