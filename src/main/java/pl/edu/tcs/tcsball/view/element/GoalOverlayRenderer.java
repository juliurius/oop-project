package pl.edu.tcs.tcsball.view.element;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.view.ConfettiSystem;

public class GoalOverlayRenderer {

    private final GraphicsContext graphicsContext;
    private final ConfettiSystem confetti;
    private long goalStartTime = 0;

    public GoalOverlayRenderer(GraphicsContext graphicsContext, ConfettiSystem confetti) {
        this.graphicsContext = graphicsContext;
        this.confetti = confetti;
    }

    public void start() {
        goalStartTime = System.currentTimeMillis();
        confetti.spawn();
    }

    public void stop() {
        // confetti.stop();
    }

    public void drawGoalOverlay() {
        long elapsed = System.currentTimeMillis() - goalStartTime;
        double seconds = elapsed / 1000.0;

        graphicsContext.setGlobalAlpha(Math.min(0.7, seconds));
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        graphicsContext.setGlobalAlpha(1.0);

        confetti.updateAndDraw(graphicsContext);

        drawGoalText(seconds);
    }

    private void drawGoalText(double seconds) {
        double scale = 0.8 + Math.sin(seconds * 10) * 0.1;
        if (seconds < 0.5) scale = seconds * 2;

        graphicsContext.save();
        graphicsContext.translate(GameConfig.WINDOW_WIDTH / 2.0, GameConfig.WINDOW_HEIGHT / 2.0);
        graphicsContext.scale(scale, scale);

        graphicsContext.setTextAlign(TextAlignment.CENTER);
        graphicsContext.setTextBaseline(VPos.CENTER);
        graphicsContext.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 120));

        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.setLineWidth(10);
        graphicsContext.strokeText("GOL !!", 0, 0);

        graphicsContext.setFill((int)(seconds * 10) % 2 == 0 ? Color.YELLOW : Color.GOLD);
        graphicsContext.fillText("GOL !!", 0, 0);

        graphicsContext.restore();
    }
}
