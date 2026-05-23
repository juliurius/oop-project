package pl.edu.tcs.tcsball.view.element;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pl.edu.tcs.tcsball.GameConfig;

public class PitchRenderer {
    GraphicsContext graphicsContext;
    
    public PitchRenderer(GraphicsContext graphicsContext) { this.graphicsContext = graphicsContext; }
    
    public void drawPitch() {
        double pitchHeight = GameConfig.WINDOW_HEIGHT - GameConfig.SCORE_PANEL_HEIGHT;

        graphicsContext.setFill(Color.web("#2e8b57"));
        graphicsContext.fillRect(0, GameConfig.SCORE_PANEL_HEIGHT, GameConfig.WINDOW_WIDTH, pitchHeight);

        graphicsContext.setStroke(Color.WHITE);
        graphicsContext.setLineWidth(3);
        graphicsContext.setGlobalAlpha(0.6);

        graphicsContext.strokeRect(
                GameConfig.PITCH_LEFT_X,
                GameConfig.PITCH_TOP_Y,
                GameConfig.WINDOW_WIDTH - 2 * GameConfig.MARGIN_X,
                pitchHeight - 2 * GameConfig.MARGIN_Y
        );

        graphicsContext.strokeLine(GameConfig.WINDOW_WIDTH / 2.0, GameConfig.PITCH_TOP_Y, GameConfig.WINDOW_WIDTH / 2.0, GameConfig.PITCH_BOTTOM_Y);
        graphicsContext.strokeOval(GameConfig.WINDOW_WIDTH / 2.0 - 50, GameConfig.SCORE_PANEL_HEIGHT + pitchHeight / 2.0 - 50, 100, 100);

        drawGoals();

        graphicsContext.setGlobalAlpha(1.0);
    }

    private void drawGoals() {
        graphicsContext.setStroke(Color.WHITE);
        graphicsContext.setLineWidth(3);
        graphicsContext.setFill(Color.web("#1c5936"));

        graphicsContext.fillRect(GameConfig.PITCH_LEFT_X - GameConfig.GOAL_WIDTH, GameConfig.GOAL_TOP_Y, GameConfig.GOAL_WIDTH, GameConfig.GOAL_HEIGHT);
        graphicsContext.strokeRect(GameConfig.PITCH_LEFT_X - GameConfig.GOAL_WIDTH, GameConfig.GOAL_TOP_Y, GameConfig.GOAL_WIDTH, GameConfig.GOAL_HEIGHT);

        graphicsContext.fillRect(GameConfig.PITCH_RIGHT_X, GameConfig.GOAL_TOP_Y, GameConfig.GOAL_WIDTH, GameConfig.GOAL_HEIGHT);
        graphicsContext.strokeRect(GameConfig.PITCH_RIGHT_X, GameConfig.GOAL_TOP_Y, GameConfig.GOAL_WIDTH, GameConfig.GOAL_HEIGHT);
    }
}
