package pl.edu.tcs.tcsball;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pl.edu.tcs.tcsball.controller.GameManager;
import pl.edu.tcs.tcsball.model.FrameDelta;
import pl.edu.tcs.tcsball.view.RedrawPlanner;
import pl.edu.tcs.tcsball.view.RenderLayers;
import pl.edu.tcs.tcsball.view.RenderPlan;
import pl.edu.tcs.tcsball.view.Renderer;
import pl.edu.tcs.tcsball.view.input.InputHandler;

public class GameApp extends Application {

    private static final int WIDTH = GameConfig.WINDOW_WIDTH;
    private static final int HEIGHT = GameConfig.WINDOW_HEIGHT;

    private Renderer renderer;
    private GameManager gameManager;
    private RedrawPlanner redrawPlanner;

    public static void launchApp(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        RenderLayers layers = new RenderLayers(WIDTH, HEIGHT);
        StackPane root = layers.asStackPane();

        Scene scene = new Scene(root, WIDTH, HEIGHT);

        renderer = new Renderer(layers);
        gameManager = new GameManager(WIDTH, HEIGHT);
        redrawPlanner = new RedrawPlanner();

        new InputHandler(gameManager, scene);

        AnimationTimer gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                double deltaTime = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;

                FrameDelta delta = gameManager.update(deltaTime);
                RenderPlan plan = redrawPlanner.plan(
                        gameManager,
                        delta,
                        gameManager.consumeEvents(),
                        gameManager.consumeInputDelta()
                );

                if (!plan.isSkip()) {
                    renderer.render(gameManager, plan);
                }

                redrawPlanner.remember(gameManager, delta);
            }
        };

        primaryStage.setTitle("TCS Ball - MVP");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        renderer.render(gameManager, RenderPlan.fullScreen());
        redrawPlanner.syncState(gameManager.getGameState());

        gameLoop.start();
    }
}
