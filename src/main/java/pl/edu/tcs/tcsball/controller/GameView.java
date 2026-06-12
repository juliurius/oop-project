package pl.edu.tcs.tcsball.controller;

import pl.edu.tcs.tcsball.model.ReadOnlyBall;
import pl.edu.tcs.tcsball.model.ReadOnlyPawn;

import java.util.List;

public interface GameView {
    GameState getGameState();

    List<? extends ReadOnlyPawn> getPawns();

    ReadOnlyBall getBall();

    int getTeamScore(int team);

    int getCurrentTurn();

    ReadOnlyPawn getAimingPawn();

    double getArrowX();

    double getArrowY();

    double getActualMouseX();

    double getActualMouseY();

    boolean isEverythingStopped();

    String getTeamPawnFlagCode(int team);
}
