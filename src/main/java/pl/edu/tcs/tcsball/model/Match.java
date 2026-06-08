package pl.edu.tcs.tcsball.model;

import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.model.formation.FormationFactory;
import pl.edu.tcs.tcsball.model.player.PlayerProfile;

import java.util.List;

public class Match {
    private final FormationFactory formationFactory;
    private PlayerProfile leftProfile;
    private PlayerProfile rightProfile;

    private List<Pawn> pawns;
    private Ball ball;
    private int scoreTeam1 = 0, scoreTeam2 = 0;
    private int playerTurn = 1;

    public Match(FormationFactory formationFactory, PlayerProfile leftProfile, PlayerProfile rightProfile) {
        this.formationFactory = formationFactory;
        this.leftProfile = leftProfile;
        this.rightProfile = rightProfile;
        resetPitch();
    }

    public void setProfiles(PlayerProfile leftProfile, PlayerProfile rightProfile) {
        this.leftProfile = leftProfile;
        this.rightProfile = rightProfile;
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

    public void resetScore() {
        scoreTeam1 = 0;
        scoreTeam2 = 0;
    }

    public void resetPitch() {
        pawns = formationFactory.createPawns(leftProfile, rightProfile);
        ball = new Ball((GameConfig.PITCH_RIGHT_X + GameConfig.PITCH_LEFT_X)/2, (GameConfig.PITCH_TOP_Y + GameConfig.PITCH_BOTTOM_Y)/2, GameConfig.BALL_RADIUS);
    }

    public void resetGame() {
        resetPitch();
        resetScore();

        playerTurn = 1;
    }

    public int getPlayerTurn() { return playerTurn; }

    public void changeTurn() { playerTurn = 3 - playerTurn; }
}
