package pl.edu.tcs.tcsball.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import pl.edu.tcs.tcsball.model.GameState;

public class InputHandler {
    public InputHandler(GameManager gameManager, Scene scene) {
        scene.setOnMouseClicked(mouseEvent -> {
            if (gameManager.getGameState() == GameState.GOAL_SCORED) {
                gameManager.dismissGoal();
            }
        });

        scene.setOnMousePressed(event -> {
            GameState state = gameManager.getGameState();
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();

            gameManager.updateActualMousePosition(mouseX, mouseY);

            switch (state) {
                case MENU -> gameManager.handleMenuClick(mouseX, mouseY);
                case CUSTOMIZATION -> gameManager.handleCustomizationClick(mouseX, mouseY);
                case HOST_LOBBY -> gameManager.handleHostLobbyClick(mouseX, mouseY);
                case JOIN_LOBBY -> gameManager.handleJoinLobbyClick(mouseX, mouseY);
                case CLIENT_LOBBY -> gameManager.handleClientLobbyClick(mouseX, mouseY);
                case PLAYING -> {
                    boolean backToMenu = gameManager.handleBackToMenuClick(mouseX, mouseY);
                    if (!backToMenu) {
                        gameManager.startAiming(mouseX, mouseY);
                    }
                }
                default -> {}
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

        scene.setOnMouseMoved(event -> {
            gameManager.updateActualMousePosition(event.getSceneX(), event.getSceneY());
        });

        scene.addEventHandler(KeyEvent.KEY_TYPED, event -> {
            if (gameManager.getGameState() == GameState.CUSTOMIZATION) {
                gameManager.handleCustomizationKey(event);
            }
        });

        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (gameManager.getGameState() == GameState.CUSTOMIZATION) {
                gameManager.handleCustomizationKey(event);
            }
        });
    }
}
