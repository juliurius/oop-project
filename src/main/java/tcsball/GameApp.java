package tcsball;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import tcsball.view.Renderer;

public class GameApp extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private Renderer renderer;

    public static void launchApp(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        renderer = new Renderer(gc, WIDTH, HEIGHT);

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

                renderer.render();
            }
        };

        primaryStage.setTitle("TCS Ball - MVP");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        gameLoop.start();
    }
}