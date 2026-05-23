package pl.edu.tcs.tcsball.view;

import javafx.scene.canvas.GraphicsContext;
import pl.edu.tcs.tcsball.model.Ball;
import pl.edu.tcs.tcsball.model.GameState;
import pl.edu.tcs.tcsball.model.GameView;
import pl.edu.tcs.tcsball.model.Pawn;
import pl.edu.tcs.tcsball.view.element.*;
import pl.edu.tcs.tcsball.view.screen.GameScreen;
import pl.edu.tcs.tcsball.view.screen.GoalScreen;
import pl.edu.tcs.tcsball.view.screen.Screen;

import java.util.List;
import java.util.Map;

public class Renderer {

    private final Map<GameState, Screen> screens;
    private GameState lastState = null;


    public Renderer(GraphicsContext gc, int width, int height) {
        ScoreBoardRenderer scoreBoard = new ScoreBoardRenderer(gc);
        PitchRenderer pitch = new PitchRenderer(gc);
        BallRenderer ball = new BallRenderer(gc);
        PawnRenderer pawn = new PawnRenderer(gc);
        AimingRenderer aiming = new AimingRenderer(gc);
        ConfettiSystem confetti = new ConfettiSystem();
        GoalOverlayRenderer overlay = new GoalOverlayRenderer(gc, confetti);

        GameScreen gameScreen = new GameScreen(scoreBoard, pitch, ball, pawn, aiming);

        screens = Map.of(
                GameState.PLAYING, gameScreen,
                GameState.GOAL_SCORED, new GoalScreen(overlay, gameScreen)
        );
    }

    public void render(GameView game) {
        GameState current = game.getGameState();

        if (current != lastState) {
            if (lastState != null) screens.get(lastState).onExit();
            screens.get(current).onEnter();
            lastState = current;
        }

        screens.get(game.getGameState()).render(game);
    }
}