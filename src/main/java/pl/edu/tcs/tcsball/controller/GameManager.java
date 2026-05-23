package pl.edu.tcs.tcsball.controller;

import pl.edu.tcs.tcsball.model.*;
import pl.edu.tcs.tcsball.view.Renderer;

import java.util.List;

public class GameManager implements GameView {
    private final Match match;
    private final PhysicsEngine physics;
    private GameState gameState = GameState.PLAYING;

    private Pawn selectedPawn = null;
    private final Vector2D tensionVector = new Vector2D(0, 0);
    double mouseX = 0, mouseY = 0;

    public GameManager(double width, double height) {
        match = new Match();
        physics = new PhysicsEngine(width, height);
    }

    public void update(double deltaTime) {
        physics.update(match.getPawns(), match.getBall(), deltaTime);

        if (physics.wasGoalScored()) {
            if (gameState != GameState.GOAL_SCORED)
                match.updateScore(physics.getLastGoalScoredByTeam());

            gameState = GameState.GOAL_SCORED;
        }
    }

    public Ball getBall() { return match.getBall(); }

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

    public Pawn getAimingPawn() { return selectedPawn; }

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

    public List<Pawn> getPawns() { return match.getPawns(); }

    public int getTeamScore(int team) { return match.getTeamScore(team); }

    public GameState getGameState() { return gameState; }
}
