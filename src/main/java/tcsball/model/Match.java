package tcsball.model;

import tcsball.GameConfig;
import java.util.List;

public class Match {
    private final List<Pawn> pawns;
    private Ball ball;
    private int scoreTeamA, scoreTeamB;

    public Match() {
        ball = new Ball((GameConfig.PITCH_RIGHT_X + GameConfig.PITCH_LEFT_X)/2, (GameConfig.PITCH_TOP_Y + GameConfig.PITCH_BOTTOM_Y)/2, 12);
        pawns = Formation.getFormation();
    }

    public List<Pawn> getPawns() { return pawns; }

    public Ball getBall() { return ball; }


}
