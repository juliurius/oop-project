package pl.edu.tcs.tcsball.view.screen;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.model.GameView;

public class SettingsScreen implements Screen {

    private final GraphicsContext gc;

    public static final double BACK_BTN_WIDTH = 150;
    public static final double BACK_BTN_HEIGHT = 50;
    public static final double BACK_BTN_X = 20;
    public static final double BACK_BTN_Y = GameConfig.WINDOW_HEIGHT - 70;

    public SettingsScreen(GraphicsContext gc) {
        this.gc = gc;
    }

    @Override
    public void render(GameView game) {
        gc.setFill(Color.web("#2f4f4f"));
        gc.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 50));
        gc.fillText("USTAWIENIA", GameConfig.WINDOW_WIDTH / 2.0, 80);

        gc.setFill(Color.web("#d9534f"));
        gc.fillRoundRect(BACK_BTN_X, BACK_BTN_Y, BACK_BTN_WIDTH, BACK_BTN_HEIGHT, 15, 15);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gc.fillText("POWRÓT", BACK_BTN_X + BACK_BTN_WIDTH / 2.0, BACK_BTN_Y + BACK_BTN_HEIGHT / 2.0);
    }
}