package pl.edu.tcs.tcsball.view.element;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class LobbyPlayerPanelRenderer {

    private final GraphicsContext gc;

    public LobbyPlayerPanelRenderer(GraphicsContext gc) {
        this.gc = gc;
    }

    public void drawPanel(double x, double y, double width, double height,
                          String roleLabel, String playerName, String flagName, String flagColor,
                          boolean occupied, boolean ready) {
        gc.setFill(Color.web("#2a4a3a"));
        gc.fillRoundRect(x, y, width, height, 14, 14);

        gc.setStroke(Color.web("#4a6a5a"));
        gc.setLineWidth(1.5);
        gc.strokeRoundRect(x, y, width, height, 14, 14);

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.TOP);
        gc.setFill(Color.web("#88aa99"));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gc.fillText(roleLabel, x + 16, y + 14);

        if (!occupied) {
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.setFill(Color.web("#888888"));
            gc.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
            gc.fillText("Czekam na gracza…", x + width / 2.0, y + height / 2.0);
            return;
        }

        gc.setFill(Color.web(flagColor));
        gc.fillOval(x + 16, y + 40, 36, 36);

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gc.fillText(playerName, x + 64, y + 58);

        gc.setFill(Color.web("#aaaaaa"));
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        gc.fillText(flagName, x + 64, y + 84);

        drawReadyBadge(x + width - 16, y + height - 16, ready);
    }

    private void drawReadyBadge(double rightX, double bottomY, boolean ready) {
        String label = ready ? "GOTOWY" : "NIE GOTOWY";
        Color bg = ready ? Color.web("#2d8a4e") : Color.web("#6a4a2a");
        Color fg = ready ? Color.web("#a8f0c0") : Color.web("#ffcc88");

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        double padX = 10;
        double padY = 5;
        double textW = label.length() * 7.5;
        double badgeW = textW + padX * 2;
        double badgeH = 22;
        double badgeX = rightX - badgeW;
        double badgeY = bottomY - badgeH;

        gc.setFill(bg);
        gc.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 8, 8);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFill(fg);
        gc.fillText(label, badgeX + badgeW / 2.0, badgeY + badgeH / 2.0);
    }
}
