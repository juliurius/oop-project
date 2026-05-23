package pl.edu.tcs.tcsball.model;

import pl.edu.tcs.tcsball.GameConfig;
import java.util.List;

public class Match {
    private List<Pawn> pawns;
    private Ball ball;
    private int scoreTeam1 = 0, scoreTeam2 = 0;
    private int turn = 1;

    public Match() {
        pawns = Formation.getFormation();
        ball = new Ball((GameConfig.PITCH_RIGHT_X + GameConfig.PITCH_LEFT_X)/2, (GameConfig.PITCH_TOP_Y + GameConfig.PITCH_BOTTOM_Y)/2, 12);
    }

    public List<Pawn> getPawns() { return pawns; }

    public Ball getBall() { return ball; }

    public int getTeamScore(int team) {
        return team == 1 ? scoreTeam1 : scoreTeam2;
    }

    public void updateScore(int team) {
        if (team == 1) scoreTeam1 += 1;
        else scoreTeam2 += 1;
    }

    public void resetGame() {
        pawns = Formation.getFormation();
        ball = new Ball((GameConfig.PITCH_RIGHT_X + GameConfig.PITCH_LEFT_X)/2, (GameConfig.PITCH_TOP_Y + GameConfig.PITCH_BOTTOM_Y)/2, 12);
    }
}
