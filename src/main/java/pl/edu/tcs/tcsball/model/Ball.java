package pl.edu.tcs.tcsball.model;

public class Ball extends PhysicsBody {
    private static final double DEFAULT_MASS = 0.55;
    private static final double DEFAULT_RESTITUTION = 0.90;
    private static final double MAX_SPIN = 3.0;
    private double spin;

    public Ball(double startX, double startY, double radius) {
        super(
                new Vector2D(startX, startY),
                radius,
                DEFAULT_MASS,
                DEFAULT_RESTITUTION
        );
    }

    public double getSpin() {
        return spin;
    }

    public void setSpin(double spin) {
        if (spin > MAX_SPIN) spin = MAX_SPIN;
        if (spin < -MAX_SPIN) spin = -MAX_SPIN;

        this.spin = spin;
    }

}
