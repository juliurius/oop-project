package pl.edu.tcs.tcsball.view;

import pl.edu.tcs.tcsball.controller.InputDelta;
import pl.edu.tcs.tcsball.model.DomainEvent;
import pl.edu.tcs.tcsball.model.FrameDelta;
import pl.edu.tcs.tcsball.model.GameState;
import pl.edu.tcs.tcsball.model.GameView;

import java.util.Set;

public class RedrawPlanner {

    private GameState lastState;
    private boolean lastPhysicsActive;
    private boolean lastAiming;

    public RenderPlan plan(GameView game, FrameDelta delta, Set<DomainEvent> events, InputDelta input) {
        GameState state = game.getGameState();

        if (state != lastState) {
            lastState = state;
            lastAiming = false;
            lastPhysicsActive = false;
            return planForStateEntry(state);
        }

        return switch (state) {
            case MENU, CUSTOMIZATION, HOST_LOBBY, JOIN_LOBBY, CLIENT_LOBBY ->
                    input.hadMouseMove() ? RenderPlan.fullScreen() : RenderPlan.SKIP;
            case PLAYING -> planPlaying(game, delta, events, input);
            case GOAL_SCORED -> RenderPlan.goalCelebration();
        };
    }

    public void remember(GameView game, FrameDelta delta) {
        lastPhysicsActive = delta.physicsActive();
        lastAiming = game.getAimingPawn() != null;
    }

    public void syncState(GameState state) {
        lastState = state;
    }

    private RenderPlan planForStateEntry(GameState state) {
        return switch (state) {
            case MENU, CUSTOMIZATION, HOST_LOBBY, JOIN_LOBBY, CLIENT_LOBBY -> RenderPlan.fullScreen();
            case PLAYING -> RenderPlan.enteringPlaying();
            case GOAL_SCORED -> RenderPlan.goalCelebration();
        };
    }

    private RenderPlan planPlaying(GameView game, FrameDelta delta, Set<DomainEvent> events, InputDelta input) {
        boolean uiLayer = events.contains(DomainEvent.SCORE_CHANGED)
                || events.contains(DomainEvent.TURN_CHANGED)
                || delta.physicsActive() != lastPhysicsActive
                || input.hadMouseMove();

        boolean gameLayer = delta.anyBodyMoved()
                || events.contains(DomainEvent.MATCH_RESET);

        boolean aiming = game.getAimingPawn() != null
                && (input.hadAimingChange() || input.hadMouseMove());

        boolean clearAiming = lastAiming && game.getAimingPawn() == null;

        boolean overlay = aiming || clearAiming;

        if (!gameLayer && !uiLayer && !overlay) {
            return RenderPlan.SKIP;
        }

        return RenderPlan.playing(gameLayer, uiLayer, overlay);
    }
}
