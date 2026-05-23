package pl.edu.tcs.tcsball.controller;

import pl.edu.tcs.tcsball.model.*;
import pl.edu.tcs.tcsball.view.Renderer;
import pl.edu.tcs.tcsball.view.element.ScoreBoardRenderer;
import pl.edu.tcs.tcsball.view.screen.MenuScreen;
import pl.edu.tcs.tcsball.view.screen.SettingsScreen;

import java.util.List;

public class GameManager implements GameView {
    private final Match match;
    private final PhysicsEngine physics;

    private GameState gameState = GameState.MENU;
    private GameState returnAfterSettings = GameState.MENU;

    private Pawn selectedPawn = null;
    private final Vector2D tensionVector = new Vector2D(0, 0);
    private double mouseX = 0, mouseY = 0;

    public GameManager(double width, double height) {
        match = new Match();
        physics = new PhysicsEngine(width, height);
    }

    public void update(double deltaTime) {
        if (gameState != GameState.PLAYING) return;


        physics.update(match.getPawns(), match.getBall(), deltaTime);

        if (physics.wasGoalScored()) {
            scoreGoal(physics.getLastGoalScoredByTeam());
        }
    }

    public void handleMenuClick(double x, double y) {
        if (x >= MenuScreen.BTN_X && x <= MenuScreen.BTN_X + MenuScreen.BTN_WIDTH) {

            if (y >= MenuScreen.START_BTN_Y && y <= MenuScreen.START_BTN_Y + MenuScreen.BTN_HEIGHT) {
                startGame();
            }
            else if (y >= MenuScreen.SETTINGS_BTN_Y && y <= MenuScreen.SETTINGS_BTN_Y + MenuScreen.BTN_HEIGHT) {
                openSettings();
            }
        }
    }

    public void handleSettingsClick(double x, double y) {
        if (x >= SettingsScreen.BACK_BTN_X && x <= SettingsScreen.BACK_BTN_X + SettingsScreen.BACK_BTN_WIDTH &&
                y >= SettingsScreen.BACK_BTN_Y && y <= SettingsScreen.BACK_BTN_Y + SettingsScreen.BACK_BTN_HEIGHT) {

            closeSettings();
        }
    }

    public boolean handleBackToMenuClick(double x, double y) {
        if (x >= ScoreBoardRenderer.BACK_BTN_X && x <= ScoreBoardRenderer.BACK_BTN_X + ScoreBoardRenderer.BACK_BTN_WIDTH &&
                y >= ScoreBoardRenderer.BACK_BTN_Y && y <= ScoreBoardRenderer.BACK_BTN_Y + ScoreBoardRenderer.BACK_BTN_HEIGHT) {

            transitionTo(GameState.MENU);

            return true;
        }
        return false;
    }

    private void startGame() {
        match.resetGame();
        transitionTo(GameState.PLAYING);
    }

    public Ball getBall() { return match.getBall(); }

    public void shootPawn() {
        if (selectedPawn == null) return;

        selectedPawn.applyForce(tensionVector);
        selectedPawn = null;
        tensionVector.setX(0);
        tensionVector.setY(0);

        match.changeTurn();
    }

    public void startAiming(double x, double y) {
        if (!physics.isEverythingStopped(match.getPawns(), match.getBall())) {
            return;
        }

        List<Pawn> pawns = match.getPawns();

        for (Pawn pawn : pawns) {
            Vector2D position = pawn.getPosition();
            double pawnX = position.getX(), pawnY = position.getY(), pawnR = pawn.getRadius();
            double distance = Math.sqrt(Math.pow((pawnX - x), 2) + Math.pow((pawnY - y), 2));

            if (pawnR >= distance && pawn.getTeam() == match.getPlayerTurn()) {
                selectedPawn = pawn;
                break;
            }
        }
    }

    public void updateMousePosition(double x, double y) {
        if (selectedPawn == null) return;

        mouseX = x;
        mouseY = y;

        double newX = selectedPawn.getPosition().getX() - mouseX;
        double newY = selectedPawn.getPosition().getY() - mouseY;

        tensionVector.setX(newX);
        tensionVector.setY(newY);
    }

    public Pawn getAimingPawn() { return selectedPawn; }

    public double getArrowX() {
        if (selectedPawn != null)
            return selectedPawn.getPosition().getX() + tensionVector.getX();
        return 0;
    }

    public double getArrowY() {
        if (selectedPawn != null)
            return selectedPawn.getPosition().getY() + tensionVector.getY();
        return 0;
    }

    public List<Pawn> getPawns() { return match.getPawns(); }

    public int getTeamScore(int team) { return match.getTeamScore(team); }

    public GameState getGameState() { return gameState; }

    public void dismissGoal() {
        match.resetPitch();
        transitionTo(GameState.PLAYING);
    }

    public void scoreGoal(int team) {
        match.updateScore(team);

        transitionTo(GameState.GOAL_SCORED);
    }

    public void openSettings() {
        returnAfterSettings = gameState;
        transitionTo(GameState.SETTINGS);
    }

    public void closeSettings() {
        transitionTo(returnAfterSettings);
    }

    public void transitionTo(GameState nextState) {
        gameState = nextState;
    }

    @Override
    public int getCurrentTurn() {
        return match.getPlayerTurn();
    }

    public void updateActualMousePosition(double x, double y) {
        this.mouseX = x;
        this.mouseY = y;
    }

    @Override
    public double getActualMouseX() { return mouseX; }

    @Override
    public double getActualMouseY() { return mouseY; }
}
