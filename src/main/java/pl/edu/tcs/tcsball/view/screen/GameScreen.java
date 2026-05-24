package pl.edu.tcs.tcsball.view.screen;

import pl.edu.tcs.tcsball.model.Ball;
import pl.edu.tcs.tcsball.model.GameView;
import pl.edu.tcs.tcsball.model.Pawn;
import pl.edu.tcs.tcsball.view.element.*;

import java.util.List;

public class GameScreen implements Screen {

    private final ScoreBoardRenderer scoreBoardRenderer;
    private final PitchRenderer pitchRenderer;
    private final BallRenderer ballRenderer;
    private final PawnRenderer pawnRenderer;
    private final AimingRenderer aimingRenderer;

    public GameScreen(ScoreBoardRenderer scoreBoard, PitchRenderer pitch, BallRenderer ball, PawnRenderer pawn, AimingRenderer aimingRenderer) {
        this.scoreBoardRenderer = scoreBoard;
        this.pitchRenderer = pitch;
        this.ballRenderer = ball;
        this.pawnRenderer = pawn;
        this.aimingRenderer = aimingRenderer;
    }

    @Override
    public void render(GameView game) {
        int score1 = game.getTeamScore(1);
        int score2 = game.getTeamScore(2);

        double ballX = game.getBall().getPosition().getX();
        double ballY = game.getBall().getPosition().getY();
        double ballRadius = game.getBall().getRadius();
        double ballAngle = game.getBall().getAngle();

        double arrowX = game.getArrowX();
        double arrowY = game.getArrowY();

        List<Pawn> pawns = game.getPawns();

        scoreBoardRenderer.drawScoreBoard(score1, score2, game.getCurrentTurn(), game.getActualMouseX(), game.getActualMouseY(), game.isEverythingStopped());
        pitchRenderer.drawPitch();
        ballRenderer.drawBall(ballX, ballY, ballRadius, ballAngle);

        for (Pawn pawn : pawns) {
            pawnRenderer.drawPawn(pawn);
        }

        aimingRenderer.drawAimingLine(game.getAimingPawn(), arrowX, arrowY);

    }
}
