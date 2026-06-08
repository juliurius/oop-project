package pl.edu.tcs.tcsball.net.discovery;

import java.io.IOException;

public class LanHostAnnouncer implements AutoCloseable {
    private int discoveryPort;
    private long broadcastIntervalMillis;
    private boolean running;
    private DiscoveryMessage currentMessage;
    private Thread announcerThread;

    public void start(DiscoveryMessage message) throws IOException {
        // TODO: rozpoczac rozglaszanie lobby w LAN.
        throw new UnsupportedOperationException("TODO");
    }

    public void update(DiscoveryMessage message) {
        // TODO: zaktualizowac dane rozglaszanego lobby.
        throw new UnsupportedOperationException("TODO");
    }

    public void stop() {
        // TODO: zatrzymac rozglaszanie lobby.
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isRunning() {
        // TODO: zwrocic, czy announcer dziala.
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void close() {
        // TODO: zwolnic zasoby sieciowe.
        throw new UnsupportedOperationException("TODO");
    }
}
