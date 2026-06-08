package pl.edu.tcs.tcsball.view.element;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class ButtonRenderer {
    private final GraphicsContext gc;

    public ButtonRenderer(GraphicsContext gc) {
        this.gc = gc;
    }

    public void drawButton(String text, double x, double y, double width, double height,
                           double mouseX, double mouseY,
                           Color baseColor, Color hoverColor) {
        drawButton(text, x, y, width, height, mouseX, mouseY, baseColor, hoverColor, true);
    }

    public void drawButton(String text, double x, double y, double width, double height,
                           double mouseX, double mouseY,
                           Color baseColor, Color hoverColor, boolean enabled) {

        boolean isHovered = enabled
                && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;

        Color disabledColor = Color.web("#555555");
        gc.setFill(enabled ? (isHovered ? hoverColor : baseColor) : disabledColor);
        gc.fillRoundRect(x, y, width, height, 15, 15);

        if (isHovered) {
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(3);
            gc.strokeRoundRect(x - 2, y - 2, width + 4, height + 4, 18, 18);
        }

        gc.setFill(enabled ? Color.WHITE : Color.web("#999999"));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        double fontSize = Math.min(24, height * 0.4);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
        gc.fillText(text, x + width / 2.0, y + height / 2.0);
    }
}