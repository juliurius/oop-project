package tcsball.model;
import tcsball.physics.Vector2D;

public class Pawn {
    private Vector2D position; // Pozycja X, Y na boisku
    private Vector2D velocity; // Wektor prędkości (domyślnie 0,0)
    private final double radius; // Promień do rysowania i kolizji

    public Pawn(double startX, double startY, double radius) {
        this.position = new Vector2D(startX, startY);
        this.velocity = new Vector2D(0, 0);
        this.radius = radius;
    }

    // TODO 1: applyForce(Vector2D force) -> zmienia wektor prędkości (velocity)
    // TODO 2: updatePosition(double deltaTime) -> dodaje prędkość do pozycji

    // Gettery (position, radius) potrzebne dla Widoku
}