package pl.edu.tcs.tcsball.model.lobby;

import pl.edu.tcs.tcsball.model.player.PlayerProfile;
import pl.edu.tcs.tcsball.model.player.PlayerSide;

import java.util.Objects;

public class LobbyPlayer {
    private final PlayerSide side;
    private PlayerProfile profile;
    private boolean ready;

    public LobbyPlayer(PlayerSide side, PlayerProfile profile) {
        this.side = Objects.requireNonNull(side, "side");
        this.profile = Objects.requireNonNull(profile, "profile");
    }

    public PlayerSide getSide() {
        return side;
    }

    public PlayerProfile getProfile() {
        return profile;
    }

    public boolean isReady() {
        return ready;
    }

    public void setProfile(PlayerProfile profile) {
        this.profile = Objects.requireNonNull(profile, "profile");
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
