package pl.edu.tcs.tcsball.model;

public class Pawn extends PhysicsBody {
    private static final double DEFAULT_MASS = 2.0;
    private static final double DEFAULT_RESTITUTION = 0.78;
    private static final double SHOT_POWER = 6.0;
    private final int team;

    public Pawn(double startX, double startY, double radius, int team) {
        super(
                new Vector2D(startX, startY),
                radius,
                DEFAULT_MASS,
                DEFAULT_RESTITUTION
        );

        this.team = team;
    }

    public int getTeam() {
        return team;
    }

    public void applyForce(Vector2D force) {
        applyImpulse(force.multiply(SHOT_POWER));
    }
}
