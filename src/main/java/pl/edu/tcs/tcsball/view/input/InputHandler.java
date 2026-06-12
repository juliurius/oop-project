package pl.edu.tcs.tcsball.view.input;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import pl.edu.tcs.tcsball.controller.GameManager;
import pl.edu.tcs.tcsball.controller.GameState;
import pl.edu.tcs.tcsball.view.element.ScoreBoardRenderer;
import pl.edu.tcs.tcsball.view.screen.ClientLobbyScreen;
import pl.edu.tcs.tcsball.view.screen.CustomizationScreen;
import pl.edu.tcs.tcsball.view.screen.HostLobbyScreen;
import pl.edu.tcs.tcsball.view.screen.JoinLobbyScreen;
import pl.edu.tcs.tcsball.view.screen.MenuScreen;

public class InputHandler {
    private final GameManager gameManager;

    public InputHandler(GameManager gameManager, Scene scene) {
        this.gameManager = gameManager;

        scene.setOnMouseClicked(mouseEvent -> {
            if (gameManager.getGameState() == GameState.GOAL_SCORED) {
                gameManager.dismissGoal();
            }
        });

        scene.setOnMousePressed(event -> {
            GameState state = gameManager.getGameState();
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();

            gameManager.updateActualMousePosition(mouseX, mouseY);

            switch (state) {
                case MENU -> handleMenuClick(mouseX, mouseY);
                case CUSTOMIZATION -> handleCustomizationClick(mouseX, mouseY);
                case HOST_LOBBY -> handleHostLobbyClick(mouseX, mouseY);
                case JOIN_LOBBY -> handleJoinLobbyClick(mouseX, mouseY);
                case CLIENT_LOBBY -> handleClientLobbyClick(mouseX, mouseY);
                case PLAYING -> handleGameplayClick(mouseX, mouseY);
                default -> {}
            }
        });

        scene.setOnMouseDragged(event -> {
            if (gameManager.getGameState() == GameState.PLAYING) {
                gameManager.updateMousePosition(event.getSceneX(), event.getSceneY());
            }
        });

        scene.setOnMouseReleased(event -> {
            if (gameManager.getGameState() == GameState.PLAYING) {
                gameManager.shootPawn();
            }
        });

        scene.setOnMouseMoved(event -> {
            gameManager.updateActualMousePosition(event.getSceneX(), event.getSceneY());
        });

        scene.addEventHandler(KeyEvent.KEY_TYPED, this::handleCustomizationKey);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, this::handleCustomizationKey);
    }

    private void handleMenuClick(double x, double y) {
        if (MenuScreen.isButtonHit(x, y, MenuScreen.LOCAL_PLAY_BTN_Y)) {
            gameManager.startLocalGame();
        } else if (MenuScreen.isButtonHit(x, y, MenuScreen.HOST_BTN_Y)) {
            gameManager.openHostLobby();
        } else if (MenuScreen.isButtonHit(x, y, MenuScreen.JOIN_BTN_Y)) {
            gameManager.openJoinLobby();
        } else if (MenuScreen.isButtonHit(x, y, MenuScreen.CUSTOMIZATION_BTN_Y)) {
            gameManager.openCustomization();
        }
    }

    private void handleCustomizationClick(double x, double y) {
        if (CustomizationScreen.isBackButtonHit(x, y)) {
            gameManager.quitToMenu();
            return;
        }

        CustomizationScreen.handleClick(x, y);

        if (CustomizationScreen.isPrevArrowHit(x, y, CustomizationScreen.Field.FLAG)) {
            gameManager.cycleFlag(-1);
        } else if (CustomizationScreen.isNextArrowHit(x, y, CustomizationScreen.Field.FLAG)) {
            gameManager.cycleFlag(1);
        } else if (CustomizationScreen.isPrevArrowHit(x, y, CustomizationScreen.Field.FORMATION)) {
            gameManager.cycleFormation(-1);
        } else if (CustomizationScreen.isNextArrowHit(x, y, CustomizationScreen.Field.FORMATION)) {
            gameManager.cycleFormation(1);
        }
    }

    private void handleHostLobbyClick(double x, double y) {
        if (HostLobbyScreen.isBackButtonHit(x, y)) {
            gameManager.leaveLobby();
            return;
        }

        if (HostLobbyScreen.isReadyButtonHit(x, y)) {
            gameManager.toggleLocalReady();
            return;
        }

        if (HostLobbyScreen.isStartButtonHit(x, y, gameManager.canStartGame())) {
            gameManager.startMultiplayerFromLobby();
        }
    }

    private void handleJoinLobbyClick(double x, double y) {
        if (JoinLobbyScreen.isBackButtonHit(x, y)) {
            gameManager.backFromJoinLobby();
            return;
        }

        if (gameManager.isJoinPending()) {
            return;
        }

        if (JoinLobbyScreen.isRefreshButtonHit(x, y)) {
            gameManager.refreshDiscoveredHosts();
            return;
        }

        int index = JoinLobbyScreen.hostIndexAt(x, y, gameManager.getDiscoveredHosts().size());
        if (index >= 0) {
            gameManager.joinHost(index);
        }
    }

    private void handleClientLobbyClick(double x, double y) {
        if (ClientLobbyScreen.isBackButtonHit(x, y)) {
            gameManager.leaveClientLobby();
            return;
        }

        if (ClientLobbyScreen.isReadyButtonHit(x, y)) {
            gameManager.toggleLocalReady();
        }
    }

    private void handleGameplayClick(double x, double y) {
        if (isBackToMenuHit(x, y)) {
            gameManager.quitToMenu();
            return;
        }

        gameManager.startAiming(x, y);
    }

    private boolean isBackToMenuHit(double x, double y) {
        return x >= ScoreBoardRenderer.BACK_BTN_X
                && x <= ScoreBoardRenderer.BACK_BTN_X + ScoreBoardRenderer.BACK_BTN_WIDTH
                && y >= ScoreBoardRenderer.BACK_BTN_Y
                && y <= ScoreBoardRenderer.BACK_BTN_Y + ScoreBoardRenderer.BACK_BTN_HEIGHT;
    }

    private void handleCustomizationKey(KeyEvent event) {
        if (gameManager.getGameState() != GameState.CUSTOMIZATION || !CustomizationScreen.isNameFieldFocused()) {
            return;
        }

        gameManager.handleCustomizationKey(event);
    }
}
