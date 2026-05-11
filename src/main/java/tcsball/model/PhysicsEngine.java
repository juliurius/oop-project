package tcsball.model;

import tcsball.GameConfig;
import java.util.List;

public class PhysicsEngine {
    private static final double FRICTION = 0.99; // Tarcie
    private static final double STOP_SPEED = 1; // !!!!!! predkosc "przygaszenia"

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
        double frictionFactor = FRICTION;
        Vector2D velocity = body.getVelocity().multiply(frictionFactor);

        if(velocity.length() < STOP_SPEED) {
            velocity = Vector2D.zero();
        }

        body.setVelocity(velocity);
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

        body1.setVelocity(secondVelocity.multiply(body1.getRestitution()));
        body2.setVelocity(firstVelocity.multiply(body2.getRestitution()));
    }

    public boolean isEverythingStopped(List<Pawn> pawns) {
        for(Pawn pawn : pawns) {
            if(pawn.getVelocity().length() > STOP_SPEED) return false;
        }

        return true;
    }
}
