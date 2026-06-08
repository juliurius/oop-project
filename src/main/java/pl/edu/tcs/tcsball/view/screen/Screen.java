package pl.edu.tcs.tcsball.view.screen;

import pl.edu.tcs.tcsball.model.GameView;
import pl.edu.tcs.tcsball.view.RenderPlan;

public interface Screen {
    default void onEnter() {}
    default void onExit() {}
    void render(GameView game, RenderPlan plan);
}
