package pl.edu.tcs.tcsball.view.screen;

import pl.edu.tcs.tcsball.controller.GameView;
import pl.edu.tcs.tcsball.view.RenderPlan;
import pl.edu.tcs.tcsball.view.element.GoalOverlayRenderer;

public class GoalScreen implements Screen {

    private final GoalOverlayRenderer goalOverlayRenderer;
    private final GameScreen gameScreen;

    public GoalScreen(GoalOverlayRenderer goalOverlayRenderer, GameScreen gameScreen) {
        this.goalOverlayRenderer = goalOverlayRenderer;
        this.gameScreen = gameScreen;
    }

    @Override
    public void onEnter() {
        goalOverlayRenderer.start();
    }

    @Override
    public void onExit() {
        goalOverlayRenderer.stop();
    }

    @Override
    public void render(GameView game, RenderPlan plan) {
        if (plan.isGameLayer() || plan.isUiLayer() || plan.isBackground()) {
            gameScreen.render(game, plan);
        }

        if (plan.isOverlay()) {
            goalOverlayRenderer.clearOverlay();
            goalOverlayRenderer.drawGoalOverlay();
        }
    }
}
