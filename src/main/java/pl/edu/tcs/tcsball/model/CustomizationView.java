package pl.edu.tcs.tcsball.model;

/**
 * Widok tylko-do-odczytu danych profilu, którego potrzebuje ekran customizacji.
 * Wzorowany na {@link LobbyView} — ekran rzutuje {@link GameView} na ten interfejs.
 */
public interface CustomizationView extends GameView {
    String getPlayerName();

    String getCurrentFlagCode();

    String getCurrentFlagName();

    String getCurrentFormationName();
}
