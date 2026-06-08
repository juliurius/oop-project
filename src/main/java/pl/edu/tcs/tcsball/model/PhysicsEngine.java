package pl.edu.tcs.tcsball.model;

import pl.edu.tcs.tcsball.model.physics.CollisionResolver;
import pl.edu.tcs.tcsball.model.physics.GoalDetector;
import pl.edu.tcs.tcsball.model.physics.MotionUpdater;

import java.util.List;
import java.util.ArrayList;

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

    public FrameDelta update(List<Pawn> pawns, Ball ball, double deltaTime) {
        lastGoalScoredByTeam = 0;

        List<double[]> pawnSnapshots = snapshotPawnPositions(pawns);
        double oldBallX = ball.getPosition().getX();
        double oldBallY = ball.getPosition().getY();
        double oldBallAngle = ball.getAngle();

        update(pawns, deltaTime);

        motionUpdater.applySpin(ball, deltaTime);
        ball.updatePosition(deltaTime);
        lastGoalScoredByTeam = goalDetector.detectGoal(ball);

        if (lastGoalScoredByTeam != 0) {
            ball.setVelocity(Vector2D.zero());
            return buildFrameDelta(pawns, ball, pawnSnapshots, oldBallX, oldBallY, oldBallAngle);
        }

        collisionResolver.resolveWallCollision(ball);
        motionUpdater.applyFriction(ball, deltaTime);

        collisionResolver.resolveBallCollisions(pawns, ball);

        return buildFrameDelta(pawns, ball, pawnSnapshots, oldBallX, oldBallY, oldBallAngle);
    }

    private List<double[]> snapshotPawnPositions(List<Pawn> pawns) {
        List<double[]> snapshots = new ArrayList<>(pawns.size());
        for (Pawn pawn : pawns) {
            snapshots.add(new double[]{pawn.getPosition().getX(), pawn.getPosition().getY()});
        }
        return snapshots;
    }

    private FrameDelta buildFrameDelta(List<Pawn> pawns, Ball ball, List<double[]> pawnSnapshots,
                                       double oldBallX, double oldBallY, double oldBallAngle) {
        boolean anyBodyMoved = ball.getPosition().getX() != oldBallX
                || ball.getPosition().getY() != oldBallY
                || ball.getAngle() != oldBallAngle;

        if (!anyBodyMoved) {
            for (int i = 0; i < pawns.size(); i++) {
                Pawn pawn = pawns.get(i);
                double[] old = pawnSnapshots.get(i);
                if (pawn.getPosition().getX() != old[0] || pawn.getPosition().getY() != old[1]) {
                    anyBodyMoved = true;
                    break;
                }
            }
        }

        boolean physicsActive = !motionUpdater.isEverythingStopped(pawns, ball);
        return new FrameDelta(anyBodyMoved, physicsActive);
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
