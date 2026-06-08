package pl.edu.tcs.tcsball.model.lobby;

import pl.edu.tcs.tcsball.model.player.PlayerProfile;
import pl.edu.tcs.tcsball.model.player.PlayerSide;

public class LobbyPlayer {
    private PlayerSide side;
    private PlayerProfile profile;
    private boolean ready;

    // TODO: trzymac strone gracza w lobby.
    // TODO: trzymac profil gracza.
    // TODO: trzymac informacje, czy gracz jest gotowy.

    public PlayerSide getSide() {
        // TODO: zwrocic strone gracza.
        throw new UnsupportedOperationException("TODO");
    }

    public PlayerProfile getProfile() {
        // TODO: zwrocic profil gracza.
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isReady() {
        // TODO: zwrocic gotowosc gracza.
        throw new UnsupportedOperationException("TODO");
    }
}
