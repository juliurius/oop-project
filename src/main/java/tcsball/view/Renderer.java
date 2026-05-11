package tcsball.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import tcsball.model.Pawn;
import java.util.List;

public class Renderer {
    private final GraphicsContext gc;
    private final int width;
    private final int height;

    public Renderer(GraphicsContext gc, int width, int height) {
        this.gc = gc;
        this.width = width;
        this.height = height;
    }

    public void render(List<Pawn> pawns, Pawn aimingPawn, double mouseX, double mouseY) {
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 0, width, height);

        for (Pawn pawn : pawns) {
            double x = pawn.getPosition().getX();
            double y = pawn.getPosition().getY();
            double radius = pawn.getRadius();

            gc.setFill(Color.BLACK);
            gc.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
        }

        if (aimingPawn != null) {
            double startX = aimingPawn.getPosition().getX();
            double startY = aimingPawn.getPosition().getY();

            gc.setStroke(Color.WHITE);
            gc.setLineWidth(3.0);

            gc.strokeLine(startX, startY, mouseX, mouseY);
        }
    }
}