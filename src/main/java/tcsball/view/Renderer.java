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

    public void render(List<Pawn> pawns) {
        // 1. Czyszczenie tła z poprzedniej klatki
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 0, width, height);

        // 2. Rysowanie pionków
        for (Pawn pawn : pawns) {
            // TODO 1: Pobierz pozycję i promień z obiektu 'pawn'
            // TODO 2: Narysuj pionek używając gc.fillOval() (pamiętaj o wyrównaniu do środka, a nie rogu)
        }

        // TODO 3: Rysowanie strzałki celowania, gdy gracz naciąga strzał
    }
}