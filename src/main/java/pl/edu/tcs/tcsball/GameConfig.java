package pl.edu.tcs.tcsball;

public class GameConfig {
    // Całkowita szerokość i wysokość okna aplikacji
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;

    // --- STEROWANIE ---
    public static final double MAX_PULL_DISTANCE = 150.0;

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

    // --- PIONKI ---
    // Promień każdego pionka (do rysowania i kolizji)
    public static final double PAWN_RADIUS = 25.0;

    // Pozycje pionków drużyny 1 (lewa strona) jako offsety:
    //   [0] = X liczone od bandy PITCH_LEFT_X w głąb boiska
    //   [1] = Y liczone od GOAL_CENTER_Y (ujemne = wyżej, dodatnie = niżej)
    // Drużyna 2 powstaje jako lustrzane odbicie tych pozycji po prawej stronie.
    // Liczba wierszy = liczba pionków na drużynę. Tu: 5 (bramkarz + 2 obrońców + 2 napastników).
    public static final double[][] TEAM_FORMATION_OFFSETS = {
            {  35,    0 },   // bramkarz
            { 125, -100 },   // obrońca góra
            { 125,  100 },   // obrońca dół
            { 245,  -80 },   // napastnik góra
            { 245,   80 },   // napastnik dół
    };
}