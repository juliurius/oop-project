package pl.edu.tcs.tcsball.model.player;

public record PlayerProfile(String name, PlayerFlag pawnFlag, String formationId) {
    public PlayerProfile withName(String newName)            { return new PlayerProfile(newName, pawnFlag, formationId); }
    public PlayerProfile withPawnFlag(PlayerFlag newFlag)    { return new PlayerProfile(name, newFlag, formationId); }
    public PlayerProfile withFormationId(String newId)       { return new PlayerProfile(name, pawnFlag, newId); }
}