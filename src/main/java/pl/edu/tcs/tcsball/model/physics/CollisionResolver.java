package pl.edu.tcs.tcsball.model.physics;

import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.model.Ball;
import pl.edu.tcs.tcsball.model.Pawn;
import pl.edu.tcs.tcsball.model.PhysicsBody;
import pl.edu.tcs.tcsball.model.Vector2D;

import java.util.List;

public class CollisionResolver {
    private static final double SPIN_FROM_HIT = 0.010;
    private static final double FULL_SPIN_HIT_SPEED = 450.0;

    public void resolveWallCollision(PhysicsBody body) {
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

    public void resolvePawnCollisions(List<Pawn> pawns) {
        for (int i = 0; i < pawns.size(); i++) {
            for (int j = i + 1; j < pawns.size(); j++) {
                resolveCollision(pawns.get(i), pawns.get(j));
            }
        }
    }

    public void resolveBallCollisions(List<Pawn> pawns, Ball ball) {
        for (Pawn pawn : pawns) {
            resolveCollision(pawn, ball);
        }
    }

    private void resolveCollision(PhysicsBody body1, PhysicsBody body2) {
        Vector2D difference = body2.getPosition().subtract(body1.getPosition());
        double distance = difference.length();
        double minDistance = body1.getRadius() + body2.getRadius();

        if (distance == 0 || distance >= minDistance) {
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
}
