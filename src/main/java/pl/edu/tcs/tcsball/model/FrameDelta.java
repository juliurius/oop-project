package pl.edu.tcs.tcsball.model;

public record FrameDelta(boolean anyBodyMoved, boolean physicsActive) {

    public static FrameDelta idle() {
        return new FrameDelta(false, false);
    }
}
