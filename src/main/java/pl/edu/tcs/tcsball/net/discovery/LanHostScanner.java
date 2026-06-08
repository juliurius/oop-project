package pl.edu.tcs.tcsball.net.discovery;

import java.io.IOException;
import java.util.List;

public class LanHostScanner implements AutoCloseable {
    private int discoveryPort;
    private long hostTimeoutMillis;
    private boolean running;
    private List<DiscoveredHost> discoveredHosts;
    private Thread scannerThread;

    public void start() throws IOException {
        // TODO: zaczac nasluchiwanie ogloszen hostow w LAN.
        throw new UnsupportedOperationException("TODO");
    }

    public List<DiscoveredHost> getDiscoveredHosts() {
        // TODO: zwrocic aktualna liste znalezionych hostow.
        throw new UnsupportedOperationException("TODO");
    }

    public void clearExpiredHosts() {
        // TODO: usunac hosty niewidziane od okreslonego czasu.
        throw new UnsupportedOperationException("TODO");
    }

    public void stop() {
        // TODO: zatrzymac skanowanie LAN.
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isRunning() {
        // TODO: zwrocic, czy scanner dziala.
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void close() {
        // TODO: zwolnic zasoby sieciowe.
        throw new UnsupportedOperationException("TODO");
    }
}
