package tcsball.model;

public class Ball extends PhysicsBody {
    private static final double DEFAULT_MASS = 0.5;
    private static final double DEFAULT_RESTITUTION = 0.9;

    public Ball(double startX, double startY, double radius) {
        super(
                new Vector2D(startX, startY),
                radius,
                DEFAULT_MASS,
                DEFAULT_RESTITUTION
        );
    }
}
