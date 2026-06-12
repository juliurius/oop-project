package pl.edu.tcs.tcsball.view.screen;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.controller.CustomizationView;
import pl.edu.tcs.tcsball.controller.GameView;
import pl.edu.tcs.tcsball.model.player.PlayerProfile;
import pl.edu.tcs.tcsball.view.RenderPlan;
import pl.edu.tcs.tcsball.view.element.ButtonRenderer;
import pl.edu.tcs.tcsball.view.element.FlagColors;

public class CustomizationScreen implements Screen {

    public enum Field {
        FLAG,
        FORMATION
    }

    private static final String NAME_PLACEHOLDER = "wpisz imię";

    // Jedyny stan UI, który zostaje w ekranie (czysty fokus pola tekstowego).
    private static boolean nameFieldFocused = true;

    private static final double ROW_CENTER_X = GameConfig.WINDOW_WIDTH / 2.0;
    private static final double ARROW_WIDTH = 48.0;
    private static final double ARROW_HEIGHT = 48.0;
    private static final double VALUE_WIDTH = 220.0;
    private static final double PREV_ARROW_X = ROW_CENTER_X - VALUE_WIDTH / 2.0 - ARROW_WIDTH - 12.0;
    private static final double NEXT_ARROW_X = ROW_CENTER_X + VALUE_WIDTH / 2.0 + 12.0;

    public static final double NAME_ROW_Y = 175.0;
    public static final double NAME_FIELD_WIDTH = 320.0;
    public static final double NAME_FIELD_HEIGHT = 48.0;
    public static final double NAME_FIELD_X = ROW_CENTER_X - NAME_FIELD_WIDTH / 2.0;
    public static final double FLAG_ROW_Y = 275.0;
    public static final double FORMATION_ROW_Y = 375.0;

    public static final double BACK_BTN_WIDTH = 150;
    public static final double BACK_BTN_HEIGHT = 50;
    public static final double BACK_BTN_X = 20;
    public static final double BACK_BTN_Y = GameConfig.WINDOW_HEIGHT - 70;

    private final GraphicsContext gc;
    private final ButtonRenderer buttonRenderer;

    public CustomizationScreen(GraphicsContext gc, ButtonRenderer buttonRenderer) {
        this.gc = gc;
        this.buttonRenderer = buttonRenderer;
    }

    @Override
    public void onEnter() {
        nameFieldFocused = true;
    }

    public static boolean handleClick(double x, double y) {
        boolean wasFocused = nameFieldFocused;
        nameFieldFocused = isNameFieldHit(x, y);
        return wasFocused != nameFieldFocused;
    }

    public static boolean isNameFieldFocused() {
        return nameFieldFocused;
    }

    /** Kolor kółka flagi po kodzie. */
    public static String flagColor(String code) {
        return FlagColors.forCode(code);
    }

