package pl.edu.tcs.tcsball.view.screen;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.model.GameView;
import pl.edu.tcs.tcsball.view.RenderPlan;
import pl.edu.tcs.tcsball.view.element.ButtonRenderer;

public class MenuScreen implements Screen {

    private final GraphicsContext gc;
    private final ButtonRenderer buttonRenderer;

    private static final double START_BUTTON_OFFSET_Y = -40.0;
    private static final double SETTINGS_BUTTON_OFFSET_Y = 50.0;

    public static final double BTN_WIDTH = 200.0;
    public static final double BTN_HEIGHT = 60.0;
    public static final double BTN_X = (GameConfig.WINDOW_WIDTH - BTN_WIDTH) / 2.0;
    public static final double START_BTN_Y = GameConfig.WINDOW_HEIGHT / 2.0 + START_BUTTON_OFFSET_Y;
    public static final double SETTINGS_BTN_Y = GameConfig.WINDOW_HEIGHT / 2.0 + SETTINGS_BUTTON_OFFSET_Y;

    public MenuScreen(GraphicsContext gc, ButtonRenderer buttonRenderer) {
        this.gc = gc;
        this.buttonRenderer = buttonRenderer;
    }

    @Override
    public void render(GameView game, RenderPlan plan) {
        double mx = game.getActualMouseX();
        double my = game.getActualMouseY();

        gc.setFill(Color.web("#2e8b57"));
        gc.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 80));
        gc.fillText("TCS BALL", GameConfig.WINDOW_WIDTH / 2.0, GameConfig.WINDOW_HEIGHT / 3.0);

        buttonRenderer.drawButton("START", BTN_X, START_BTN_Y, BTN_WIDTH, BTN_HEIGHT, mx, my,
                Color.web("#1e90ff"), Color.web("#157dec"));

        buttonRenderer.drawButton("USTAWIENIA", BTN_X, SETTINGS_BTN_Y, BTN_WIDTH, BTN_HEIGHT, mx, my,
                Color.web("#708090"), Color.web("#5c6b73"));
    }
}