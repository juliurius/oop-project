package pl.edu.tcs.tcsball.view.element;

import pl.edu.tcs.tcsball.view.UiTheme;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pl.edu.tcs.tcsball.model.ReadOnlyPawn;

public class PawnRenderer {
    GraphicsContext graphicsContext;

    public PawnRenderer(GraphicsContext graphicsContext) { this.graphicsContext = graphicsContext; }

    public void drawPawn(ReadOnlyPawn pawn, String outerColor, String innerColor) {
        double x = pawn.getX();
        double y = pawn.getY();
        double radius = pawn.getRadius();

        graphicsContext.setFill(Color.color(0, 0, 0, 0.4));
        graphicsContext.fillOval(x - radius + 4, y - radius + 4, radius * 2, radius * 2);

        graphicsContext.setFill(Color.web(outerColor));
        graphicsContext.fillOval(x - radius, y - radius, radius * 2, radius * 2);

        graphicsContext.setFill(Color.web(innerColor));
        double innerRadius = radius * 0.7;
        graphicsContext.fillOval(x - innerRadius, y - innerRadius, innerRadius * 2, innerRadius * 2);

        graphicsContext.setTextAlign(TextAlignment.CENTER);
        graphicsContext.setTextBaseline(VPos.CENTER);
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(UiTheme.font(FontWeight.BOLD, 18));
        graphicsContext.fillText(String.valueOf(pawn.getTeam()), x, y);
    }
}
