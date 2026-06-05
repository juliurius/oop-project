package pl.edu.tcs.tcsball.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pl.edu.tcs.tcsball.GameConfig;
import java.util.ArrayList;
import java.util.List;

public class ConfettiSystem {
    private static final int PARTICLE_COUNT = 150;
    private static final double START_Y = -10.0;
    private static final double START_Y_RANGE = 200.0;
    private static final double MIN_SPEED_Y = 2.0;
    private static final double SPEED_Y_RANGE = 4.0;
    private static final double MIN_SPEED_X = -1.5;
    private static final double SPEED_X_RANGE = 3.0;
    private static final double FULL_ROTATION_DEGREES = 360.0;
    private static final double ROTATION_SPEED = 4.0;

    private final List<ConfettiParticle> particles = new ArrayList<>();
    private boolean active = false;

    public void spawn() {
        particles.clear();
        active = true;
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particles.add(new ConfettiParticle());
        }
    }

    public void stop() {
        active = false;
        particles.clear();
    }

    public void updateAndDraw(GraphicsContext gc) {
        if (!active) return;

        for (ConfettiParticle p : particles) {
            p.update();

            gc.save();
            gc.setFill(p.color);
            gc.translate(p.x, p.y);
            gc.rotate(p.rotation);
            gc.fillRect(-4, -4, 8, 8);
            gc.restore();
        }
    }

    private static class ConfettiParticle {
        double x, y, speedY, speedX, rotation;
        Color color;

        ConfettiParticle() {
            this.x = Math.random() * GameConfig.WINDOW_WIDTH;
            this.y = START_Y - Math.random() * START_Y_RANGE;
            this.speedY = MIN_SPEED_Y + Math.random() * SPEED_Y_RANGE;
            this.speedX = MIN_SPEED_X + Math.random() * SPEED_X_RANGE;
            this.rotation = Math.random() * FULL_ROTATION_DEGREES;
            this.color = Color.hsb(Math.random() * 360, 0.7, 1.0);
        }

        void update() {
            y += speedY;
            x += speedX;
            rotation += ROTATION_SPEED;
        }
    }
}
