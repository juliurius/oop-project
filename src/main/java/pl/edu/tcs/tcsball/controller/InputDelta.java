package pl.edu.tcs.tcsball.controller;

public class InputDelta {

    private boolean mouseMoved;
    private boolean aimingChanged;

    public void markMouseMoved() {
        mouseMoved = true;
    }

    public void markAimingChanged() {
        aimingChanged = true;
    }

    public boolean hadMouseMove() {
        return mouseMoved;
    }

    public boolean hadAimingChange() {
        return aimingChanged;
    }

    public InputDelta consume() {
        InputDelta snapshot = new InputDelta(mouseMoved, aimingChanged);
        mouseMoved = false;
        aimingChanged = false;
        return snapshot;
    }

    private InputDelta(boolean mouseMoved, boolean aimingChanged) {
        this.mouseMoved = mouseMoved;
        this.aimingChanged = aimingChanged;
    }

    public InputDelta() {}
}
