package pl.edu.tcs.tcsball.view.screen;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.model.GameView;
import pl.edu.tcs.tcsball.model.LobbyView;
import pl.edu.tcs.tcsball.net.discovery.DiscoveredHost;
import pl.edu.tcs.tcsball.view.RenderPlan;
import pl.edu.tcs.tcsball.view.element.ButtonRenderer;

// MOCK: brak prawdziwego połączenia — tylko lokalnie zapamiętany wybrany host
public class ClientLobbyScreen implements Screen {

    private final GraphicsContext gc;
    private final ButtonRenderer buttonRenderer;

    public static final double BACK_BTN_WIDTH = 150;
    public static final double BACK_BTN_HEIGHT = 50;
    public static final double BACK_BTN_X = 20;
    public static final double BACK_BTN_Y = GameConfig.WINDOW_HEIGHT - 70;

    public ClientLobbyScreen(GraphicsContext gc, ButtonRenderer buttonRenderer) {
        this.gc = gc;
        this.buttonRenderer = buttonRenderer;
    }

    @Override
    public void render(GameView game, RenderPlan plan) {
        gc.setFill(Color.web("#1a2e3d"));
        gc.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 44));
        gc.fillText("LOBBY", GameConfig.WINDOW_WIDTH / 2.0, 72);

        String hostLabel = "nieznany host";
        if (game instanceof LobbyView lobbyView) {
            DiscoveredHost joined = lobbyView.getJoinedHost();
            if (joined != null) {
                hostLabel = joined.getHostName();
            }
        }

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        gc.fillText("Host: " + hostLabel, GameConfig.WINDOW_WIDTH / 2.0, 160);

        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        gc.setFill(Color.web("#cccccc"));
        gc.fillText("[MOCK] czekam na start od hosta…", GameConfig.WINDOW_WIDTH / 2.0, 210);

        buttonRenderer.drawButton("OPUŚĆ", BACK_BTN_X, BACK_BTN_Y, BACK_BTN_WIDTH, BACK_BTN_HEIGHT,
                game.getActualMouseX(), game.getActualMouseY(),
                Color.web("#d9534f"), Color.web("#c9302c"));
    }

    public static boolean isBackButtonHit(double x, double y) {
        return x >= BACK_BTN_X && x <= BACK_BTN_X + BACK_BTN_WIDTH
                && y >= BACK_BTN_Y && y <= BACK_BTN_Y + BACK_BTN_HEIGHT;
    }
}
