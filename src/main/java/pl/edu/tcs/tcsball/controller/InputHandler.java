package pl.edu.tcs.tcsball.controller;

import javafx.scene.Scene;

public class InputHandler {
    private GameManager gameManager;

    public InputHandler(GameManager gameManager, Scene scene) {
        this.gameManager = gameManager;

        scene.setOnMousePressed(event -> {
           gameManager.startAiming(event.getSceneX(), event.getSceneY());
        });

        scene.setOnMouseDragged(event -> {
            gameManager.updateMousePosition(event.getSceneX(), event.getSceneY());
        });

        scene.setOnMouseReleased(event -> {
            gameManager.shootPawn();
        });
    }
}