package pl.edu.tcs.tcsball.model.formation;

import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.model.Pawn;
import pl.edu.tcs.tcsball.model.player.PlayerProfile;
import pl.edu.tcs.tcsball.model.player.PlayerSide;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FormationFactory {
    private final Map<String, FormationDefinition> definitions;

    public FormationFactory() {
        this.definitions = defaultCatalog();
    }

    public List<String> getAvailableIds() { return new ArrayList<>(definitions.keySet()); }

    public FormationDefinition getDefinition(String formationId) {
        FormationDefinition definition = definitions.get(formationId);
        if (definition == null) {
            throw new IllegalArgumentException("Nieznana formacja: " + formationId);
        }
        return definition;
    }

    public List<Pawn> createTeam(PlayerSide side, PlayerProfile profile) {
        FormationDefinition definition = getDefinition(profile.formationId());
        int team = (side == PlayerSide.LEFT) ? 1 : 2;

        List<Pawn> pawns = new ArrayList<>();
        for (double[] offset : definition.offsets()) {
            double dx = offset[0];
            double dy = offset[1];

            double x = (side == PlayerSide.LEFT) ? GameConfig.PITCH_LEFT_X + dx : GameConfig.PITCH_RIGHT_X - dx;
            double y = GameConfig.GOAL_CENTER_Y + dy;

            pawns.add(new Pawn(x, y, GameConfig.PAWN_RADIUS, team));
        }
        return pawns;
    }

    public List<Pawn> createPawns(PlayerProfile leftPlayer, PlayerProfile rightPlayer) {
        List<Pawn> pawns = new ArrayList<>();
        pawns.addAll(createTeam(PlayerSide.LEFT, leftPlayer));
        pawns.addAll(createTeam(PlayerSide.RIGHT, rightPlayer));
        return pawns;
    }

    private static Map<String, FormationDefinition> defaultCatalog() {
        Map<String, FormationDefinition> catalog = new LinkedHashMap<>();
        catalog.put("balanced", new FormationDefinition("balanced", "Zbalansowana 2-2",
                new double[][] {{35, 0}, {125, -100}, {125, 100}, {245, -80}, {245, 80}}));
        catalog.put("defensive", new FormationDefinition("defensive", "Obronna",
                new double[][] {{35, 0}, {90, -70}, {90, 70}, {200, -120}, {200, 120}}));
        catalog.put("offensive", new FormationDefinition("offensive", "Ofensywna",
                new double[][] {{35, 0}, {140, -110}, {140, 110}, {280, -60}, {280, 60}}));
        return catalog;
    }
}
