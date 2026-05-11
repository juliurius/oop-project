package tcsball.controller;

import tcsball.GameConfig;
import tcsball.model.Ball;
import tcsball.model.Pawn;
import tcsball.model.PhysicsEngine;
import tcsball.model.Vector2D;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private List<Pawn> pawns;
    private Ball ball;
    private PhysicsEngine physics;
    private Pawn selectedPawn = null;
    private Vector2D tensionVector;
    double mouseX = 0, mouseY = 0;

    public GameManager(double width, double height) {
        this.pawns = new ArrayList<>();
        this.physics = new PhysicsEngine(width, height);
        tensionVector = new Vector2D(0, 0);

        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 35, GameConfig.GOAL_CENTER_Y, 20, 1));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 125, GameConfig.PITCH_TOP_Y + 70, 20, 1));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 125, GameConfig.PITCH_TOP_Y + 185, 20,1 ));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 125, GameConfig.PITCH_BOTTOM_Y - 185, 20, 1));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 125, GameConfig.PITCH_BOTTOM_Y - 70, 20, 1));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 245, GameConfig.PITCH_TOP_Y + 70, 20, 1));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 245, GameConfig.PITCH_TOP_Y + 185, 20, 1));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 245, GameConfig.PITCH_BOTTOM_Y - 185, 20, 1));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 245, GameConfig.PITCH_BOTTOM_Y - 70, 20, 1));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 325, GameConfig.PITCH_TOP_Y + 185, 20, 1));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 325, GameConfig.PITCH_BOTTOM_Y - 185, 20, 1));

        ball = new Ball((GameConfig.PITCH_RIGHT_X + GameConfig.PITCH_LEFT_X)/2, (GameConfig.PITCH_TOP_Y + GameConfig.PITCH_BOTTOM_Y)/2, 12);

        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 35, GameConfig.GOAL_CENTER_Y, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 125, GameConfig.PITCH_TOP_Y + 70, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 125, GameConfig.PITCH_TOP_Y + 185, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 125, GameConfig.PITCH_BOTTOM_Y - 185, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 125, GameConfig.PITCH_BOTTOM_Y - 70, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 245, GameConfig.PITCH_TOP_Y + 70, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 245, GameConfig.PITCH_TOP_Y + 185, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 245, GameConfig.PITCH_BOTTOM_Y - 185, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 245, GameConfig.PITCH_BOTTOM_Y - 70, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 325, GameConfig.PITCH_TOP_Y + 185, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 325, GameConfig.PITCH_BOTTOM_Y - 185, 20, 2));
    }

    public void update(double deltaTime) {
        physics.update(pawns, ball, deltaTime);
    }

    public List<Pawn> getPawns() {
        return pawns;
    }

    public void shootPawn() {
        selectedPawn.applyForce(tensionVector);
        selectedPawn = null;
        tensionVector.setX(0);
        tensionVector.setY(0);
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
