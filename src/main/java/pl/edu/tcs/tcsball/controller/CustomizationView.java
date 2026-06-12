package pl.edu.tcs.tcsball.controller;

/**
 * Widok tylko-do-odczytu danych profilu, ktorego potrzebuje ekran customizacji.
 * Wzorowany na {@link LobbyView}; ekran rzutuje {@link GameView} na ten interfejs.
 */
public interface CustomizationView extends GameView {
    String getPlayerName();

    String getCurrentFlagCode();

    String getCurrentFlagName();

    String getCurrentFormationName();
}
