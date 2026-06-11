package pl.edu.tcs.tcsball.view.screen;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.controller.GameView;
import pl.edu.tcs.tcsball.controller.LobbyView;
import pl.edu.tcs.tcsball.net.discovery.DiscoveredHost;
import pl.edu.tcs.tcsball.view.RenderPlan;
import pl.edu.tcs.tcsball.view.element.ButtonRenderer;

import java.util.List;

public class JoinLobbyScreen implements Screen {

    private final GraphicsContext gc;
    private final ButtonRenderer buttonRenderer;

    public static final double ROW_WIDTH = 520.0;
    public static final double ROW_HEIGHT = 52.0;
    public static final double ROW_GAP = 10.0;
    public static final double LIST_TOP_Y = 145.0;
    public static final double ROW_X = (GameConfig.WINDOW_WIDTH - ROW_WIDTH) / 2.0;

    public static final double REFRESH_BTN_WIDTH = 150.0;
    public static final double REFRESH_BTN_HEIGHT = 44.0;
    public static final double REFRESH_BTN_X = GameConfig.WINDOW_WIDTH - REFRESH_BTN_WIDTH - 24;
    public static final double REFRESH_BTN_Y = 28.0;

    public static final double BACK_BTN_WIDTH = 150;
    public static final double BACK_BTN_HEIGHT = 50;
    public static final double BACK_BTN_X = 20;
    public static final double BACK_BTN_Y = GameConfig.WINDOW_HEIGHT - 70;

    public JoinLobbyScreen(GraphicsContext gc, ButtonRenderer buttonRenderer) {
        this.gc = gc;
        this.buttonRenderer = buttonRenderer;
    }

    @Override
    public void render(GameView game, RenderPlan plan) {
        double mx = game.getActualMouseX();
        double my = game.getActualMouseY();

        gc.setFill(Color.web("#1a2e3d"));
        gc.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 44));
        gc.fillText("WYBIERZ HOSTA", GameConfig.WINDOW_WIDTH / 2.0, 72);

        if (!(game instanceof LobbyView lobbyView && lobbyView.isJoinPending())) {
            buttonRenderer.drawButton("ODŚWIEŻ", REFRESH_BTN_X, REFRESH_BTN_Y, REFRESH_BTN_WIDTH, REFRESH_BTN_HEIGHT,
                    mx, my, Color.web("#4682b4"), Color.web("#357abd"));
        }

        if (game instanceof LobbyView lobbyView) {
            if (lobbyView.isJoinPending()) {
                gc.setFont(Font.font("Arial", FontWeight.NORMAL, 22));
                gc.setFill(Color.web("#cccccc"));
                String hostName = lobbyView.getJoinedHost() != null
                        ? lobbyView.getJoinedHost().getHostName()
                        : "hosta";
                gc.fillText("Łączenie z " + hostName + "…", GameConfig.WINDOW_WIDTH / 2.0, LIST_TOP_Y + 40);
            } else if (lobbyView.getJoinStatusMessage() != null) {
                gc.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
                gc.setFill(Color.web("#ff9999"));
                gc.fillText(lobbyView.getJoinStatusMessage(), GameConfig.WINDOW_WIDTH / 2.0, LIST_TOP_Y - 20);

                List<DiscoveredHost> hosts = lobbyView.getDiscoveredHosts();
                for (int i = 0; i < hosts.size(); i++) {
                    drawHostRow(hosts.get(i), i, mx, my);
                }
            } else {
                List<DiscoveredHost> hosts = lobbyView.getDiscoveredHosts();
                if (hosts.isEmpty()) {
                    gc.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
                    gc.setFill(Color.web("#aaaaaa"));
                    gc.fillText("Brak hostów — kliknij ODŚWIEŻ", GameConfig.WINDOW_WIDTH / 2.0, LIST_TOP_Y + 40);
                } else {
                    for (int i = 0; i < hosts.size(); i++) {
                        drawHostRow(hosts.get(i), i, mx, my);
                    }
                }
            }
        }

        buttonRenderer.drawButton("WSTECZ", BACK_BTN_X, BACK_BTN_Y, BACK_BTN_WIDTH, BACK_BTN_HEIGHT,
                mx, my, Color.web("#d9534f"), Color.web("#c9302c"));
    }

    private void drawHostRow(DiscoveredHost host, int index, double mouseX, double mouseY) {
        double rowY = rowY(index);
        boolean hovered = isHostRowHit(mouseX, mouseY, index);
        boolean joinable = host.isJoinable();

        if (!joinable) {
            gc.setFill(Color.web("#3a3a3a"));
        } else if (hovered) {
            gc.setFill(Color.web("#2a5a8a"));
        } else {
            gc.setFill(Color.web("#2a4a6a"));
        }

        gc.fillRoundRect(ROW_X, rowY, ROW_WIDTH, ROW_HEIGHT, 12, 12);

        if (hovered && joinable) {
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeRoundRect(ROW_X, rowY, ROW_WIDTH, ROW_HEIGHT, 12, 12);
        }

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFill(joinable ? Color.WHITE : Color.web("#888888"));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        gc.fillText(host.getHostName(), ROW_X + 20, rowY + ROW_HEIGHT / 2.0);

        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        gc.fillText(host.getStatusLabel(), ROW_X + ROW_WIDTH - 20, rowY + ROW_HEIGHT / 2.0);
    }

    public static double rowY(int index) {
        return LIST_TOP_Y + index * (ROW_HEIGHT + ROW_GAP);
    }

    public static boolean isHostRowHit(double x, double y, int index) {
        double rowY = rowY(index);
        return x >= ROW_X && x <= ROW_X + ROW_WIDTH
                && y >= rowY && y <= rowY + ROW_HEIGHT;
    }

    public static int hostIndexAt(double x, double y, int hostCount) {
        for (int i = 0; i < hostCount; i++) {
            if (isHostRowHit(x, y, i)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isBackButtonHit(double x, double y) {
        return x >= BACK_BTN_X && x <= BACK_BTN_X + BACK_BTN_WIDTH
                && y >= BACK_BTN_Y && y <= BACK_BTN_Y + BACK_BTN_HEIGHT;
    }

    public static boolean isRefreshButtonHit(double x, double y) {
        return x >= REFRESH_BTN_X && x <= REFRESH_BTN_X + REFRESH_BTN_WIDTH
                && y >= REFRESH_BTN_Y && y <= REFRESH_BTN_Y + REFRESH_BTN_HEIGHT;
    }
}
