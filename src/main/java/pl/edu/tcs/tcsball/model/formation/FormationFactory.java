package pl.edu.tcs.tcsball.model.formation;

import pl.edu.tcs.tcsball.model.Pawn;
import pl.edu.tcs.tcsball.model.player.PlayerProfile;
import pl.edu.tcs.tcsball.model.player.PlayerSide;

import java.util.List;
import java.util.Map;

public class FormationFactory {
    private Map<String, FormationDefinition> definitions;

    public FormationDefinition getDefinition(String formationId) {
        // TODO: zwrocic definicje formacji dla podanego id.
        throw new UnsupportedOperationException("TODO");
    }

    public List<Pawn> createTeam(PlayerSide side, PlayerProfile profile) {
        // TODO: utworzyc pionki jednej druzyny na podstawie strony i profilu.
        throw new UnsupportedOperationException("TODO");
    }

    public List<Pawn> createPawns(PlayerProfile leftPlayer, PlayerProfile rightPlayer) {
        // TODO: utworzyc wszystkie pionki meczu dla obu profili.
        throw new UnsupportedOperationException("TODO");
    }
}
