package pl.edu.tcs.tcsball.controller;

import javafx.scene.Scene;
import pl.edu.tcs.tcsball.model.GameState;

public class InputHandler {
    private GameManager gameManager;

    public InputHandler(GameManager gameManager, Scene scene) {
        this.gameManager = gameManager;

        scene.setOnMousePressed(event -> {
            if (gameManager.getGameState() == GameState.MENU) {
                gameManager.handleMenuClick(event.getSceneX(), event.getSceneY());
            } else if (gameManager.getGameState() == GameState.PLAYING) {
                gameManager.startAiming(event.getSceneX(), event.getSceneY());
            }
        });

        scene.setOnMouseDragged(event -> {
            if (gameManager.getGameState() == GameState.PLAYING) {
                gameManager.updateMousePosition(event.getSceneX(), event.getSceneY());
            }
        });

        scene.setOnMouseReleased(event -> {
            if (gameManager.getGameState() == GameState.PLAYING) {
                gameManager.shootPawn();
            }
        });
    }
}