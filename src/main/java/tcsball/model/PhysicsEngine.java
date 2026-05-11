package tcsball.model;

import java.util.List;

public class PhysicsEngine {
    private static final double FRICTION = 0.98; // Tarcie
    private final double arenaWidth;
    private final double arenaHeight;

    public PhysicsEngine(double arenaWidth, double arenaHeight) {
        this.arenaWidth = arenaWidth;
        this.arenaHeight = arenaHeight;
    }

    public void update(List<Pawn> pawns, double deltaTime) {
        for (Pawn pawn : pawns) {
            // TODO 1: Aktualizacja pozycji (pawn.updatePosition(...))
            // TODO 2: Zastosowanie tarcia (zmniejszenie wektora prędkości)
            // TODO 3: Odbijanie od ścian (jeśli pozycja wychodzi za arenaWidth/arenaHeight, odwróć wektor)
        }
    }
}