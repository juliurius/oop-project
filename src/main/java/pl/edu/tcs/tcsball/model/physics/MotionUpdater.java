package pl.edu.tcs.tcsball.model.physics;

import pl.edu.tcs.tcsball.model.Ball;
import pl.edu.tcs.tcsball.model.Pawn;
import pl.edu.tcs.tcsball.model.PhysicsBody;
import pl.edu.tcs.tcsball.model.Vector2D;

import java.util.List;

public class MotionUpdater {
    private static final double REFERENCE_FPS = 120.0;
    private static final double FRICTION = 0.9925; // Tarcie na klatke przy 120 FPS
    private static final double PAWN_STOP_SPEED = 9.0; // px/s
    private static final double BALL_STOP_SPEED = 7.0; // px/s

    private static final double SPIN_TURN_POWER = 0.35;
    private static final double SPIN_FRICTION = 0.985;
    private static final double MIN_SPEED_FOR_SPIN = 30.0;

    public void updatePosition(PhysicsBody body, double deltaTime) {
        body.updatePosition(deltaTime);
    }

    public void applyFriction(PhysicsBody body, double deltaTime) {
        double frictionFactor = Math.pow(FRICTION, deltaTime * REFERENCE_FPS);
        Vector2D velocity = body.getVelocity().multiply(frictionFactor);

        if (velocity.length() < getStopSpeed(body)) {
            velocity = Vector2D.zero();
        }

        body.setVelocity(velocity);
    }

    public void applySpin(Ball ball, double deltaTime) {
        Vector2D velocity = ball.getVelocity();

        if (velocity.length() < MIN_SPEED_FOR_SPIN) {
            ball.setSpin(0);
            return;
        }

        double angle = ball.getSpin() * SPIN_TURN_POWER * deltaTime;
        ball.setVelocity(velocity.rotate(angle));

        double spinFactor = Math.pow(SPIN_FRICTION, deltaTime * REFERENCE_FPS);
        ball.setSpin(ball.getSpin() * spinFactor);

        ball.setAngle(ball.getAngle() + (ball.getSpin() * 1500.0 * deltaTime));
    }

    public boolean isEverythingStopped(List<Pawn> pawns, Ball ball) {
        if (ball.getVelocity().length() > BALL_STOP_SPEED) {
            return false;
        }

        for (Pawn pawn : pawns) {
            if (pawn.getVelocity().length() > PAWN_STOP_SPEED) {
                return false;
            }
        }

        return true;
    }

    private double getStopSpeed(PhysicsBody body) {
        return body instanceof Ball ? BALL_STOP_SPEED : PAWN_STOP_SPEED;
    }
}
