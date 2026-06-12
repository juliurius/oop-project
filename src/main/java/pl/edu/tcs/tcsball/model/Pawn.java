package pl.edu.tcs.tcsball.model;

import pl.edu.tcs.tcsball.GameConfig;

public class Pawn extends PhysicsBody implements ReadOnlyPawn {
    private static final double DEFAULT_MASS = GameConfig.PAWN_MASS;
    private static final double DEFAULT_RESTITUTION = GameConfig.PAWN_RESTITUTION;
    private static final double SHOT_POWER = GameConfig.PAWN_SHOT_POWER;
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

    @Override
    public double getX() {
        return getPosition().getX();
    }

    @Override
    public double getY() {
        return getPosition().getY();
    }

    public void applyForce(Vector2D force) {
        applyImpulse(force.multiply(SHOT_POWER));
    }
}
