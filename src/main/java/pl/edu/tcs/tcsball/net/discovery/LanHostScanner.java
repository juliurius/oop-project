package pl.edu.tcs.tcsball.net.discovery;

import pl.edu.tcs.tcsball.GameConfig;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanHostScanner implements AutoCloseable {
    private final int discoveryPort = GameConfig.NETWORK_DISCOVERY_PORT;
    private final long hostTimeoutMillis = GameConfig.NETWORK_HOST_TIMEOUT_MILLIS;
    private final Map<String, DiscoveredHost> discoveredHosts = new HashMap<>();

    private volatile boolean running;
    private DatagramSocket socket;
    private Thread scannerThread;

    public void start() throws IOException {
        if (running) {
            return;
        }

        socket = new DatagramSocket(discoveryPort);
        running = true;

        scannerThread = new Thread(this::scanLoop, "tcsball-lan-scanner");
        scannerThread.setDaemon(true);
        scannerThread.start();
    }

    public List<DiscoveredHost> getDiscoveredHosts() {
        synchronized (discoveredHosts) {
            return new ArrayList<>(discoveredHosts.values());
        }
    }

    public void clearExpiredHosts() {
        long now = System.currentTimeMillis();
        synchronized (discoveredHosts) {
            discoveredHosts.values().removeIf(host -> host.isExpired(now, hostTimeoutMillis));
        }
    }

    public void stop() {
        running = false;

        if (scannerThread != null) {
            scannerThread.interrupt();
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

    private void scanLoop() {
        while (running) {
            try {
                receiveHostAnnouncement();
                clearExpiredHosts();
            } catch (SocketException exception) {
                if (running) {
                    running = false;
                }
            } catch (IOException | IllegalArgumentException ignored) {
                // Obce albo uszkodzone pakiety w LAN ignorujemy
            }
        }
    }

    private void receiveHostAnnouncement() throws IOException {
        DatagramSocket activeSocket = socket;
        if (activeSocket == null) {
            return;
        }

        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        activeSocket.receive(packet);

        String payload = new String(
                packet.getData(),
                packet.getOffset(),
                packet.getLength(),
                StandardCharsets.UTF_8
        );

        DiscoveryMessage message = DiscoveryMessage.fromPayload(payload);
        String hostAddress = packet.getAddress().getHostAddress();
        DiscoveredHost host = message.toDiscoveredHost(hostAddress, System.currentTimeMillis());

        synchronized (discoveredHosts) {
            discoveredHosts.put(host.lobbyId(), host);
        }
    }
}
