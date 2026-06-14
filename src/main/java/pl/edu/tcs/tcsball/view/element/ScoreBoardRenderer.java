package pl.edu.tcs.tcsball.view.element;

import pl.edu.tcs.tcsball.view.UiTheme;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pl.edu.tcs.tcsball.GameConfig;

public class ScoreBoardRenderer {
    private final GraphicsContext graphicsContext;
    private final ButtonRenderer buttonRenderer;

    private static final double TURN_DOT_RADIUS = 12.0;
    private static final double TURN_DOT_OFFSET_X = 40.0;

    public static final double BACK_BTN_WIDTH = 100.0;
    public static final double BACK_BTN_HEIGHT = 40.0;
    public static final double BACK_BTN_X = 20.0;
    public static final double BACK_BTN_Y = 20.0;

    public ScoreBoardRenderer(GraphicsContext graphicsContext, ButtonRenderer buttonRenderer) {
        this.graphicsContext = graphicsContext;
        this.buttonRenderer = buttonRenderer;
    }

    public void drawScoreBoard(int score1, int score2, int currentTurn,
                               String team1Color, String team2Color,
                               double mouseX, double mouseY, boolean everythingStopped) {
        graphicsContext.setFill(Color.web("#1a1a1a"));
        graphicsContext.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.SCORE_PANEL_HEIGHT);

        graphicsContext.setTextAlign(TextAlignment.CENTER);
        graphicsContext.setTextBaseline(VPos.CENTER);
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(UiTheme.font(FontWeight.BOLD, 36));
        graphicsContext.fillText("WYNIK  " + score1 + " : " + score2, GameConfig.WINDOW_WIDTH / 2.0, GameConfig.SCORE_PANEL_HEIGHT / 2.0);

        buttonRenderer.drawButton("MENU", BACK_BTN_X, BACK_BTN_Y, BACK_BTN_WIDTH, BACK_BTN_HEIGHT,
                mouseX, mouseY,
                UiTheme.DANGER, UiTheme.DANGER_HOVER);

        double dotRadius = TURN_DOT_RADIUS;
        double dotX = GameConfig.WINDOW_WIDTH - TURN_DOT_OFFSET_X;
        double dotY = GameConfig.SCORE_PANEL_HEIGHT / 2.0;

        graphicsContext.setFill(Color.color(0, 0, 0, 0.5));
        graphicsContext.fillOval(dotX - dotRadius + 2, dotY - dotRadius + 2, dotRadius * 2, dotRadius * 2);

        String turnColor = currentTurn == 1 ? team1Color : team2Color;
        double turnOpacity = everythingStopped ? 1.0 : 0.4;
        graphicsContext.setFill(Color.web(turnColor, turnOpacity));

        graphicsContext.fillOval(dotX - dotRadius, dotY - dotRadius, dotRadius * 2, dotRadius * 2);

        graphicsContext.setFill(Color.color(1, 1, 1, 0.4));
        graphicsContext.fillOval(dotX - dotRadius + 3, dotY - dotRadius + 3, dotRadius * 0.4 * 2, dotRadius * 0.4 * 2);
    }
}