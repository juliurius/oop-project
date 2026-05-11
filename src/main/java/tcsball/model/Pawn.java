package tcsball.model;

public class Pawn extends PhysicsBody {
    private static final double DEFAULT_MASS = 1.0;
    private static final double DEFAULT_RESTITUTION = 0.8;

    public Pawn(double startX, double startY, double radius) {
        super(
                new Vector2D(startX, startY),
                radius,
                DEFAULT_MASS,
                DEFAULT_RESTITUTION
        );
    }

    public void applyForce(Vector2D force) {
        applyImpulse(force);
    }
}