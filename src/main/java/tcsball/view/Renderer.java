package tcsball.view;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import tcsball.GameConfig;
import tcsball.model.Pawn;

import java.util.List;

public class Renderer {
    private final GraphicsContext gc;

    private final ConfettiSystem confetti = new ConfettiSystem();
    private boolean goalMessageVisible = false;
    private long goalStartTime = 0;

    public Renderer(GraphicsContext gc, int width, int height) {
        this.gc = gc;
    }

    public void render(List<Pawn> pawns, int score1, int score2, double ballX, double ballY, Pawn aimingPawn, double mouseX, double mouseY) {
        drawScoreBoard(score1, score2);
        drawPitch();
        drawBall(ballX, ballY);

        for (Pawn pawn : pawns) {
            drawPawn(pawn);
        }

        drawAimingLine(aimingPawn, mouseX, mouseY);

        if (goalMessageVisible) {
            drawGoalOverlay();
        }
    }

    private void drawScoreBoard(int score1, int score2) {
        gc.setFill(Color.web("#1a1a1a"));
        gc.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.SCORE_PANEL_HEIGHT);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        gc.fillText("WYNIK  " + score1 + " : " + score2, GameConfig.WINDOW_WIDTH / 2.0, GameConfig.SCORE_PANEL_HEIGHT / 2.0);
    }

    private void drawPitch() {
        double pitchHeight = GameConfig.WINDOW_HEIGHT - GameConfig.SCORE_PANEL_HEIGHT;

        gc.setFill(Color.web("#2e8b57"));
        gc.fillRect(0, GameConfig.SCORE_PANEL_HEIGHT, GameConfig.WINDOW_WIDTH, pitchHeight);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.setGlobalAlpha(0.6);

        gc.strokeRect(
                GameConfig.PITCH_LEFT_X,
                GameConfig.PITCH_TOP_Y,
                GameConfig.WINDOW_WIDTH - 2 * GameConfig.MARGIN_X,
                pitchHeight - 2 * GameConfig.MARGIN_Y
        );

        gc.strokeLine(GameConfig.WINDOW_WIDTH / 2.0, GameConfig.PITCH_TOP_Y, GameConfig.WINDOW_WIDTH / 2.0, GameConfig.PITCH_BOTTOM_Y);
        gc.strokeOval(GameConfig.WINDOW_WIDTH / 2.0 - 50, GameConfig.SCORE_PANEL_HEIGHT + pitchHeight / 2.0 - 50, 100, 100);

        drawGoals();

        gc.setGlobalAlpha(1.0);
    }

    private void drawBall(double x, double y) {
        double radius = 10.0;

        gc.setFill(Color.color(0, 0, 0, 0.5));
        gc.fillOval(x - radius + 3, y - radius + 3, radius * 2, radius * 2);

        gc.setFill(Color.WHITE);
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);

        gc.setFill(Color.color(1, 1, 1, 0.8));
        double specularSize = radius * 0.4;
        gc.fillOval(x - radius + 3, y - radius + 3, specularSize * 2, specularSize * 2);
    }

    private void drawGoals() {
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.setFill(Color.web("#1c5936"));

        gc.fillRect(GameConfig.PITCH_LEFT_X - GameConfig.GOAL_WIDTH, GameConfig.GOAL_TOP_Y, GameConfig.GOAL_WIDTH, GameConfig.GOAL_HEIGHT);
        gc.strokeRect(GameConfig.PITCH_LEFT_X - GameConfig.GOAL_WIDTH, GameConfig.GOAL_TOP_Y, GameConfig.GOAL_WIDTH, GameConfig.GOAL_HEIGHT);

        gc.fillRect(GameConfig.PITCH_RIGHT_X, GameConfig.GOAL_TOP_Y, GameConfig.GOAL_WIDTH, GameConfig.GOAL_HEIGHT);
        gc.strokeRect(GameConfig.PITCH_RIGHT_X, GameConfig.GOAL_TOP_Y, GameConfig.GOAL_WIDTH, GameConfig.GOAL_HEIGHT);
    }

    private void drawPawn(Pawn pawn) {
        double x = pawn.getPosition().getX();
        double y = pawn.getPosition().getY();
        double radius = pawn.getRadius();

        gc.setFill(Color.color(0, 0, 0, 0.4));
        gc.fillOval(x - radius + 4, y - radius + 4, radius * 2, radius * 2);

        if (pawn.getTeam() == 1) {
            gc.setFill(Color.web("#1e90ff"));
            gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);

            gc.setFill(Color.web("#4682b4"));
            double innerRadius = radius * 0.7;
            gc.fillOval(x - innerRadius, y - innerRadius, innerRadius * 2, innerRadius * 2);

        } else if (pawn.getTeam() == 2) {
            gc.setFill(Color.web("#ff4c4c"));
            gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);

            gc.setFill(Color.web("#b30000"));
            double innerRadius = radius * 0.7;
            gc.fillOval(x - innerRadius, y - innerRadius, innerRadius * 2, innerRadius * 2);
        }

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        //gc.fillText(String.valueOf(pawn.getNumber()), x, y);

        gc.fillText(String.valueOf(67), x, y);
    }

    private void drawAimingLine(Pawn aimingPawn, double mouseX, double mouseY) {
        if (aimingPawn != null) {
            double centerX = aimingPawn.getPosition().getX();
            double centerY = aimingPawn.getPosition().getY();
            double radius = aimingPawn.getRadius();

            double distance = Math.hypot(mouseX - centerX, mouseY - centerY);

            if (distance > radius) {
                double lineAngle = Math.atan2(mouseY - centerY, mouseX - centerX);
                double edgeX = centerX + radius * Math.cos(lineAngle);
                double edgeY = centerY + radius * Math.sin(lineAngle);

                gc.setStroke(Color.WHITE);
                gc.setLineWidth(4.0);
                gc.setLineCap(StrokeLineCap.ROUND);
                gc.strokeLine(edgeX, edgeY, mouseX, mouseY);

                double arrowLength = 15.0;
                double arrowAngle = Math.PI / 6;

                double x1 = mouseX - arrowLength * Math.cos(lineAngle - arrowAngle);
                double y1 = mouseY - arrowLength * Math.sin(lineAngle - arrowAngle);

                double x2 = mouseX - arrowLength * Math.cos(lineAngle + arrowAngle);
                double y2 = mouseY - arrowLength * Math.sin(lineAngle + arrowAngle);

                gc.setLineWidth(3.0);
                gc.strokeLine(mouseX, mouseY, x1, y1);
                gc.strokeLine(mouseX, mouseY, x2, y2);

                gc.setLineCap(StrokeLineCap.SQUARE);
            }
        }
    }

    public void setGoalMessageVisible(boolean visible) {
        if (visible && !this.goalMessageVisible) {
            this.goalStartTime = System.currentTimeMillis();
            confetti.spawn();
        }

        if (!visible && this.goalMessageVisible) {
            confetti.stop();
        }

        this.goalMessageVisible = visible;
    }

    private void drawGoalOverlay() {
        long elapsed = System.currentTimeMillis() - goalStartTime;
        double seconds = elapsed / 1000.0;

        gc.setGlobalAlpha(Math.min(0.7, seconds));
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        gc.setGlobalAlpha(1.0);

        confetti.updateAndDraw(gc);

        drawGoalText(seconds);
    }

    private void drawGoalText(double seconds) {
        double scale = 0.8 + Math.sin(seconds * 10) * 0.1;
        if (seconds < 0.5) scale = seconds * 2;

        gc.save();
        gc.translate(GameConfig.WINDOW_WIDTH / 2.0, GameConfig.WINDOW_HEIGHT / 2.0);
        gc.scale(scale, scale);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 120));

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(10);
        gc.strokeText("GOL !!", 0, 0);

        gc.setFill((int)(seconds * 10) % 2 == 0 ? Color.YELLOW : Color.GOLD);
        gc.fillText("GOL !!", 0, 0);

        gc.restore();
    }
}