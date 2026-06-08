package pl.edu.tcs.tcsball.controller;

import pl.edu.tcs.tcsball.model.player.PlayerFlag;
import pl.edu.tcs.tcsball.model.player.PlayerProfile;

import java.util.List;

public class CustomizationManager {
    private PlayerProfile currentProfile;
    private List<PlayerFlag> availableFlags;
    private List<String> availableFormationIds;

    public PlayerProfile getCurrentProfile() {
        // TODO: zwrocic aktualny profil gracza.
        throw new UnsupportedOperationException("TODO");
    }

    public void setName(String name) {
        // TODO: ustawic nazwe gracza.
        throw new UnsupportedOperationException("TODO");
    }

    public void setPawnFlag(PlayerFlag flag) {
        // TODO: ustawic flage wyswietlana na pionkach.
        throw new UnsupportedOperationException("TODO");
    }

    public void setFormationId(String formationId) {
        // TODO: ustawic id wybranej formacji.
        throw new UnsupportedOperationException("TODO");
    }

    public void resetToDefaults() {
        // TODO: wrocic do wartosci domyslnych z konfiguracji.
        throw new UnsupportedOperationException("TODO");
    }
}
