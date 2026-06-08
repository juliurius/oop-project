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
import pl.edu.tcs.tcsball.view.RenderPlan;
import pl.edu.tcs.tcsball.view.element.ButtonRenderer;
import pl.edu.tcs.tcsball.view.element.LobbyPlayerPanelRenderer;

public class HostLobbyScreen implements Screen {

    public static final double PANEL_WIDTH = 250.0;
    public static final double PANEL_HEIGHT = 130.0;
    public static final double PANEL_GAP = 36.0;
    public static final double PANEL_TOP_Y = 145.0;

    public static final double READY_BTN_WIDTH = 180.0;
    public static final double READY_BTN_HEIGHT = 50.0;
    public static final double START_BTN_WIDTH = 180.0;
    public static final double START_BTN_HEIGHT = 50.0;
    public static final double ACTION_BTN_Y = 340.0;

    public static final double BACK_BTN_WIDTH = 150;
    public static final double BACK_BTN_HEIGHT = 50;
    public static final double BACK_BTN_X = 20;
    public static final double BACK_BTN_Y = GameConfig.WINDOW_HEIGHT - 70;

    private final GraphicsContext gc;
    private final ButtonRenderer buttonRenderer;
    private final LobbyPlayerPanelRenderer panelRenderer;

    public HostLobbyScreen(GraphicsContext gc, ButtonRenderer buttonRenderer) {
        this.gc = gc;
        this.buttonRenderer = buttonRenderer;
        this.panelRenderer = new LobbyPlayerPanelRenderer(gc);
    }

    @Override
    public void render(GameView game, RenderPlan plan) {
        double mx = game.getActualMouseX();
        double my = game.getActualMouseY();

        gc.setFill(Color.web("#1a3d2e"));
        gc.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 44));
        gc.fillText("TWOJE LOBBY", GameConfig.WINDOW_WIDTH / 2.0, 72);

        if (!(game instanceof LobbyView lobby)) {
            return;
        }

        double totalWidth = PANEL_WIDTH * 2 + PANEL_GAP;
        double leftX = (GameConfig.WINDOW_WIDTH - totalWidth) / 2.0;
        double rightX = leftX + PANEL_WIDTH + PANEL_GAP;

        panelRenderer.drawPanel(leftX, PANEL_TOP_Y, PANEL_WIDTH, PANEL_HEIGHT,
                "HOST (TY)", lobby.getLocalPlayerName(),
                lobby.getLocalPlayerFlagName(), lobby.getLocalPlayerFlagColor(),
                true, lobby.isLocalPlayerReady());

        panelRenderer.drawPanel(rightX, PANEL_TOP_Y, PANEL_WIDTH, PANEL_HEIGHT,
                "GOŚĆ",
                lobby.hasOpponent() ? lobby.getOpponentName() : "",
                lobby.getOpponentFlagName(), lobby.getOpponentFlagColor(),
                lobby.hasOpponent(), lobby.isOpponentReady());

        drawStatusMessage(lobby);

        double readyX = GameConfig.WINDOW_WIDTH / 2.0 - READY_BTN_WIDTH - 12;
        double startX = GameConfig.WINDOW_WIDTH / 2.0 + 12;
        boolean canStart = lobby.canStartGame();

        String readyLabel = lobby.isLocalPlayerReady() ? "NIE GOTOWY" : "GOTOWY";
        buttonRenderer.drawButton(readyLabel, readyX, ACTION_BTN_Y, READY_BTN_WIDTH, READY_BTN_HEIGHT,
                mx, my, Color.web("#4682b4"), Color.web("#357abd"));

        buttonRenderer.drawButton("START", startX, ACTION_BTN_Y, START_BTN_WIDTH, START_BTN_HEIGHT,
                mx, my, Color.web("#32cd32"), Color.web("#28a428"), canStart);

        buttonRenderer.drawButton("ANULUJ", BACK_BTN_X, BACK_BTN_Y, BACK_BTN_WIDTH, BACK_BTN_HEIGHT,
                mx, my, Color.web("#d9534f"), Color.web("#c9302c"));
    }

    private void drawStatusMessage(LobbyView lobby) {
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        gc.setFill(Color.web("#cccccc"));

        String message;
        if (!lobby.hasOpponent()) {
            message = "Oczekiwanie na dołączenie gracza…";
        } else if (!lobby.isLocalPlayerReady() || !lobby.isOpponentReady()) {
            message = "Oznaczcie gotowość, aby rozpocząć mecz";
        } else {
            message = "Wszyscy gotowi — kliknij START";
        }
        gc.fillText(message, GameConfig.WINDOW_WIDTH / 2.0, 300);
    }

    public static double readyBtnX() {
        return GameConfig.WINDOW_WIDTH / 2.0 - READY_BTN_WIDTH - 12;
    }

    public static double startBtnX() {
        return GameConfig.WINDOW_WIDTH / 2.0 + 12;
    }

    public static boolean isReadyButtonHit(double x, double y) {
        return x >= readyBtnX() && x <= readyBtnX() + READY_BTN_WIDTH
                && y >= ACTION_BTN_Y && y <= ACTION_BTN_Y + READY_BTN_HEIGHT;
    }

    public static boolean isStartButtonHit(double x, double y, boolean enabled) {
        return enabled
                && x >= startBtnX() && x <= startBtnX() + START_BTN_WIDTH
                && y >= ACTION_BTN_Y && y <= ACTION_BTN_Y + START_BTN_HEIGHT;
    }

    public static boolean isBackButtonHit(double x, double y) {
        return x >= BACK_BTN_X && x <= BACK_BTN_X + BACK_BTN_WIDTH
                && y >= BACK_BTN_Y && y <= BACK_BTN_Y + BACK_BTN_HEIGHT;
    }
}
