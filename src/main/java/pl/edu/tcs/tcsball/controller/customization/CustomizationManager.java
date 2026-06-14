package pl.edu.tcs.tcsball.controller.customization;

import pl.edu.tcs.tcsball.model.player.FlagCatalog;
import pl.edu.tcs.tcsball.model.player.PlayerFlag;
import pl.edu.tcs.tcsball.model.player.PlayerProfile;
import pl.edu.tcs.tcsball.model.formation.FormationFactory;

import java.util.List;

public class CustomizationManager {
    private PlayerProfile currentProfile;
    private List<PlayerFlag> availableFlags;
    private List<String> availableFormationIds;

    public static PlayerProfile defaultProfile(FlagCatalog flags, FormationFactory formations) {
        return new PlayerProfile("Gracz", flags.first(), formations.getAvailableIds().get(0));
    }

    public CustomizationManager(PlayerProfile currentProfile, List<PlayerFlag> availableFlags, List<String> availableFormationIds) {
        this.currentProfile = currentProfile;
        this.availableFlags = availableFlags;
        this.availableFormationIds = availableFormationIds;
    }

    public PlayerProfile getCurrentProfile() { return currentProfile; }

    public List<PlayerFlag> getAvailableFlags() { return availableFlags; }
    public List<String> getAvailableFormationIds() { return availableFormationIds; }

    public void setName(String name)            { currentProfile = currentProfile.withName(name); }
    public void setPawnFlag(PlayerFlag flag)    { currentProfile = currentProfile.withPawnFlag(flag); }
    public void setFormationId(String id)       { currentProfile = currentProfile.withFormationId(id); }
}
