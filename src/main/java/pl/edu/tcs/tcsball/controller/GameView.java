package pl.edu.tcs.tcsball.controller;

import pl.edu.tcs.tcsball.model.Ball;
import pl.edu.tcs.tcsball.model.Pawn;

import java.util.List;

public interface GameView {
    GameState getGameState();

    List<Pawn> getPawns();

    Ball getBall();

    int getTeamScore(int team);

    int getCurrentTurn();

    Pawn getAimingPawn();

    double getArrowX();

    double getArrowY();

    double getActualMouseX();

    double getActualMouseY();

    boolean isEverythingStopped();

    String getTeamPawnColor(int team);

    String getTeamPawnInnerColor(int team);
}
