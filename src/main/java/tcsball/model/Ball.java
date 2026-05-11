package tcsball.model;

public class Ball extends PhysicsBody {
    private static final double DEFAULT_MASS = 1;
    private static final double DEFAULT_RESTITUTION = 0.8;

    public Ball(double startX, double startY, double radius) {
        super(
                new Vector2D(startX, startY),
                radius,
                DEFAULT_MASS,
                DEFAULT_RESTITUTION
        );
    }
}
