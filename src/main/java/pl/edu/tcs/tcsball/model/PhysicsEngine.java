package pl.edu.tcs.tcsball.model;

import pl.edu.tcs.tcsball.model.physics.CollisionResolver;
import pl.edu.tcs.tcsball.model.physics.GoalDetector;
import pl.edu.tcs.tcsball.model.physics.MotionUpdater;

import java.util.List;

public class PhysicsEngine {
    private final MotionUpdater motionUpdater;
    private final CollisionResolver collisionResolver;
    private final GoalDetector goalDetector;
    private final double arenaWidth;
    private final double arenaHeight;
    private int lastGoalScoredByTeam = 0;

    public PhysicsEngine(double arenaWidth, double arenaHeight) {
        motionUpdater = new MotionUpdater();
        collisionResolver = new CollisionResolver();
        goalDetector = new GoalDetector();
        this.arenaWidth = arenaWidth;
        this.arenaHeight = arenaHeight;
    }

    public void update(List<Pawn> pawns, double deltaTime) {
        for (Pawn pawn : pawns) {
            motionUpdater.updatePosition(pawn, deltaTime);
            collisionResolver.resolveWallCollision(pawn);
            motionUpdater.applyFriction(pawn, deltaTime);
        }

        collisionResolver.resolvePawnCollisions(pawns);
    }

    public void update(List<Pawn> pawns, Ball ball, double deltaTime) {
        lastGoalScoredByTeam = 0;

        update(pawns, deltaTime);

        motionUpdater.applySpin(ball, deltaTime);
        ball.updatePosition(deltaTime);
        lastGoalScoredByTeam = goalDetector.detectGoal(ball);

        if (lastGoalScoredByTeam != 0) {
            ball.setVelocity(Vector2D.zero());
            return;
        }

        collisionResolver.resolveWallCollision(ball);
        motionUpdater.applyFriction(ball, deltaTime);

        collisionResolver.resolveBallCollisions(pawns, ball);
    }

    public int getLastGoalScoredByTeam() {
        return lastGoalScoredByTeam;
    }

    public boolean wasGoalScored() {
        return lastGoalScoredByTeam != 0;
    }

    public boolean isEverythingStopped(List<Pawn> pawns, Ball ball) {
        return motionUpdater.isEverythingStopped(pawns, ball);
    }
}
