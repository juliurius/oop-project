package pl.edu.tcs.tcsball.model;

import pl.edu.tcs.tcsball.GameConfig;
import java.util.List;

public class PhysicsEngine {
    private static final double REFERENCE_FPS = 120.0;
    private static final double FRICTION = 0.9925; // Tarcie na klatke przy 120 FPS
    private static final double PAWN_STOP_SPEED = 9.0; // px/s
    private static final double BALL_STOP_SPEED = 7.0; // px/s

    private static final double SPIN_TURN_POWER = 0.35;
    private static final double SPIN_FRICTION = 0.985;
    private static final double MIN_SPEED_FOR_SPIN = 30.0;
    private static final double SPIN_FROM_HIT = 0.010;
    private static final double FULL_SPIN_HIT_SPEED = 450.0;

    private final double arenaWidth;
    private final double arenaHeight;
    private int lastGoalScoredByTeam = 0;

    public PhysicsEngine(double arenaWidth, double arenaHeight) {
        this.arenaWidth = arenaWidth;
        this.arenaHeight = arenaHeight;
    }

    public void update(List<Pawn> pawns, double deltaTime) {
        for (Pawn pawn : pawns) {
            pawn.updatePosition(deltaTime);
            resolveWallCollision(pawn);
            applyFriction(pawn, deltaTime);
        }

        resolvePawnCollisions(pawns);
    }

    public void update(List<Pawn> pawns, Ball ball, double deltaTime) {
        lastGoalScoredByTeam = 0;

        update(pawns, deltaTime);

        applySpin(ball, deltaTime);
        ball.updatePosition(deltaTime);
        lastGoalScoredByTeam = detectGoal(ball);

        if (lastGoalScoredByTeam != 0) {
            ball.setVelocity(Vector2D.zero());
            return;
        }

        resolveWallCollision(ball);
        applyFriction(ball, deltaTime);

        for (Pawn pawn : pawns) {
            resolveCollision(pawn, ball);
        }
    }

    private int detectGoal(Ball ball) {
        double x = ball.getPosition().getX();
        double y = ball.getPosition().getY();
        double radius = ball.getRadius();

        boolean insideGoalHeight = y >= GameConfig.GOAL_TOP_Y && y <= GameConfig.GOAL_BOTTOM_Y;

        if (!insideGoalHeight) {
            return 0;
        }

        if (x - radius <= GameConfig.PITCH_LEFT_X) {
            return 2;
        }

        if (x + radius >= GameConfig.PITCH_RIGHT_X) {
            return 1;
        }

        return 0;
    }

    public int getLastGoalScoredByTeam() {
        return lastGoalScoredByTeam;
    }

    public boolean wasGoalScored() {
        return lastGoalScoredByTeam != 0;
    }

    private void applyFriction(PhysicsBody body, double deltaTime) {
        double frictionFactor = Math.pow(FRICTION, deltaTime * REFERENCE_FPS);
        Vector2D velocity = body.getVelocity().multiply(frictionFactor);

        if (velocity.length() < getStopSpeed(body)) {
            velocity = Vector2D.zero();
        }

        body.setVelocity(velocity);
    }

    private void applySpin(Ball ball, double deltaTime) {
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

    private double getStopSpeed(PhysicsBody body) {
        return body instanceof Ball ? BALL_STOP_SPEED : PAWN_STOP_SPEED;
    }

    private void resolveWallCollision(PhysicsBody body) {
        Vector2D position = body.getPosition();
        Vector2D velocity = body.getVelocity();

        double x = position.getX();
        double y = position.getY();
        double radius = body.getRadius();
        double restitution = body.getRestitution();

        if (x - radius < GameConfig.PITCH_LEFT_X) {
            x = GameConfig.PITCH_LEFT_X + radius;
            velocity.setX(-velocity.getX() * restitution);
        }

        if (x + radius > GameConfig.PITCH_RIGHT_X) {
            x = GameConfig.PITCH_RIGHT_X - radius;
            velocity.setX(-velocity.getX() * restitution);
        }

        if (y - radius < GameConfig.PITCH_TOP_Y) {
            y = GameConfig.PITCH_TOP_Y + radius;
            velocity.setY(-velocity.getY() * restitution);
        }

        if (y + radius > GameConfig.PITCH_BOTTOM_Y) {
            y = GameConfig.PITCH_BOTTOM_Y - radius;
            velocity.setY(-velocity.getY() * restitution);
        }

        body.setPosition(new Vector2D(x, y));
        body.setVelocity(velocity);
    }

    private void resolvePawnCollisions(List<Pawn> pawns) {
        for (int i = 0; i< pawns.size();i++) {
            for (int j = i+1; j<pawns.size();j++) {
                resolveCollision(pawns.get(i), pawns.get(j));
            }
        }
    }

    private void resolveCollision(PhysicsBody body1, PhysicsBody body2) {
        Vector2D difference = body2.getPosition().subtract(body1.getPosition());
        double distance = difference.length();
        double minDistance = body1.getRadius() + body2.getRadius();

        if(distance == 0 || distance >= minDistance) {
            return;
        }

        Vector2D normal = difference.normalized();
        double overlap = minDistance - distance;

        Vector2D firstPosition = body1.getPosition().subtract(normal.multiply(overlap / 2));
        Vector2D secondPosition = body2.getPosition().add(normal.multiply(overlap / 2));

        body1.setPosition(firstPosition);
        body2.setPosition(secondPosition);

        Vector2D firstVelocity = body1.getVelocity();
        Vector2D secondVelocity = body2.getVelocity();

        Vector2D velocityDifference = firstVelocity.subtract(secondVelocity);
        double speedTowardEachOther = velocityDifference.dot(normal);

        // Tangent jest prostopadly do normalnej, czyli idzie "po boku" zderzenia.
        Vector2D tangent = new Vector2D(-normal.getY(), normal.getX());
        // Im wieksza predkosc po tangencie, tym bardziej uderzenie nadaje pilce spin.
        double sideHitSpeed = velocityDifference.dot(tangent);
        double hitSpeed = velocityDifference.length();
        double sideHitRatio = hitSpeed == 0 ? 0 : Math.abs(sideHitSpeed) / hitSpeed;

        if (speedTowardEachOther <= 0) {
            return;
        }

        double bounce = Math.min(body1.getRestitution(), body2.getRestitution());
        Vector2D bounceImpulse = normal.multiply(speedTowardEachOther * bounce);

        body1.setVelocity(firstVelocity.subtract(bounceImpulse));
        body2.setVelocity(secondVelocity.add(bounceImpulse));

        if (body1 instanceof Pawn && body2 instanceof Ball ball) {
            double hitPower = Math.min(hitSpeed / FULL_SPIN_HIT_SPEED, 1.0);
            double sidePower = sideHitRatio * sideHitRatio;
            // Kazde boczne trafienie nadaje spin, ale slabe/centralne strzaly daja prawie zerowy efekt.
            double spinPower = hitPower * sidePower;

            ball.setSpin(ball.getSpin() - sideHitSpeed * SPIN_FROM_HIT * spinPower);
        }
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
}
