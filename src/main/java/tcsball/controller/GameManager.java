package tcsball.controller;

import tcsball.model.*;
import tcsball.view.Renderer;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GameManager {
    private final Match match;
    private final PhysicsEngine physics;
    private final Renderer renderer;

    private Pawn selectedPawn = null;
    private final Vector2D tensionVector = new Vector2D(0, 0);
    double mouseX = 0, mouseY = 0;

    public GameManager(double width, double height, Renderer renderer) {
        match = new Match();
        physics = new PhysicsEngine(width, height);
        this.renderer = renderer;
    }

    public void update(double deltaTime) {
        List<Pawn> pawns = match.getPawns();
        Ball ball = match.getBall();

        physics.update(pawns, ball, deltaTime);

        if (physics.wasGoalScored()) {
            int teamScored = physics.getLastGoalScoredByTeam();
            match.updateScore(teamScored);
            renderer.setGoalMessageVisible(true);
            match.resetGame();
            renderer.setGoalMessageVisible(false);
        }
    }

    public void shootPawn() {
        if (selectedPawn == null) return;

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
        if (selectedPawn == null) return;

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

    public int getTeamScore(int team) {
        return match.getTeamScore(team);
    }
}
