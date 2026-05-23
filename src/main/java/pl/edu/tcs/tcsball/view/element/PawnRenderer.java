package pl.edu.tcs.tcsball.view.element;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pl.edu.tcs.tcsball.model.Pawn;

public class PawnRenderer {
    GraphicsContext graphicsContext;

    public PawnRenderer(GraphicsContext graphicsContext) { this.graphicsContext = graphicsContext; }

    public void drawPawn(Pawn pawn) {
        double x = pawn.getPosition().getX();
        double y = pawn.getPosition().getY();
        double radius = pawn.getRadius();

        graphicsContext.setFill(Color.color(0, 0, 0, 0.4));
        graphicsContext.fillOval(x - radius + 4, y - radius + 4, radius * 2, radius * 2);

        if (pawn.getTeam() == 1) {
            graphicsContext.setFill(Color.web("#1e90ff"));
            graphicsContext.fillOval(x - radius, y - radius, radius * 2, radius * 2);

            graphicsContext.setFill(Color.web("#4682b4"));
            double innerRadius = radius * 0.7;
            graphicsContext.fillOval(x - innerRadius, y - innerRadius, innerRadius * 2, innerRadius * 2);

        } else if (pawn.getTeam() == 2) {
            graphicsContext.setFill(Color.web("#ff4c4c"));
            graphicsContext.fillOval(x - radius, y - radius, radius * 2, radius * 2);

            graphicsContext.setFill(Color.web("#b30000"));
            double innerRadius = radius * 0.7;
            graphicsContext.fillOval(x - innerRadius, y - innerRadius, innerRadius * 2, innerRadius * 2);
        }

        graphicsContext.setTextAlign(TextAlignment.CENTER);
        graphicsContext.setTextBaseline(VPos.CENTER);
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        //gc.fillText(String.valueOf(pawn.getNumber()), x, y);

        graphicsContext.fillText(String.valueOf(67), x, y);
    }
}
