package pl.edu.tcs.tcsball.model.physics;

import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.model.Ball;

public class GoalDetector {
    public int detectGoal(Ball ball) {
        double x = ball.getPosition().x();
        double y = ball.getPosition().y();
        double radius = ball.getRadius();

        if (!isInsideGoalHeight(y)) {
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

    private boolean isInsideGoalHeight(double y) {
        return y >= GameConfig.GOAL_TOP_Y && y <= GameConfig.GOAL_BOTTOM_Y;
    }
}
