package pl.edu.tcs.tcsball.model.player;

import pl.edu.tcs.tcsball.GameConfig;

public record PlayerProfile(String name, PlayerFlag pawnFlag, String formationId) {
    public static final int MAX_NAME_LENGTH = GameConfig.MAX_PLAYER_NAME_LENGTH;

    public PlayerProfile withName(String newName)            { return new PlayerProfile(newName, pawnFlag, formationId); }
    public PlayerProfile withPawnFlag(PlayerFlag newFlag)    { return new PlayerProfile(name, newFlag, formationId); }
    public PlayerProfile withFormationId(String newId)       { return new PlayerProfile(name, pawnFlag, newId); }
}
