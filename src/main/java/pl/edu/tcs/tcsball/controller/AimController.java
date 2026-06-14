package pl.edu.tcs.tcsball.controller;

import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.model.Pawn;
import pl.edu.tcs.tcsball.model.Vector2D;

import java.util.List;

public class AimController {
    private Pawn selectedPawn;
    private Vector2D tension = Vector2D.zero();
    private double mouseX;
    private double mouseY;

    public boolean selectPawnAt(List<Pawn> pawns, double x, double y, int team) {
        for (Pawn pawn : pawns) {
            Vector2D position = pawn.getPosition();
            double distance = Math.sqrt(Math.pow(position.x() - x, 2) + Math.pow(position.y() - y, 2));

            if (pawn.getRadius() >= distance && pawn.getTeam() == team) {
                selectedPawn = pawn;
                return true;
            }
        }
        return false;
    }

    public boolean aimTo(double x, double y) {
        if (selectedPawn == null) {
            return false;
        }

        mouseX = x;
        mouseY = y;

        Vector2D pulled = new Vector2D(
                selectedPawn.getPosition().x() - x,
                selectedPawn.getPosition().y() - y
        );

        if (pulled.length() > GameConfig.MAX_PULL_DISTANCE) {
            pulled = pulled.normalized().multiply(GameConfig.MAX_PULL_DISTANCE);
        }

        tension = pulled;
        return true;
    }

    public void clear() {
        selectedPawn = null;
        tension = Vector2D.zero();
    }

    public boolean hasSelection() {
        return selectedPawn != null;
    }

    public Pawn getSelectedPawn() {
        return selectedPawn;
    }

    public Vector2D getTension() {
        return tension;
    }

    public double getArrowX() {
        return selectedPawn != null ? selectedPawn.getPosition().x() + tension.x() : 0;
    }

    public double getArrowY() {
        return selectedPawn != null ? selectedPawn.getPosition().y() + tension.y() : 0;
    }

    public void setMousePosition(double x, double y) {
        mouseX = x;
        mouseY = y;
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }
}
