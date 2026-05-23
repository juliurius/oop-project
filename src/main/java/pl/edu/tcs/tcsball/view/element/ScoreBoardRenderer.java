package pl.edu.tcs.tcsball.view.element;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pl.edu.tcs.tcsball.GameConfig;

public class ScoreBoardRenderer {
    private final GraphicsContext graphicsContext;

    public static final double BACK_BTN_WIDTH = 100;
    public static final double BACK_BTN_HEIGHT = 40;
    public static final double BACK_BTN_X = 20;
    public static final double BACK_BTN_Y = 20;

    public ScoreBoardRenderer(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    public void drawScoreBoard(int score1, int score2, int currentTurn) {
        graphicsContext.setFill(Color.web("#1a1a1a"));
        graphicsContext.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.SCORE_PANEL_HEIGHT);

        graphicsContext.setTextAlign(TextAlignment.CENTER);
        graphicsContext.setTextBaseline(VPos.CENTER);
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        graphicsContext.fillText("WYNIK  " + score1 + " : " + score2, GameConfig.WINDOW_WIDTH / 2.0, GameConfig.SCORE_PANEL_HEIGHT / 2.0);

        graphicsContext.setFill(Color.web("#d9534f"));
        graphicsContext.fillRoundRect(BACK_BTN_X, BACK_BTN_Y, BACK_BTN_WIDTH, BACK_BTN_HEIGHT, 10, 10);

        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        graphicsContext.fillText("MENU", BACK_BTN_X + BACK_BTN_WIDTH / 2.0, BACK_BTN_Y + BACK_BTN_HEIGHT / 2.0);

        double dotRadius = 12.0;
        double dotX = GameConfig.WINDOW_WIDTH - 40.0;
        double dotY = GameConfig.SCORE_PANEL_HEIGHT / 2.0;

        graphicsContext.setFill(Color.color(0, 0, 0, 0.5));
        graphicsContext.fillOval(dotX - dotRadius + 2, dotY - dotRadius + 2, dotRadius * 2, dotRadius * 2);

        if (currentTurn == 1) {
            graphicsContext.setFill(Color.web("#1e90ff"));
        } else {
            graphicsContext.setFill(Color.web("#ff4c4c"));
        }
        graphicsContext.fillOval(dotX - dotRadius, dotY - dotRadius, dotRadius * 2, dotRadius * 2);

        graphicsContext.setFill(Color.color(1, 1, 1, 0.4));
        graphicsContext.fillOval(dotX - dotRadius + 3, dotY - dotRadius + 3, dotRadius * 0.4 * 2, dotRadius * 0.4 * 2);
    }
}