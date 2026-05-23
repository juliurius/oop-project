package pl.edu.tcs.tcsball.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pl.edu.tcs.tcsball.GameConfig;
import java.util.ArrayList;
import java.util.List;

public class ConfettiSystem {
    private final List<ConfettiParticle> particles = new ArrayList<>();
    private boolean active = false;

    public void spawn() {
        particles.clear();
        active = true;
        for (int i = 0; i < 150; i++) {
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
            this.y = -10 - Math.random() * 200;
            this.speedY = 2 + Math.random() * 4;
            this.speedX = -1.5 + Math.random() * 3;
            this.rotation = Math.random() * 360;
            this.color = Color.hsb(Math.random() * 360, 0.7, 1.0);
        }

        void update() {
            y += speedY;
            x += speedX;
            rotation += 4;
        }
    }
}