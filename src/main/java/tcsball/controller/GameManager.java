package tcsball.controller;

import tcsball.model.*;
import java.util.List;

public class GameManager {
    private final Match match;
    private final PhysicsEngine physics;

    private Pawn selectedPawn = null;
    private Vector2D tensionVector;
    double mouseX = 0, mouseY = 0;

    public GameManager(double width, double height) {
        match = new Match();
        physics = new PhysicsEngine(width, height);

        tensionVector = new Vector2D(0, 0);
    }

    public void update(double deltaTime) {
        List<Pawn> pawns = match.getPawns();
        Ball ball = match.getBall();

        physics.update(pawns, ball, deltaTime);
    }

    public void shootPawn() {
        selectedPawn.applyForce(tensionVector);
        selectedPawn = null;
        tensionVector.setX(0);
        tensionVector.setY(0);
    }

    public void startAiming(double x, double y) {
        List<Pawn> pawns = match.getPawns();

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

    public List<Pawn> getPawns() {
        return match.getPawns();
    }

    public double getBallX() {
        return match.getBall().getPosition().getX();
    }

    public double getBallY() {
        return match.getBall().getPosition().getY();
    }
}
