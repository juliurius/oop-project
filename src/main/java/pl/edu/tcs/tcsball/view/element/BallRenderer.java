package pl.edu.tcs.tcsball.view.element;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BallRenderer {
    GraphicsContext gc;

    public BallRenderer(GraphicsContext graphicsContext) { this.gc = graphicsContext; }

    public void drawBall(double x, double y, double radius, double angle) {

        gc.setFill(Color.color(0, 0, 0, 0.5));
        gc.fillOval(x - radius + 3, y - radius + 3, radius * 2, radius * 2);

        gc.save();

        gc.translate(x, y);
        gc.rotate(angle);

        gc.setFill(Color.WHITE);
        gc.fillOval(-radius, -radius, radius * 2, radius * 2);

        gc.setFill(Color.BLACK);

        double[] px = {0, 4, 2.5, -2.5, -4};
        double[] py = {-5, -2, 4, 4, -2};
        gc.fillPolygon(px, py, 5);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.strokeLine(0, -5, 0, -radius);
        gc.strokeLine(4, -2, radius * 0.9, -radius * 0.3);
        gc.strokeLine(2.5, 4, radius * 0.5, radius * 0.8);
        gc.strokeLine(-2.5, 4, -radius * 0.5, radius * 0.8);
        gc.strokeLine(-4, -2, -radius * 0.9, -radius * 0.3);

        gc.restore();

//        gc.setFill(Color.color(1, 1, 1, 0.8));
//        double specularSize = radius * 0.4;
//        gc.fillOval(x - radius + 3, y - radius + 3, specularSize * 2, specularSize * 2);
    }
}
