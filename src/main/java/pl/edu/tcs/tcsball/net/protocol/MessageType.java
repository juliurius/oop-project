package pl.edu.tcs.tcsball.net.protocol;

public enum MessageType {
    JOIN_REQUEST,
    JOIN_ACCEPTED,
    LOBBY_STATE,
    PLAYER_PROFILE_UPDATED,
    PLAYER_READY,
    START_GAME,
    SHOT,
    GAME_STATE,
    GOAL_DISMISSED,
    QUIT,
    ERROR
}
