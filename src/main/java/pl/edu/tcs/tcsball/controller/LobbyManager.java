package pl.edu.tcs.tcsball.controller;

import pl.edu.tcs.tcsball.model.lobby.Lobby;
import pl.edu.tcs.tcsball.model.player.PlayerProfile;
import pl.edu.tcs.tcsball.model.player.PlayerSide;
import pl.edu.tcs.tcsball.net.connection.GameClient;
import pl.edu.tcs.tcsball.net.connection.GameHostServer;
import pl.edu.tcs.tcsball.net.discovery.DiscoveredHost;
import pl.edu.tcs.tcsball.net.discovery.LanHostAnnouncer;
import pl.edu.tcs.tcsball.net.discovery.LanHostScanner;

import java.io.IOException;
import java.util.Optional;

public class LobbyManager {
    private Lobby lobby;
    private LanHostAnnouncer announcer;
    private LanHostScanner scanner;
    private GameHostServer hostServer;
    private GameClient client;

    public Optional<Lobby> getLobby() {
        // TODO: zwrocic aktualne lobby, jesli istnieje.
        throw new UnsupportedOperationException("TODO");
    }

    public void hostLobby(PlayerProfile hostProfile) throws IOException {
        // TODO: utworzyc lobby hosta, uruchomic serwer i LAN announcer.
        throw new UnsupportedOperationException("TODO");
    }

    public void joinLobby(DiscoveredHost host, PlayerProfile guestProfile) throws IOException {
        // TODO: polaczyc sie z wybranym hostem i wyslac profil goscia.
        throw new UnsupportedOperationException("TODO");
    }

    public void leaveLobby() {
        // TODO: opuscic lobby i zamknac zasoby sieciowe.
        throw new UnsupportedOperationException("TODO");
    }

    public void updateProfile(PlayerSide side, PlayerProfile profile) {
        // TODO: zaktualizowac profil gracza w lobby.
        throw new UnsupportedOperationException("TODO");
    }

    public void setReady(PlayerSide side, boolean ready) {
        // TODO: ustawic gotowosc gracza w lobby.
        throw new UnsupportedOperationException("TODO");
    }

    public boolean canStartGame() {
        // TODO: sprawdzic, czy lobby moze przejsc do meczu.
        throw new UnsupportedOperationException("TODO");
    }
}
