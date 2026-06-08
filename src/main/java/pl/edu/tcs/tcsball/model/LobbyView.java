package pl.edu.tcs.tcsball.model;

import pl.edu.tcs.tcsball.net.discovery.DiscoveredHost;

import java.util.List;

public interface LobbyView extends GameView {
    List<DiscoveredHost> getDiscoveredHosts();

    DiscoveredHost getJoinedHost();
}
