package pl.edu.tcs.tcsball;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public final class GameConfig {
    // Wartosci sa ladowane z pliku src/main/resources/game-config.json.
    private static final String CONFIG = readConfig();

    // Calkowita szerokosc i wysokosc okna aplikacji
    public static final int WINDOW_WIDTH = intValue("windowWidth");
    public static final int WINDOW_HEIGHT = intValue("windowHeight");

    // --- STEROWANIE ---
    public static final double MAX_PULL_DISTANCE = doubleValue("maxPullDistance");

    // Wysokosc gornego paska na wynik (boisko zaczyna sie ponizej)
    public static final double SCORE_PANEL_HEIGHT = doubleValue("scorePanelHeight");

    // Odleglosc linii boiska od krawedzi okna (zostawia miejsce na bramki)
    public static final double MARGIN_X = doubleValue("marginX");
    public static final double MARGIN_Y = doubleValue("marginY");

    // Glebokosc bramki (ile wystaje za aut) i jej wysokosc (swiatlo bramki)
    public static final double GOAL_WIDTH = doubleValue("goalWidth");
    public static final double GOAL_HEIGHT = doubleValue("goalHeight");

    // --- GRANICE FIZYCZNE (BANDY) ---
    // Wspolrzedne scian boiska dla silnika fizyki (do odbijania pionkow)
    public static final double PITCH_TOP_Y = SCORE_PANEL_HEIGHT + MARGIN_Y;
    public static final double PITCH_BOTTOM_Y = WINDOW_HEIGHT - MARGIN_Y;
    public static final double PITCH_LEFT_X = MARGIN_X;
    public static final double PITCH_RIGHT_X = WINDOW_WIDTH - MARGIN_X;
    public static final double PITCH_CENTER_CIRCLE_RADIUS = doubleValue("pitchCenterCircleRadius");

    // --- KOORDYNATY BRAMKI ---
    // Wspolrzedne ulatwiajace fizyce sprawdzenie, czy padl gol (pomiedzy slupkami)
    public static final double GOAL_CENTER_Y = SCORE_PANEL_HEIGHT + ((WINDOW_HEIGHT - SCORE_PANEL_HEIGHT) / 2.0);
    public static final double GOAL_TOP_Y = GOAL_CENTER_Y - (GOAL_HEIGHT / 2.0);    // Gorny slupek
    public static final double GOAL_BOTTOM_Y = GOAL_CENTER_Y + (GOAL_HEIGHT / 2.0); // Dolny slupek

    // --- PIONKI ---
    // Promien kazdego pionka (do rysowania i kolizji)
    public static final double PAWN_RADIUS = doubleValue("pawnRadius");
    public static final double PAWN_MASS = doubleValue("pawnMass");
    public static final double PAWN_RESTITUTION = doubleValue("pawnRestitution");
    public static final double PAWN_SHOT_POWER = doubleValue("pawnShotPower");

    // --- PILKA ---
    public static final double BALL_RADIUS = doubleValue("ballRadius");
    public static final double BALL_MASS = doubleValue("ballMass");
    public static final double BALL_RESTITUTION = doubleValue("ballRestitution");
    public static final double BALL_MAX_SPIN = doubleValue("ballMaxSpin");
    public static final double BALL_FULL_ROTATION_DEGREES = doubleValue("ballFullRotationDegrees");
    public static final double BALL_SPIN_ROTATION_SPEED = doubleValue("ballSpinRotationSpeed");

    // --- FIZYKA ---
    public static final double REFERENCE_FPS = doubleValue("referenceFps");
    public static final double FRICTION = doubleValue("friction");             // Tarcie na klatke przy 120 FPS
    public static final double PAWN_STOP_SPEED = doubleValue("pawnStopSpeed"); // px/s
    public static final double BALL_STOP_SPEED = doubleValue("ballStopSpeed"); // px/s
    public static final double SPIN_TURN_POWER = doubleValue("spinTurnPower");
    public static final double SPIN_FRICTION = doubleValue("spinFriction");
    public static final double MIN_SPEED_FOR_SPIN = doubleValue("minSpeedForSpin");
    public static final double SPIN_FROM_HIT = doubleValue("spinFromHit");
    public static final double FULL_SPIN_HIT_SPEED = doubleValue("fullSpinHitSpeed");
    public static final int COLLISION_PASSES = intValue("collisionPasses");
    public static final double COLLISION_MIN_DISTANCE = doubleValue("collisionMinDistance");

    // --- SIEC LAN ---
    public static final int NETWORK_GAME_PORT = intValue("networkGamePort");
    public static final int NETWORK_DISCOVERY_PORT = intValue("networkDiscoveryPort");
    public static final int NETWORK_BROADCAST_INTERVAL_MILLIS = intValue("networkBroadcastIntervalMillis");
    public static final int NETWORK_HOST_TIMEOUT_MILLIS = intValue("networkHostTimeoutMillis");
    public static final int NETWORK_STATE_SYNC_INTERVAL_MILLIS = intValue("networkStateSyncIntervalMillis");

    // Pozycje pionkow druzyny 1 (lewa strona) jako offsety:
    //   [0] = X liczone od bandy PITCH_LEFT_X w glab boiska
    //   [1] = Y liczone od GOAL_CENTER_Y (ujemne = wyzej, dodatnie = nizej)
    // Druzyna 2 powstaje jako lustrzane odbicie tych pozycji po prawej stronie.
    public static final double[][] TEAM_FORMATION_OFFSETS = formation();

    private GameConfig() {
    }

    private static String readConfig() {
        try (var input = GameConfig.class.getResourceAsStream("/game-config.json")) {
            if (input == null) {
                throw new IllegalStateException("Missing game-config.json");
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static int intValue(String key) {
        return (int) doubleValue(key);
    }

    private static double doubleValue(String key) {
        return Double.parseDouble(value(key));
    }

    private static String value(String key) {
        var matcher = Pattern.compile("\"" + key + "\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?|\\[[^]]*])").matcher(CONFIG);
        if (!matcher.find()) {
            throw new IllegalStateException("Missing config value: " + key);
        }
        return matcher.group(1);
    }

    private static double[] array(String key) {
        return Pattern.compile("-?\\d+(?:\\.\\d+)?")
                .matcher(value(key))
                .results()
                .mapToDouble(match -> Double.parseDouble(match.group()))
                .toArray();
    }

    private static double[][] formation() {
        double[] x = array("teamFormationOffsetX");
        double[] y = array("teamFormationOffsetY");
        if (x.length != y.length) {
            throw new IllegalStateException("Formation arrays must have the same length");
        }

        double[][] offsets = new double[x.length][2];
        for (int i = 0; i < x.length; i++) {
            offsets[i][0] = x[i];
            offsets[i][1] = y[i];
        }
        return offsets;
    }
}