    @Override
    public void render(GameView game, RenderPlan plan) {
        double mx = game.getActualMouseX();
        double my = game.getActualMouseY();

        String playerName = "";
        String flagName = "";
        String flagCode = "";
        String formationName = "";
        if (game instanceof CustomizationView view) {
            playerName = view.getPlayerName();
            flagName = view.getCurrentFlagName();
            flagCode = view.getCurrentFlagCode();
            formationName = view.getCurrentFormationName();
        }

        gc.setFill(Color.web("#2f4f4f"));
        gc.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 44));
        gc.fillText("CUSTOMIZACJA", ROW_CENTER_X, 72);

        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        gc.setFill(Color.web("#cccccc"));
        gc.fillText("Profil zapisywany przed meczem — zmiany tylko tutaj", ROW_CENTER_X, 118);

        drawNameInputRow(playerName, mx, my);
        drawSelectorRow(Field.FLAG, "FLAGA", flagName, FlagColors.forCode(flagCode), mx, my);
        drawSelectorRow(Field.FORMATION, "FORMACJA", formationName, null, mx, my);

        buttonRenderer.drawButton("POWRÓT", BACK_BTN_X, BACK_BTN_Y, BACK_BTN_WIDTH, BACK_BTN_HEIGHT,
                mx, my, Color.web("#d9534f"), Color.web("#c9302c"));
    }

    private void drawNameInputRow(String playerName, double mouseX, double mouseY) {
        double rowY = NAME_ROW_Y;
        double fieldTop = rowY - NAME_FIELD_HEIGHT / 2.0;
        boolean hovered = isNameFieldHit(mouseX, mouseY);

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Color.web("#aaaaaa"));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.fillText("NAZWA", 80, rowY - 34);

        Color border = nameFieldFocused
                ? Color.web("#6a9fd8")
                : (hovered ? Color.web("#5a7a9a") : Color.web("#4a6a8a"));
        gc.setStroke(border);
        gc.setLineWidth(nameFieldFocused ? 2.5 : 1.5);
        gc.setFill(Color.web("#3a4a5a"));
        gc.fillRoundRect(NAME_FIELD_X, fieldTop, NAME_FIELD_WIDTH, NAME_FIELD_HEIGHT, 10, 10);
        gc.strokeRoundRect(NAME_FIELD_X, fieldTop, NAME_FIELD_WIDTH, NAME_FIELD_HEIGHT, 10, 10);

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);

        double textX = NAME_FIELD_X + 16;
        if (playerName.isEmpty()) {
            gc.setFill(Color.web("#888888"));
            gc.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
            gc.fillText(NAME_PLACEHOLDER, textX, rowY);
        } else {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 22));
            gc.fillText(playerName, textX, rowY);
        }

        if (nameFieldFocused) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 22));
            double cursorX = textX + textWidth(playerName.isEmpty() ? "" : playerName) + 2;
            gc.fillText("|", cursorX, rowY);
        }

        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setFill(Color.web("#888888"));
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        gc.fillText(playerName.length() + "/" + PlayerProfile.MAX_NAME_LENGTH,
                NAME_FIELD_X + NAME_FIELD_WIDTH - 12, rowY + 28);
    }

    private static double textWidth(String text) {
        Text measure = new Text(text);
        measure.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        return measure.getLayoutBounds().getWidth();
    }

    private void drawSelectorRow(Field field, String label, String value, String flagColor,
                                 double mouseX, double mouseY) {
        double rowY = rowY(field);

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Color.web("#aaaaaa"));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.fillText(label, 80, rowY - 34);

        drawArrow(PREV_ARROW_X, rowY, mouseX, mouseY, true);
        drawArrow(NEXT_ARROW_X, rowY, mouseX, mouseY, false);

        gc.setFill(Color.web("#3a4a5a"));
        gc.fillRoundRect(ROW_CENTER_X - VALUE_WIDTH / 2.0, rowY - ARROW_HEIGHT / 2.0,
                VALUE_WIDTH, ARROW_HEIGHT, 10, 10);

        if (flagColor != null) {
            gc.setFill(Color.web(flagColor));
            gc.fillOval(ROW_CENTER_X - VALUE_WIDTH / 2.0 + 16, rowY - 14, 28, 28);
        }

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        double textX = flagColor != null ? ROW_CENTER_X + 16 : ROW_CENTER_X;
        gc.fillText(value, textX, rowY);
    }

    private void drawArrow(double x, double rowY, double mouseX, double mouseY, boolean prev) {
        double y = rowY - ARROW_HEIGHT / 2.0;
        boolean hovered = mouseX >= x && mouseX <= x + ARROW_WIDTH
                && mouseY >= y && mouseY <= y + ARROW_HEIGHT;

        gc.setFill(hovered ? Color.web("#5a7a9a") : Color.web("#4a6a8a"));
        gc.fillRoundRect(x, y, ARROW_WIDTH, ARROW_HEIGHT, 10, 10);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(prev ? "‹" : "›", x + ARROW_WIDTH / 2.0, rowY);
    }

    public static boolean isNameFieldHit(double x, double y) {
        double top = NAME_ROW_Y - NAME_FIELD_HEIGHT / 2.0;
        return x >= NAME_FIELD_X && x <= NAME_FIELD_X + NAME_FIELD_WIDTH
                && y >= top && y <= top + NAME_FIELD_HEIGHT;
    }

    public static double rowY(Field field) {
        return switch (field) {
            case FLAG -> FLAG_ROW_Y;
            case FORMATION -> FORMATION_ROW_Y;
        };
    }

    public static boolean isPrevArrowHit(double x, double y, Field field) {
        return isArrowHit(x, y, field, PREV_ARROW_X);
    }

    public static boolean isNextArrowHit(double x, double y, Field field) {
        return isArrowHit(x, y, field, NEXT_ARROW_X);
    }

    private static boolean isArrowHit(double x, double y, Field field, double arrowX) {
        double rowY = rowY(field);
        double top = rowY - ARROW_HEIGHT / 2.0;
        return x >= arrowX && x <= arrowX + ARROW_WIDTH
                && y >= top && y <= top + ARROW_HEIGHT;
    }

    public static boolean isBackButtonHit(double x, double y) {
        return x >= BACK_BTN_X && x <= BACK_BTN_X + BACK_BTN_WIDTH
                && y >= BACK_BTN_Y && y <= BACK_BTN_Y + BACK_BTN_HEIGHT;
    }
}
