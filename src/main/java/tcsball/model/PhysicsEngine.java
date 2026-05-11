package tcsball.model;

import tcsball.GameConfig;
import java.util.List;

public class PhysicsEngine {
    private static final double FRICTION = 0.9999; // Tarcie
    private static final double STOP_SPEED = 1; // !!!!!! predkosc "przygaszenia"

    private final double arenaWidth;
    private final double arenaHeight;

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

    private void applyFriction(Pawn pawn, double deltaTime) {
        double frictionFactor = FRICTION;
        Vector2D velocity = pawn.getVelocity().multiply(frictionFactor);

        if(velocity.length() < STOP_SPEED) {
            velocity = Vector2D.zero();
        }

        pawn.setVelocity(velocity);
    }

    private void resolveWallCollision(Pawn pawn) {
        Vector2D position = pawn.getPosition();
        Vector2D velocity = pawn.getVelocity();

        double x = position.getX();
        double y = position.getY();
        double radius = pawn.getRadius();
        double restitution = pawn.getRestitution();

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

        pawn.setPosition(new Vector2D(x, y));
        pawn.setVelocity(velocity);
    }

    private void resolvePawnCollisions(List<Pawn> pawns) {
        for (int i = 0; i< pawns.size();i++) {
            for (int j = i+1; j<pawns.size();j++) {
                resolveCollision(pawns.get(i), pawns.get(j));
            }
        }
    }

    private void resolveCollision(Pawn pawn1, Pawn pawn2) {
        Vector2D difference = pawn2.getPosition().subtract(pawn1.getPosition());
        double distance = difference.length();
        double minDistance = pawn1.getRadius() + pawn2.getRadius();

        if(distance == 0 || distance >= minDistance) {
            return;
        }

        Vector2D normal = difference.normalized();
        double overlap = minDistance - distance;

        Vector2D firstPosition = pawn1.getPosition().subtract(normal.multiply(overlap / 2));
        Vector2D secondPosition = pawn2.getPosition().add(normal.multiply(overlap / 2));

        pawn1.setPosition(firstPosition);
        pawn2.setPosition(secondPosition);

        Vector2D firstVelocity = pawn1.getVelocity();
        Vector2D secondVelocity = pawn2.getVelocity();

        pawn1.setVelocity(secondVelocity.multiply(pawn1.getRestitution()));
        pawn2.setVelocity(firstVelocity.multiply(pawn2.getRestitution()));
    }

    public boolean isEverythingStopped(List<Pawn> pawns) {
        for(Pawn pawn : pawns) {
            if(pawn.getVelocity().length() > STOP_SPEED) return false;
        }

        return true;
    }
}
