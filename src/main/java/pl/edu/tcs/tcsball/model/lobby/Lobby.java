package pl.edu.tcs.tcsball.model.lobby;

import pl.edu.tcs.tcsball.model.player.PlayerProfile;
import pl.edu.tcs.tcsball.model.player.PlayerSide;

import java.util.Optional;

public class Lobby {
    private LobbyPlayer host;
    private LobbyPlayer guest;
    private LobbyState state;

    // TODO: trzymac hosta lobby.
    // TODO: trzymac opcjonalnego goscia.
    // TODO: trzymac aktualny stan lobby.

    public Lobby(PlayerProfile hostProfile) {
        // TODO: utworzyc lobby dla hosta.
    }

    public LobbyPlayer getHost() {
        // TODO: zwrocic hosta lobby.
        throw new UnsupportedOperationException("TODO");
    }

    public Optional<LobbyPlayer> getGuest() {
        // TODO: zwrocic goscia, jesli dolaczyl.
        throw new UnsupportedOperationException("TODO");
    }

    public LobbyState getState() {
        // TODO: zwrocic stan lobby.
        throw new UnsupportedOperationException("TODO");
    }

    public void addGuest(PlayerProfile guestProfile) {
        // TODO: dodac drugiego gracza do lobby.
        throw new UnsupportedOperationException("TODO");
    }

    public void removeGuest() {
        // TODO: usunac goscia z lobby.
        throw new UnsupportedOperationException("TODO");
    }

    public void updateProfile(PlayerSide side, PlayerProfile profile) {
        // TODO: zaktualizowac profil gracza i cofnac jego gotowosc.
        throw new UnsupportedOperationException("TODO");
    }

    public void setReady(PlayerSide side, boolean ready) {
        // TODO: ustawic gotowosc gracza.
        throw new UnsupportedOperationException("TODO");
    }

    public boolean canStart() {
        // TODO: sprawdzic, czy mozna zaczac mecz.
        throw new UnsupportedOperationException("TODO");
    }

    public void startGame() {
        // TODO: przejsc ze stanu lobby do meczu.
        throw new UnsupportedOperationException("TODO");
    }

    public void close() {
        // TODO: zamknac lobby.
        throw new UnsupportedOperationException("TODO");
    }
}
