package pl.edu.tcs.tcsball.controller;

import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.model.Pawn;
import pl.edu.tcs.tcsball.model.Vector2D;

import java.util.List;

/**
 * Stan wskaznika i procy: wybrany pionek, wektor naciagu oraz ostatnia pozycja myszy.
 * Trzyma tylko geometrie celowania — decyzje (czyja tura, tryb sieciowy) podejmuje
 * {@link GameManager}.
 */
public class AimController {
    private Pawn selectedPawn;
    private final Vector2D tension = new Vector2D(0, 0);
    private double mouseX;
    private double mouseY;

    /** Wybiera pionek danej druzyny pod kursorem; zwraca true, gdy cos wybrano. */
    public boolean selectPawnAt(List<Pawn> pawns, double x, double y, int team) {
        for (Pawn pawn : pawns) {
            Vector2D position = pawn.getPosition();
            double distance = Math.sqrt(Math.pow(position.getX() - x, 2) + Math.pow(position.getY() - y, 2));

            if (pawn.getRadius() >= distance && pawn.getTeam() == team) {
                selectedPawn = pawn;
                return true;
            }
        }
        return false;
    }

    /** Aktualizuje naciag w strone kursora (przyciety do MAX_PULL_DISTANCE). */
    public boolean aimTo(double x, double y) {
        if (selectedPawn == null) {
            return false;
        }

        mouseX = x;
        mouseY = y;

        Vector2D pulled = new Vector2D(
                selectedPawn.getPosition().getX() - x,
                selectedPawn.getPosition().getY() - y
        );

        if (pulled.length() > GameConfig.MAX_PULL_DISTANCE) {
            pulled = pulled.normalized().multiply(GameConfig.MAX_PULL_DISTANCE);
        }

        tension.set(pulled.getX(), pulled.getY());
        return true;
    }

    public void clear() {
        selectedPawn = null;
        tension.set(0, 0);
    }

    public boolean hasSelection() {
        return selectedPawn != null;
    }

    public Pawn getSelectedPawn() {
        return selectedPawn;
    }

    /** Kopia wektora naciagu (sila strzalu przed przemnozeniem przez moc pionka). */
    public Vector2D getTension() {
        return new Vector2D(tension.getX(), tension.getY());
    }

    public double getArrowX() {
        return selectedPawn != null ? selectedPawn.getPosition().getX() + tension.getX() : 0;
    }

    public double getArrowY() {
        return selectedPawn != null ? selectedPawn.getPosition().getY() + tension.getY() : 0;
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
