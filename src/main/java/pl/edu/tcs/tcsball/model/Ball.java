package pl.edu.tcs.tcsball.model;

import pl.edu.tcs.tcsball.GameConfig;

public class Ball extends PhysicsBody implements ReadOnlyBall {
    private static final double DEFAULT_MASS = GameConfig.BALL_MASS;
    private static final double DEFAULT_RESTITUTION = GameConfig.BALL_RESTITUTION;
    private static final double MAX_SPIN = GameConfig.BALL_MAX_SPIN;
    private double spin;
    private double angle = 0;

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

    @Override
    public double getX() {
        return getPosition().x();
    }

    @Override
    public double getY() {
        return getPosition().y();
    }

    public void setSpin(double spin) {
        if (spin > MAX_SPIN) spin = MAX_SPIN;
        if (spin < -MAX_SPIN) spin = -MAX_SPIN;

        this.spin = spin;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle % GameConfig.BALL_FULL_ROTATION_DEGREES;
    }
}
