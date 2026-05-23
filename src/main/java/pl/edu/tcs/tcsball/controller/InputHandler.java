package pl.edu.tcs.tcsball.controller;

import javafx.scene.Scene;
import pl.edu.tcs.tcsball.model.GameState;

public class InputHandler {
    private GameManager gameManager;

    public InputHandler(GameManager gameManager, Scene scene) {
        this.gameManager = gameManager;

        scene.setOnMousePressed(event -> {
            GameState state = gameManager.getGameState();
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();

            if (state == GameState.MENU) {
                gameManager.handleMenuClick(mouseX, mouseY);
            } else if (state == GameState.SETTINGS) {
                gameManager.handleSettingsClick(mouseX, mouseY);
            } else if (state == GameState.PLAYING) {
                gameManager.startAiming(mouseX, mouseY);
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