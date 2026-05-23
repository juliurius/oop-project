package pl.edu.tcs.tcsball.model;

import pl.edu.tcs.tcsball.GameConfig;

import java.util.ArrayList;
import java.util.List;

public class Formation {
    public static List<Pawn> getFormation() {
        List<Pawn> pawns = new ArrayList<>();

        for (double[] offset : GameConfig.TEAM_FORMATION_OFFSETS) {
            double dx = offset[0];
            double dy = offset[1];

            // Drużyna 1 — lewa strona (offset od lewej bandy)
            pawns.add(new Pawn(
                    GameConfig.PITCH_LEFT_X + dx,
                    GameConfig.GOAL_CENTER_Y + dy,
                    GameConfig.PAWN_RADIUS,
                    1
            ));

            // Drużyna 2 — lustrzane odbicie po prawej stronie
            pawns.add(new Pawn(
                    GameConfig.PITCH_RIGHT_X - dx,
                    GameConfig.GOAL_CENTER_Y + dy,
                    GameConfig.PAWN_RADIUS,
                    2
            ));
        }

        return pawns;
    }
}
