package pl.edu.tcs.tcsball;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public final class GameConfig {
    // Wartosci sa ladowane z pliku src/main/resources/game-config.json przez Gson.
    private static final ConfigData DATA = readConfig();

    // Calkowita szerokosc i wysokosc okna aplikacji
    public static final int WINDOW_WIDTH = DATA.windowWidth();
    public static final int WINDOW_HEIGHT = DATA.windowHeight();

    // --- STEROWANIE ---
    public static final double MAX_PULL_DISTANCE = DATA.maxPullDistance();

    // Maksymalna dlugosc nazwy gracza wpisywanej na ekranie customizacji
    public static final int MAX_PLAYER_NAME_LENGTH = DATA.maxPlayerNameLength();

    // Wysokosc gornego paska na wynik (boisko zaczyna sie ponizej)
    public static final double SCORE_PANEL_HEIGHT = DATA.scorePanelHeight();

    // Odleglosc linii boiska od krawedzi okna (zostawia miejsce na bramki)
    public static final double MARGIN_X = DATA.marginX();
    public static final double MARGIN_Y = DATA.marginY();

    // Glebokosc bramki (ile wystaje za aut) i jej wysokosc (swiatlo bramki)
    public static final double GOAL_WIDTH = DATA.goalWidth();
    public static final double GOAL_HEIGHT = DATA.goalHeight();

    // --- GRANICE FIZYCZNE (BANDY) ---
    // Wspolrzedne scian boiska dla silnika fizyki (do odbijania pionkow)
    public static final double PITCH_TOP_Y = SCORE_PANEL_HEIGHT + MARGIN_Y;
    public static final double PITCH_BOTTOM_Y = WINDOW_HEIGHT - MARGIN_Y;
    public static final double PITCH_LEFT_X = MARGIN_X;
    public static final double PITCH_RIGHT_X = WINDOW_WIDTH - MARGIN_X;
    public static final double PITCH_CENTER_CIRCLE_RADIUS = DATA.pitchCenterCircleRadius();

    // --- KOORDYNATY BRAMKI ---
    // Wspolrzedne ulatwiajace fizyce sprawdzenie, czy padl gol (pomiedzy slupkami)
    public static final double GOAL_CENTER_Y = SCORE_PANEL_HEIGHT + ((WINDOW_HEIGHT - SCORE_PANEL_HEIGHT) / 2.0);
    public static final double GOAL_TOP_Y = GOAL_CENTER_Y - (GOAL_HEIGHT / 2.0);    // Gorny slupek
    public static final double GOAL_BOTTOM_Y = GOAL_CENTER_Y + (GOAL_HEIGHT / 2.0); // Dolny slupek

    // --- PIONKI ---
    // Promien kazdego pionka (do rysowania i kolizji)
    public static final double PAWN_RADIUS = DATA.pawnRadius();
    public static final double PAWN_MASS = DATA.pawnMass();
    public static final double PAWN_RESTITUTION = DATA.pawnRestitution();
    public static final double PAWN_SHOT_POWER = DATA.pawnShotPower();

    // --- PILKA ---
    public static final double BALL_RADIUS = DATA.ballRadius();
    public static final double BALL_MASS = DATA.ballMass();
    public static final double BALL_RESTITUTION = DATA.ballRestitution();
    public static final double BALL_MAX_SPIN = DATA.ballMaxSpin();
    public static final double BALL_FULL_ROTATION_DEGREES = DATA.ballFullRotationDegrees();
    public static final double BALL_SPIN_ROTATION_SPEED = DATA.ballSpinRotationSpeed();

    // --- FIZYKA ---
    public static final double REFERENCE_FPS = DATA.referenceFps();
    public static final double FRICTION = DATA.friction();             // Tarcie na klatke przy 120 FPS
    public static final double PAWN_STOP_SPEED = DATA.pawnStopSpeed(); // px/s
    public static final double BALL_STOP_SPEED = DATA.ballStopSpeed(); // px/s
    public static final double SPIN_TURN_POWER = DATA.spinTurnPower();
    public static final double SPIN_FRICTION = DATA.spinFriction();
    public static final double MIN_SPEED_FOR_SPIN = DATA.minSpeedForSpin();
    public static final double SPIN_FROM_HIT = DATA.spinFromHit();
    public static final double FULL_SPIN_HIT_SPEED = DATA.fullSpinHitSpeed();
    public static final int COLLISION_PASSES = DATA.collisionPasses();
    public static final double COLLISION_MIN_DISTANCE = DATA.collisionMinDistance();

    // --- SIEC LAN ---
    public static final int NETWORK_GAME_PORT = DATA.networkGamePort();
    public static final int NETWORK_DISCOVERY_PORT = DATA.networkDiscoveryPort();
    public static final int NETWORK_BROADCAST_INTERVAL_MILLIS = DATA.networkBroadcastIntervalMillis();
    public static final int NETWORK_HOST_TIMEOUT_MILLIS = DATA.networkHostTimeoutMillis();
    public static final int NETWORK_STATE_SYNC_INTERVAL_MILLIS = DATA.networkStateSyncIntervalMillis();
    // Czas oczekiwania klienta na potwierdzenie dolaczenia do lobby zanim zrezygnuje
    public static final int JOIN_PENDING_TIMEOUT_MILLIS = DATA.joinPendingTimeoutMillis();

    // Pozycje pionkow druzyny 1 (lewa strona) jako offsety:
    //   [0] = X liczone od bandy PITCH_LEFT_X w glab boiska
    //   [1] = Y liczone od GOAL_CENTER_Y (ujemne = wyzej, dodatnie = nizej)
    // Druzyna 2 powstaje jako lustrzane odbicie tych pozycji po prawej stronie.
    public static final double[][] TEAM_FORMATION_OFFSETS = formation();

    private GameConfig() {
    }

    private static ConfigData readConfig() {
        try (InputStream input = GameConfig.class.getResourceAsStream("/game-config.json")) {
            if (input == null) {
                throw new IllegalStateException("Missing game-config.json");
            }
            Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
            ConfigData data = new Gson().fromJson(reader, ConfigData.class);
            if (data == null) {
                throw new IllegalStateException("Empty game-config.json");
            }
            data.validate();
            return data;
        } catch (IOException | JsonParseException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static double[][] formation() {
        double[] x = DATA.teamFormationOffsetX();
        double[] y = DATA.teamFormationOffsetY();

        double[][] offsets = new double[x.length][2];
        for (int i = 0; i < x.length; i++) {
            offsets[i][0] = x[i];
            offsets[i][1] = y[i];
        }
        return offsets;
    }

    // Nazwy pol musza odpowiadac kluczom w game-config.json (Gson mapuje je 1:1).
    private record ConfigData(
            int windowWidth,
            int windowHeight,
            double maxPullDistance,
            int maxPlayerNameLength,
            double scorePanelHeight,
            double marginX,
            double marginY,
            double goalWidth,
            double goalHeight,
            double pitchCenterCircleRadius,
            double pawnRadius,
            double pawnMass,
            double pawnRestitution,
            double pawnShotPower,
            double ballRadius,
            double ballMass,
            double ballRestitution,
            double ballMaxSpin,
            double ballFullRotationDegrees,
            double ballSpinRotationSpeed,
            double referenceFps,
            double friction,
            double pawnStopSpeed,
            double ballStopSpeed,
            double spinTurnPower,
            double spinFriction,
            double minSpeedForSpin,
            double spinFromHit,
            double fullSpinHitSpeed,
            int collisionPasses,
            double collisionMinDistance,
            int networkGamePort,
            int networkDiscoveryPort,
            int networkBroadcastIntervalMillis,
            int networkHostTimeoutMillis,
            int networkStateSyncIntervalMillis,
            int joinPendingTimeoutMillis,
            double[] teamFormationOffsetX,
            double[] teamFormationOffsetY
    ) {
        private void validate() {
            if (teamFormationOffsetX == null || teamFormationOffsetY == null) {
                throw new IllegalStateException("Missing formation offsets in game-config.json");
            }
            if (teamFormationOffsetX.length != teamFormationOffsetY.length) {
                throw new IllegalStateException("Formation arrays must have the same length");
            }
        }
    }
}
