package pl.edu.tcs.tcsball.model.lobby;

import pl.edu.tcs.tcsball.model.player.PlayerProfile;
import pl.edu.tcs.tcsball.model.player.PlayerSide;

import java.util.Objects;
import java.util.Optional;

public class Lobby {
    private LobbyPlayer host;
    private LobbyPlayer guest;
    private LobbyState state;

    public Lobby(PlayerProfile hostProfile) {
        host = new LobbyPlayer(PlayerSide.LEFT, hostProfile);
        state = LobbyState.WAITING_FOR_PLAYER;
    }

    public LobbyPlayer getHost() {
        return host;
    }

    public Optional<LobbyPlayer> getGuest() {
        return Optional.ofNullable(guest);
    }

    public LobbyState getState() {
        return state;
    }

    public void addGuest(PlayerProfile guestProfile) {
        if (guest != null) {
            throw new IllegalStateException("Guest already joined");
        }
        if (state == LobbyState.CLOSED || state == LobbyState.IN_GAME) {
            throw new IllegalStateException("Lobby is not joinable");
        }

        guest = new LobbyPlayer(PlayerSide.RIGHT, guestProfile);
        updateWaitingState();
    }

    public void removeGuest() {
        guest = null;
        host.setReady(false);
        updateWaitingState();
    }

    public void updateProfile(PlayerSide side, PlayerProfile profile) {
        LobbyPlayer player = getPlayer(side);
        player.setProfile(profile);
        player.setReady(false);
        updateWaitingState();
    }

    public void setReady(PlayerSide side, boolean ready) {
        getPlayer(side).setReady(ready);
        updateWaitingState();
    }

    public boolean canStart() {
        return guest != null && host.isReady() && guest.isReady();
    }

    public void startGame() {
        if (!canStart()) {
            throw new IllegalStateException("Both players must be ready");
        }
        state = LobbyState.IN_GAME;
    }

    public void close() {
        state = LobbyState.CLOSED;
    }

    private LobbyPlayer getPlayer(PlayerSide side) {
        Objects.requireNonNull(side, "side");
        if (side == PlayerSide.LEFT) {
            return host;
        }
        return getGuest().orElseThrow(() -> new IllegalStateException("Guest has not joined"));
    }

    private void updateWaitingState() {
        if (state == LobbyState.CLOSED || state == LobbyState.IN_GAME) {
            return;
        }

        if (guest == null) {
            state = LobbyState.WAITING_FOR_PLAYER;
        } else if (canStart()) {
            state = LobbyState.READY_TO_START;
        } else {
            state = LobbyState.WAITING_FOR_READY;
        }
    }
}
