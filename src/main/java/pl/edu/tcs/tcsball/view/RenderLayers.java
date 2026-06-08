package pl.edu.tcs.tcsball.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;

public class RenderLayers {

    private final Canvas backgroundCanvas;
    private final Canvas gameCanvas;
    private final Canvas uiCanvas;
    private final Canvas overlayCanvas;

    public RenderLayers(double width, double height) {
        backgroundCanvas = new Canvas(width, height);
        gameCanvas = new Canvas(width, height);
        uiCanvas = new Canvas(width, height);
        overlayCanvas = new Canvas(width, height);

        backgroundCanvas.setMouseTransparent(true);
        gameCanvas.setMouseTransparent(true);
        uiCanvas.setMouseTransparent(true);
        overlayCanvas.setMouseTransparent(true);
    }

    public StackPane asStackPane() {
        return new StackPane(backgroundCanvas, gameCanvas, uiCanvas, overlayCanvas);
    }

    public GraphicsContext backgroundGc() {
        return backgroundCanvas.getGraphicsContext2D();
    }

    public GraphicsContext gameGc() {
        return gameCanvas.getGraphicsContext2D();
    }

    public GraphicsContext uiGc() {
        return uiCanvas.getGraphicsContext2D();
    }

    public GraphicsContext overlayGc() {
        return overlayCanvas.getGraphicsContext2D();
    }

    public void clearAll() {
        double w = backgroundCanvas.getWidth();
        double h = backgroundCanvas.getHeight();
        backgroundGc().clearRect(0, 0, w, h);
        gameGc().clearRect(0, 0, w, h);
        uiGc().clearRect(0, 0, w, h);
        overlayGc().clearRect(0, 0, w, h);
    }
}
