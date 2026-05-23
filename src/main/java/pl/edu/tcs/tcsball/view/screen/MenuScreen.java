package pl.edu.tcs.tcsball.view.screen;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.model.GameView;

public class MenuScreen implements Screen {

    private final GraphicsContext gc;

    public static final double BTN_WIDTH = 200;
    public static final double BTN_HEIGHT = 60;
    public static final double BTN_X = (GameConfig.WINDOW_WIDTH - BTN_WIDTH) / 2.0;
    public static final double BTN_Y = GameConfig.WINDOW_HEIGHT / 2.0;

    public MenuScreen(GraphicsContext gc) {
        this.gc = gc;
    }

    @Override
    public void render(GameView game) {
        gc.setFill(Color.web("#2e8b57"));
        gc.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 80));
        gc.fillText("TCS BALL", GameConfig.WINDOW_WIDTH / 2.0, GameConfig.WINDOW_HEIGHT / 3.0);

        gc.setFill(Color.web("#1e90ff"));
        gc.fillRoundRect(BTN_X, BTN_Y, BTN_WIDTH, BTN_HEIGHT, 20, 20);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gc.fillText("START", GameConfig.WINDOW_WIDTH / 2.0, BTN_Y + BTN_HEIGHT / 2.0);
    }
}