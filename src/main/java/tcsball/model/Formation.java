package tcsball.model;

import tcsball.GameConfig;

import java.util.ArrayList;
import java.util.List;

public class Formation {
    public static List<Pawn> getFormation() {
        List<Pawn> pawns = new ArrayList<>();

        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 35, GameConfig.GOAL_CENTER_Y, 20, 1));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 125, GameConfig.PITCH_TOP_Y + 70, 20, 1));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 125, GameConfig.PITCH_TOP_Y + 185, 20,1 ));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 125, GameConfig.PITCH_BOTTOM_Y - 185, 20, 1));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 125, GameConfig.PITCH_BOTTOM_Y - 70, 20, 1));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 245, GameConfig.PITCH_TOP_Y + 70, 20, 1));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 245, GameConfig.PITCH_TOP_Y + 185, 20, 1));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 245, GameConfig.PITCH_BOTTOM_Y - 185, 20, 1));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 245, GameConfig.PITCH_BOTTOM_Y - 70, 20, 1));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 325, GameConfig.PITCH_TOP_Y + 185, 20, 1));
        pawns.add(new Pawn(GameConfig.PITCH_LEFT_X + 325, GameConfig.PITCH_BOTTOM_Y - 185, 20, 1));

        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 35, GameConfig.GOAL_CENTER_Y, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 125, GameConfig.PITCH_TOP_Y + 70, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 125, GameConfig.PITCH_TOP_Y + 185, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 125, GameConfig.PITCH_BOTTOM_Y - 185, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 125, GameConfig.PITCH_BOTTOM_Y - 70, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 245, GameConfig.PITCH_TOP_Y + 70, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 245, GameConfig.PITCH_TOP_Y + 185, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 245, GameConfig.PITCH_BOTTOM_Y - 185, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 245, GameConfig.PITCH_BOTTOM_Y - 70, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 325, GameConfig.PITCH_TOP_Y + 185, 20, 2));
        pawns.add(new Pawn(GameConfig.PITCH_RIGHT_X - 325, GameConfig.PITCH_BOTTOM_Y - 185, 20, 2));

        return pawns;
    }
}
