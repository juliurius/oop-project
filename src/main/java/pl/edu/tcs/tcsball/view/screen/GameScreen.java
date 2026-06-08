package pl.edu.tcs.tcsball.view.screen;

import javafx.scene.canvas.GraphicsContext;
import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.model.GameView;
import pl.edu.tcs.tcsball.model.Pawn;
import pl.edu.tcs.tcsball.view.RenderPlan;
import pl.edu.tcs.tcsball.view.element.*;

import java.util.List;

public class GameScreen implements Screen {

    private final GraphicsContext backgroundGc;
    private final GraphicsContext gameGc;
    private final GraphicsContext uiGc;
    private final GraphicsContext overlayGc;

    private final ScoreBoardRenderer scoreBoardRenderer;
    private final PitchRenderer pitchRenderer;
    private final BallRenderer ballRenderer;
    private final PawnRenderer pawnRenderer;
    private final AimingRenderer aimingRenderer;

    public GameScreen(GraphicsContext backgroundGc, GraphicsContext gameGc, GraphicsContext uiGc,
                      GraphicsContext overlayGc, ScoreBoardRenderer scoreBoard, PitchRenderer pitch,
                      BallRenderer ball, PawnRenderer pawn, AimingRenderer aimingRenderer) {
        this.backgroundGc = backgroundGc;
        this.gameGc = gameGc;
        this.uiGc = uiGc;
        this.overlayGc = overlayGc;
        this.scoreBoardRenderer = scoreBoard;
        this.pitchRenderer = pitch;
        this.ballRenderer = ball;
        this.pawnRenderer = pawn;
        this.aimingRenderer = aimingRenderer;
    }

    @Override
    public void render(GameView game, RenderPlan plan) {
        if (plan.isBackground()) {
            pitchRenderer.bakeTo(backgroundGc);
        }

        if (plan.isUiLayer()) {
            uiGc.clearRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.SCORE_PANEL_HEIGHT);
            scoreBoardRenderer.drawScoreBoard(
                    game.getTeamScore(1),
                    game.getTeamScore(2),
                    game.getCurrentTurn(),
                    game.getActualMouseX(),
                    game.getActualMouseY(),
                    game.isEverythingStopped()
            );
        }

        if (plan.isGameLayer()) {
            clearPitchArea(gameGc);
            drawBodies(game);
        }

        if (plan.isOverlay()) {
            clearOverlay(overlayGc);
            if (game.getAimingPawn() != null) {
                aimingRenderer.drawAimingLine(game.getAimingPawn(), game.getArrowX(), game.getArrowY());
            }
        }
    }

    private void drawBodies(GameView game) {
        for (Pawn pawn : game.getPawns()) {
            pawnRenderer.drawPawn(pawn);
        }

        ballRenderer.drawBall(
                game.getBall().getPosition().getX(),
                game.getBall().getPosition().getY(),
                game.getBall().getRadius(),
                game.getBall().getAngle()
        );
    }

    private void clearPitchArea(GraphicsContext gc) {
        gc.clearRect(0, GameConfig.SCORE_PANEL_HEIGHT, GameConfig.WINDOW_WIDTH,
                GameConfig.WINDOW_HEIGHT - GameConfig.SCORE_PANEL_HEIGHT);
    }

    private void clearOverlay(GraphicsContext gc) {
        gc.clearRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
    }
}
