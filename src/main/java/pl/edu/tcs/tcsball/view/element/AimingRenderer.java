package pl.edu.tcs.tcsball.view.element;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.model.ReadOnlyPawn;

public class AimingRenderer {
    private static final double ARROW_LENGTH = 15.0;

    GraphicsContext graphicsContext;

    public AimingRenderer(GraphicsContext graphicsContext) { this.graphicsContext = graphicsContext; }

    public void drawAimingLine(ReadOnlyPawn aimingPawn, double mouseX, double mouseY) {
        if (aimingPawn != null) {
            double centerX = aimingPawn.getX();
            double centerY = aimingPawn.getY();
            double radius = aimingPawn.getRadius();

            double distance = Math.hypot(mouseX - centerX, mouseY - centerY);

            if (distance > radius) {
                double lineAngle = Math.atan2(mouseY - centerY, mouseX - centerX);
                double edgeX = centerX + radius * Math.cos(lineAngle);
                double edgeY = centerY + radius * Math.sin(lineAngle);

                graphicsContext.save();
                graphicsContext.beginPath();
                graphicsContext.rect(0, GameConfig.SCORE_PANEL_HEIGHT, GameConfig.WINDOW_WIDTH,
                        GameConfig.WINDOW_HEIGHT - GameConfig.SCORE_PANEL_HEIGHT);
                graphicsContext.clip();

                graphicsContext.setStroke(Color.WHITE);
                graphicsContext.setLineWidth(4.0);
                graphicsContext.setLineCap(StrokeLineCap.ROUND);
                graphicsContext.strokeLine(edgeX, edgeY, mouseX, mouseY);

                double arrowLength = ARROW_LENGTH;
                double arrowAngle = Math.PI / 6;

                double x1 = mouseX - arrowLength * Math.cos(lineAngle - arrowAngle);
                double y1 = mouseY - arrowLength * Math.sin(lineAngle - arrowAngle);

                double x2 = mouseX - arrowLength * Math.cos(lineAngle + arrowAngle);
                double y2 = mouseY - arrowLength * Math.sin(lineAngle + arrowAngle);

                graphicsContext.setLineWidth(3.0);
                graphicsContext.strokeLine(mouseX, mouseY, x1, y1);
                graphicsContext.strokeLine(mouseX, mouseY, x2, y2);

                graphicsContext.setLineCap(StrokeLineCap.SQUARE);
                graphicsContext.restore();
            }
        }
    }
}
