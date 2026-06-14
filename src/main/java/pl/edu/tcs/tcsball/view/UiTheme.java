package pl.edu.tcs.tcsball.view;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Centralna paleta kolorow i typografia UI. Wartosci wspoldzielone przez wiele
 * ekranow/rendererow (kolory przyciskow, odcienie tekstu, panele) oraz rodzina
 * fontow zyja tutaj, zeby nie powielac tych samych literalow w kodzie widoku.
 * Kolory jednorazowe (tla konkretnych ekranow) i kolory flag pozostaja lokalne.
 */
public final class UiTheme {
    private UiTheme() {
    }

    // --- Przyciski: kolor bazowy + hover ---
    public static final Color ACCENT = Color.web("#4682b4");
    public static final Color ACCENT_HOVER = Color.web("#357abd");
    public static final Color SUCCESS = Color.web("#32cd32");
    public static final Color SUCCESS_HOVER = Color.web("#28a428");
    public static final Color DANGER = Color.web("#d9534f");
    public static final Color DANGER_HOVER = Color.web("#c9302c");
    public static final Color BUTTON_DISABLED = Color.web("#555555");

    // --- Odcienie tekstu ---
    public static final Color TEXT_MUTED = Color.web("#cccccc");
    public static final Color TEXT_DIM = Color.web("#aaaaaa");
    public static final Color TEXT_DISABLED = Color.web("#888888");

    // --- Panele / selektory ---
    public static final Color PANEL = Color.web("#3a4a5a");
    public static final Color SELECTOR = Color.web("#5a7a9a");
    public static final Color SELECTOR_HOVER = Color.web("#4a6a8a");

    // --- Typografia ---
    public static final String FONT_FAMILY = "Arial";

    /** Font z domyslnej rodziny UI o zadanej grubosci i rozmiarze. */
    public static Font font(FontWeight weight, double size) {
        return Font.font(FONT_FAMILY, weight, size);
    }
}
