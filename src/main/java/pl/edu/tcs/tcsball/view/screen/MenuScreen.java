package pl.edu.tcs.tcsball.view.screen;

import pl.edu.tcs.tcsball.view.UiTheme;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.controller.GameView;
import pl.edu.tcs.tcsball.view.RenderPlan;
import pl.edu.tcs.tcsball.view.element.ButtonRenderer;

public class MenuScreen implements Screen {

    private final GraphicsContext gc;
    private final ButtonRenderer buttonRenderer;

    private static final double TITLE_Y = 95.0;
    private static final double TITLE_FONT_SIZE = 64.0;
    private static final double BUTTON_GAP = 16.0;
    private static final double BUTTON_BLOCK_TOP = 220.0;

    public static final double BTN_WIDTH = 240.0;
    public static final double BTN_HEIGHT = 50.0;
    public static final double BTN_X = (GameConfig.WINDOW_WIDTH - BTN_WIDTH) / 2.0;

    public static final double LOCAL_PLAY_BTN_Y = BUTTON_BLOCK_TOP;
    public static final double HOST_BTN_Y = BUTTON_BLOCK_TOP + BTN_HEIGHT + BUTTON_GAP;
    public static final double JOIN_BTN_Y = HOST_BTN_Y + BTN_HEIGHT + BUTTON_GAP;
    public static final double CUSTOMIZATION_BTN_Y = JOIN_BTN_Y + BTN_HEIGHT + BUTTON_GAP;

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
        gc.setFont(UiTheme.font(FontWeight.EXTRA_BOLD, TITLE_FONT_SIZE));
        gc.fillText("TCS BALL", GameConfig.WINDOW_WIDTH / 2.0, TITLE_Y);

        buttonRenderer.drawButton("LOCAL PLAY", BTN_X, LOCAL_PLAY_BTN_Y, BTN_WIDTH, BTN_HEIGHT, mx, my,
                Color.web("#1e90ff"), Color.web("#157dec"));

        buttonRenderer.drawButton("HOST", BTN_X, HOST_BTN_Y, BTN_WIDTH, BTN_HEIGHT, mx, my,
                UiTheme.SUCCESS, UiTheme.SUCCESS_HOVER);

        buttonRenderer.drawButton("JOIN", BTN_X, JOIN_BTN_Y, BTN_WIDTH, BTN_HEIGHT, mx, my,
                Color.web("#ffa500"), Color.web("#e69500"));

        buttonRenderer.drawButton("CUSTOMIZACJA", BTN_X, CUSTOMIZATION_BTN_Y, BTN_WIDTH, BTN_HEIGHT, mx, my,
                Color.web("#708090"), Color.web("#5c6b73"));
    }

    public static boolean isButtonHit(double x, double y, double buttonY) {
        return x >= BTN_X && x <= BTN_X + BTN_WIDTH
                && y >= buttonY && y <= buttonY + BTN_HEIGHT;
    }
}
