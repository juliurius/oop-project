package pl.edu.tcs.tcsball.model;

import java.util.List;

public interface GameView {
    GameState getGameState();
    List<Pawn> getPawns();
    Ball getBall();
    int getTeamScore(int team);

    Pawn getAimingPawn();
    double getArrowX();
    double getArrowY();
}
