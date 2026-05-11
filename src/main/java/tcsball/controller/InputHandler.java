package tcsball.controller;

import javafx.scene.Scene;
import tcsball.model.Pawn;
import tcsball.model.Vector2D;
import java.util.List;

import java.lang.Math.*;

public class InputHandler {
    private GameManager gameManager;
    private Pawn selectedPawn = null;
    private Vector2D tensionVector;
    double mouseX = 0, mouseY = 0;

    public InputHandler(GameManager gameManager, Scene scene) {
        this.gameManager = gameManager;
        tensionVector = new Vector2D(0, 0);

        scene.setOnMousePressed(event -> {
           gameManager.startAiming(event.getSceneX(), event.getSceneY());
        });

        scene.setOnMouseDragged(event -> {
            gameManager.updateMousePosition(event.getSceneX(), event.getSceneY());
        });

        scene.setOnMouseReleased(event -> {
            if (selectedPawn != null) {
                gameManager.shootPawn(selectedPawn, tensionVector);
            }
        });
    }
}