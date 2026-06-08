package pl.edu.tcs.tcsball.view.element;

import javafx.scene.paint.Color;

import java.util.Map;

/** Mapowanie kodu flagi na kolor — wspólne dla customizacji, lobby i pionków. */
public final class FlagColors {

    private static final Map<String, String> COLORS = Map.of(
            "PL", "#dc143c",
            "UA", "#005bbb",
            "DE", "#000000",
            "FR", "#0055a4",
            "ES", "#c60b1e"
    );

    /** Local play: drużyna 2 dostaje kontrastowy kolor zamiast tego samego co drużyna 1. */
    private static final Map<String, String> LOCAL_PLAY_TEAM2 = Map.of(
            "PL", "#1e90ff",   // czerwony → niebieski
            "UA", "#c9a000",   // niebieski → ciemny złoty
            "DE", "#d4a017",   // czarny → złoty
            "FR", "#c60b1e",   // niebieski → czerwony
            "ES", "#0055a4"    // czerwony → niebieski
    );

    private static final String DEFAULT = "#888888";
    private static final String DEFAULT_LOCAL_TEAM2 = "#1e90ff";

    private FlagColors() {}

    public static String forCode(String code) {
        return COLORS.getOrDefault(code, DEFAULT);
    }

    public static String innerForCode(String code) {
        return innerForHex(forCode(code));
    }

    /** Local play — ten sam kraj na obu stronach, drużyna 2 w kontrastowym kolorze. */
    public static String localPlayTeam2Color(String code) {
        return LOCAL_PLAY_TEAM2.getOrDefault(code, DEFAULT_LOCAL_TEAM2);
    }

    public static String innerForHex(String hex) {
        Color color = Color.web(hex);
        if (color.getBrightness() < 0.12) {
            return "#4a4a4a";
        }
        if (color.getBrightness() > 0.7) {
            return shade(hex, 0.65);
        }
        return shade(hex, 0.72);
    }

    private static String shade(String hex, double factor) {
        Color color = Color.web(hex);
        double r = clamp(color.getRed() * factor);
        double g = clamp(color.getGreen() * factor);
        double b = clamp(color.getBlue() * factor);
        return String.format("#%02x%02x%02x",
                (int) Math.round(r * 255),
                (int) Math.round(g * 255),
                (int) Math.round(b * 255));
    }

    private static double clamp(double value) {
        return Math.max(0, Math.min(1, value));
    }
}
