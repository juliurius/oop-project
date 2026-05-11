package tcsball.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Renderer {
    private final GraphicsContext gc;
    private final int width;
    private final int height;

    public Renderer(GraphicsContext gc, int width, int height) {
        this.gc = gc;
        this.width = width;
        this.height = height;
    }

    public void render() {
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 0, width, height);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeRect(50, 50, width - 100, height - 100);

        gc.setFill(Color.BLUE);
        gc.fillOval(380, 280, 40, 40);

        gc.setFill(Color.WHITE);
        gc.fillOval(390, 290, 20, 20);
    }
}