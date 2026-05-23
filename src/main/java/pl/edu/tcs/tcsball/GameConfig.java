package pl.edu.tcs.tcsball;

public class GameConfig {
    // Całkowita szerokość i wysokość okna aplikacji
    public static final double WINDOW_WIDTH = 800.0;
    public static final double WINDOW_HEIGHT = 600.0;

    // Wysokość górnego paska na wynik (boisko zaczyna się poniżej)
    public static final double SCORE_PANEL_HEIGHT = 80.0;

    // Odległość linii boiska od krawędzi okna (zostawia miejsce na bramki)
    public static final double MARGIN_X = 40.0;
    public static final double MARGIN_Y = 20.0;

    // Głębokość bramki (ile wystaje za aut) i jej szerokość (światło bramki)
    public static final double GOAL_WIDTH = 30.0;
    public static final double GOAL_HEIGHT = 160.0;

    // --- GRANICE FIZYCZNE (BANDY) ---
    // Współrzędne ścian boiska dla silnika fizyki (do odbijania pionków)
    public static final double PITCH_TOP_Y = SCORE_PANEL_HEIGHT + MARGIN_Y;
    public static final double PITCH_BOTTOM_Y = WINDOW_HEIGHT - MARGIN_Y;
    public static final double PITCH_LEFT_X = MARGIN_X;
    public static final double PITCH_RIGHT_X = WINDOW_WIDTH - MARGIN_X;

    // --- KOORDYNATY BRAMKI ---
    // Współrzędne ułatwiające fizyce sprawdzenie, czy padł gol (pomiędzy słupkami)
    public static final double GOAL_CENTER_Y = SCORE_PANEL_HEIGHT + ((WINDOW_HEIGHT - SCORE_PANEL_HEIGHT) / 2.0);
    public static final double GOAL_TOP_Y = GOAL_CENTER_Y - (GOAL_HEIGHT / 2.0);    // Górny słupek
    public static final double GOAL_BOTTOM_Y = GOAL_CENTER_Y + (GOAL_HEIGHT / 2.0); // Dolny słupek
}