package tcsball.controller;

import tcsball.model.Pawn;
import tcsball.model.PhysicsEngine;
import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private List<Pawn> pawns;
    private PhysicsEngine physics;

    public GameManager(double width, double height) {
        this.pawns = new ArrayList<>();
        this.physics = new PhysicsEngine(width, height);

        // MVP: Jeden pionek testowy na środku
        pawns.add(new Pawn(width / 2, height / 2, 20));
    }

    public void update(double deltaTime) {
        // TODO: physics.update(pawns, deltaTime);
    }

    public List<Pawn> getPawns() {
        return pawns;
    }

    public void shootPawn(Pawn selectedPawn, double forceX, double forceY) {
        // TODO: Przekazanie wyliczonej siły strzału do pionka
    }
}