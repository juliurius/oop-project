package pl.edu.tcs.tcsball.view;

import pl.edu.tcs.tcsball.GameConfig;
import pl.edu.tcs.tcsball.controller.GameState;
import pl.edu.tcs.tcsball.controller.GameView;
import pl.edu.tcs.tcsball.view.element.*;
import pl.edu.tcs.tcsball.view.screen.*;

import java.util.Map;

public class Renderer {

    private final Map<GameState, Screen> screens;
    private final RenderLayers layers;
    private GameState lastState = null;

    public Renderer(RenderLayers layers) {
        this.layers = layers;

        ButtonRenderer uiButtonRenderer = new ButtonRenderer(layers.uiGc());
        ButtonRenderer overlayButtonRenderer = new ButtonRenderer(layers.overlayGc());

        ScoreBoardRenderer scoreBoard = new ScoreBoardRenderer(layers.uiGc(), uiButtonRenderer);
        PitchRenderer pitch = new PitchRenderer(layers.backgroundGc());
        BallRenderer ball = new BallRenderer(layers.gameGc());
        PawnRenderer pawn = new PawnRenderer(layers.gameGc());
        AimingRenderer aiming = new AimingRenderer(layers.overlayGc());
        ConfettiSystem confetti = new ConfettiSystem();
        GoalOverlayRenderer goalOverlay = new GoalOverlayRenderer(layers.overlayGc(), confetti);

        GameScreen gameScreen = new GameScreen(
                layers.backgroundGc(), layers.gameGc(), layers.uiGc(), layers.overlayGc(),
                scoreBoard, pitch, ball, pawn, aiming
        );

        screens = Map.ofEntries(
                Map.entry(GameState.MENU, new MenuScreen(layers.overlayGc(), overlayButtonRenderer)),
                Map.entry(GameState.CUSTOMIZATION, new CustomizationScreen(layers.overlayGc(), overlayButtonRenderer)),
                Map.entry(GameState.HOST_LOBBY, new HostLobbyScreen(layers.overlayGc(), overlayButtonRenderer)),
                Map.entry(GameState.JOIN_LOBBY, new JoinLobbyScreen(layers.overlayGc(), overlayButtonRenderer)),
                Map.entry(GameState.CLIENT_LOBBY, new ClientLobbyScreen(layers.overlayGc(), overlayButtonRenderer)),
                Map.entry(GameState.PLAYING, gameScreen),
                Map.entry(GameState.GOAL_SCORED, new GoalScreen(goalOverlay, gameScreen))
        );
    }

    public void render(GameView game, RenderPlan plan) {
        GameState current = game.getGameState();

        if (current != lastState) {
            if (lastState != null) {
                screens.get(lastState).onExit();
            }

            prepareLayersForState(current);
            screens.get(current).onEnter();
            lastState = current;
        }

        if (!plan.isSkip()) {
            screens.get(current).render(game, plan);
        }
    }

    private void prepareLayersForState(GameState state) {
        switch (state) {
            case MENU, CUSTOMIZATION, HOST_LOBBY, JOIN_LOBBY, CLIENT_LOBBY -> layers.clearAll();
            case PLAYING -> layers.overlayGc().clearRect(0, 0,
                    GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
            case GOAL_SCORED -> {}
        }
    }
}
