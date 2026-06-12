package pl.edu.tcs.tcsball.controller;

import pl.edu.tcs.tcsball.model.Match;
import pl.edu.tcs.tcsball.model.lobby.Lobby;
import pl.edu.tcs.tcsball.model.lobby.LobbyPlayer;
import pl.edu.tcs.tcsball.model.player.PlayerProfile;
import pl.edu.tcs.tcsball.model.player.PlayerSide;
import pl.edu.tcs.tcsball.net.discovery.DiscoveredHost;

import java.util.List;
import java.util.function.Supplier;

/**
 * Projekcja stanu lobby i profili na dane czytane przez renderery (interfejs LobbyView).
 * Wylacznie odczyt — nie zmienia modelu ani stanu gry. Zmienne pola kontrolera
 * (biezacy stan gry, wybrany host) wstrzykiwane sa jako dostawcy, by czytac je na zywo.
 */
public class LobbyPresenter {
    private final LobbyManager lobbyManager;
    private final CustomizationManager customization;
    private final Match match;
    private final List<DiscoveredHost> discoveredHosts;
    private final Supplier<GameState> gameState;
    private final Supplier<DiscoveredHost> joinedHost;

    public LobbyPresenter(LobbyManager lobbyManager, CustomizationManager customization, Match match,
                          List<DiscoveredHost> discoveredHosts,
                          Supplier<GameState> gameState, Supplier<DiscoveredHost> joinedHost) {
        this.lobbyManager = lobbyManager;
        this.customization = customization;
        this.match = match;
        this.discoveredHosts = discoveredHosts;
        this.gameState = gameState;
        this.joinedHost = joinedHost;
    }

    public String getTeamPawnFlagCode(int team) {
        PlayerProfile profile = team == 1 ? match.getLeftProfile() : match.getRightProfile();
        return profile.pawnFlag().code();
    }

    public List<DiscoveredHost> getDiscoveredHosts() {
        return List.copyOf(discoveredHosts);
    }

    public DiscoveredHost getJoinedHost() {
        return joinedHost.get();
    }

    public boolean isLocalPlayerHost() {
        return lobbyManager.getLocalSide()
                .map(side -> side == PlayerSide.LEFT)
                .orElse(gameState.get() == GameState.HOST_LOBBY);
    }

    public String getLocalPlayerName() {
        return getLocalProfile().name();
    }

    public String getLocalPlayerFlagName() {
        return getLocalProfile().pawnFlag().displayName();
    }

    public String getLocalPlayerFlagCode() {
        return getLocalProfile().pawnFlag().code();
    }

    public boolean hasOpponent() {
        if (isLocalPlayerHost()) {
            return lobbyManager.getLobby()
                    .flatMap(Lobby::getGuest)
                    .isPresent();
        }
        return getOpponentPlayer() != null || joinedHost.get() != null;
    }

    public String getOpponentName() {
        LobbyPlayer opponent = getOpponentPlayer();
        if (opponent != null) {
            return opponent.getProfile().name();
        }
        if (gameState.get() == GameState.CLIENT_LOBBY && joinedHost.get() != null) {
            return joinedHost.get().getHostName();
        }
        return null;
    }

    public String getOpponentFlagName() {
        LobbyPlayer opponent = getOpponentPlayer();
        if (opponent != null) {
            return opponent.getProfile().pawnFlag().displayName();
        }
        return gameState.get() == GameState.CLIENT_LOBBY ? "?" : "";
    }

    public String getOpponentFlagCode() {
        LobbyPlayer opponent = getOpponentPlayer();
        if (opponent != null) {
            return opponent.getProfile().pawnFlag().code();
        }
        return "";
    }

    public boolean isLocalPlayerReady() {
        Lobby lobby = lobbyManager.getLobby().orElse(null);
        PlayerSide side = lobbyManager.getLocalSide().orElse(null);
        if (lobby == null || side == null) {
            return false;
        }
        return side == PlayerSide.LEFT
                ? lobby.getHost().isReady()
                : lobby.getGuest().map(LobbyPlayer::isReady).orElse(false);
    }

    public boolean isOpponentReady() {
        LobbyPlayer opponent = getOpponentPlayer();
        return opponent != null && opponent.isReady();
    }

    public boolean canStartGame() {
        return gameState.get() == GameState.HOST_LOBBY && lobbyManager.canStartGame();
    }

    private PlayerProfile getLocalProfile() {
        Lobby lobby = lobbyManager.getLobby().orElse(null);
        PlayerSide side = lobbyManager.getLocalSide().orElse(null);
        if (lobby == null || side == null) {
            return customization.getCurrentProfile();
        }
        if (side == PlayerSide.LEFT) {
            return lobby.getHost().getProfile();
        }
        return lobby.getGuest()
                .map(LobbyPlayer::getProfile)
                .orElse(customization.getCurrentProfile());
    }

    private LobbyPlayer getOpponentPlayer() {
        Lobby lobby = lobbyManager.getLobby().orElse(null);
        PlayerSide side = lobbyManager.getLocalSide().orElse(null);
        if (lobby == null || side == null) {
            return null;
        }
        if (side == PlayerSide.LEFT) {
            return lobby.getGuest().orElse(null);
        }
        return lobby.getHost();
    }

}
