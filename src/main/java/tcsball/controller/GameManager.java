package tcsball.controller;

import tcsball.model.Pawn;
import tcsball.model.PhysicsEngine;
import tcsball.model.Vector2D;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private List<Pawn> pawns;
    private PhysicsEngine physics;
    private Pawn selectedPawn = null;
    private Vector2D tensionVector;
    double mouseX = 0, mouseY = 0;

    public GameManager(double width, double height) {
        this.pawns = new ArrayList<>();
        this.physics = new PhysicsEngine(width, height);

        // MVP: Jeden pionek testowy na środku
        pawns.add(new Pawn(width / 2, height / 2, 20));
    }

    public void update(double deltaTime) {
        physics.update(pawns, deltaTime);
    }

    public List<Pawn> getPawns() {
        return pawns;
    }

    public void shootPawn(Pawn selectedPawn, Vector2D force) {
        selectedPawn.applyForce(force);
        selectedPawn = null;
    }

    public void startAiming(double x, double y) {
        for (Pawn pawn : pawns) {
            Vector2D position = pawn.getPosition();
            double pawnX = position.getX(), pawnY = position.getY(), pawnR = pawn.getRadius();
            double distance = Math.sqrt(Math.pow((pawnX - x), 2) + Math.pow((pawnY - y), 2));

            if (pawnR >= distance) {
                selectedPawn = pawn;
                break;
            }
        }
    };

    public void updateMousePosition(double x, double y) {
        mouseX = x;
        mouseY = y;
        double newX = selectedPawn.getPosition().getX() - mouseX;
        double newY = selectedPawn.getPosition().getY() - mouseY;

        tensionVector.setX(newX);
        tensionVector.setY(newY);
    }

    public Pawn getAimingPawn() {
        return selectedPawn;
    }

    public double getArrowX() {
        if (selectedPawn != null)
            return selectedPawn.getPosition().getX() + tensionVector.getX();
        return 0;
    }

    public double getArrowY() {
        if (selectedPawn != null)
            return selectedPawn.getPosition().getY() + tensionVector.getY();
        return 0;
    }
}