package pl.edu.tcs.tcsball.view.screen;

import pl.edu.tcs.tcsball.model.GameView;

public interface Screen {
    default void onEnter() {};
    default void onExit() {};
    void render(GameView game);
}
