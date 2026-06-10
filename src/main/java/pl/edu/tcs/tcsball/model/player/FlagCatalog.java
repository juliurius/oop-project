package pl.edu.tcs.tcsball.model.player;

import java.util.*;

public class FlagCatalog {
    private final List<PlayerFlag> flags = List.of(
            new PlayerFlag("PL", "Polska"), new PlayerFlag("UA", "Ukraina"),
            new PlayerFlag("DE", "Niemcy"), new PlayerFlag("FR", "Francja"),
            new PlayerFlag("ES", "Hiszpania"));

    public List<PlayerFlag> all() { return flags; }
    public PlayerFlag first()     { return flags.getFirst(); }
}