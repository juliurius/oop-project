package tcsball.controller;

import javafx.scene.Scene;
import tcsball.model.Pawn;

public class InputHandler {
    private GameManager gameManager;
    private Pawn selectedPawn = null;

    public InputHandler(GameManager gameManager, Scene scene) {
        this.gameManager = gameManager;

        // KROK 1: Kliknięcie myszką
        scene.setOnMousePressed(event -> {
            // TODO: Zapisz punkt kliknięcia
            // TODO: Znajdź, czy kliknięto wewnątrz któregoś pionka i przypisz go do selectedPawn
        });

        // KROK 2: Przeciąganie myszki
        scene.setOnMouseDragged(event -> {
            // TODO: Oblicz wektor naciągu (do rysowania strzałki)
        });

        // KROK 3: Puszczenie myszki (Strzał)
        scene.setOnMouseReleased(event -> {
            if (selectedPawn != null) {
                // TODO: Oblicz siłę strzału (różnica między puszczeniem a kliknięciem)
                // TODO: Przekaż do gameManager.shootPawn(...)
                // TODO: selectedPawn = null (reset)
            }
        });
    }
}