package pl.edu.tcs.tcsball.view.element;

import java.util.Map;

/** Mapowanie kodu flagi na kolor uzywany przez renderery. */
public final class FlagColors {

    private static final Map<String, String> COLORS = Map.of(
            "PL", "#dc143c",
            "UA", "#005bbb",
            "DE", "#000000",
            "FR", "#0055a4",
            "ES", "#c60b1e"
    );

    /** Local play: druzyna 2 dostaje kontrastowy kolor zamiast tego samego co druzyna 1. */
    private static final Map<String, String> LOCAL_PLAY_TEAM2 = Map.of(
            "PL", "#1e90ff",
            "UA", "#c9a000",
            "DE", "#d4a017",
            "FR", "#c60b1e",
            "ES", "#0055a4"
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

    public static String localPlayTeam2Color(String code) {
        return LOCAL_PLAY_TEAM2.getOrDefault(code, DEFAULT_LOCAL_TEAM2);
    }

    public static String innerForHex(String hex) {
        int[] rgb = parseRgb(hex);
        double brightness = Math.max(rgb[0], Math.max(rgb[1], rgb[2])) / 255.0;

        if (brightness < 0.12) {
            return "#4a4a4a";
        }
        if (brightness > 0.7) {
            return shade(rgb, 0.65);
        }
        return shade(rgb, 0.72);
    }

    private static String shade(int[] rgb, double factor) {
        int r = clamp((int) Math.round(rgb[0] * factor));
        int g = clamp((int) Math.round(rgb[1] * factor));
        int b = clamp((int) Math.round(rgb[2] * factor));
        return String.format("#%02x%02x%02x", r, g, b);
    }

    private static int[] parseRgb(String hex) {
        String value = hex.startsWith("#") ? hex.substring(1) : hex;
        return new int[] {
                Integer.parseInt(value.substring(0, 2), 16),
                Integer.parseInt(value.substring(2, 4), 16),
                Integer.parseInt(value.substring(4, 6), 16)
        };
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
