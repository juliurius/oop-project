package pl.edu.tcs.tcsball.view.element;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BallRenderer {
    GraphicsContext graphicsContext;

    public BallRenderer(GraphicsContext graphicsContext) { this.graphicsContext = graphicsContext; }

    public void drawBall(double x, double y) {
        double radius = 10.0;

        graphicsContext.setFill(Color.color(0, 0, 0, 0.5));
        graphicsContext.fillOval(x - radius + 3, y - radius + 3, radius * 2, radius * 2);

        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillOval(x - radius, y - radius, radius * 2, radius * 2);

        graphicsContext.setFill(Color.color(1, 1, 1, 0.8));
        double specularSize = radius * 0.4;
        graphicsContext.fillOval(x - radius + 3, y - radius + 3, specularSize * 2, specularSize * 2);
    }
}
