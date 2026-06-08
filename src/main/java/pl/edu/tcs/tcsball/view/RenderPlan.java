package pl.edu.tcs.tcsball.view;

public final class RenderPlan {

    public static final RenderPlan SKIP = new RenderPlan(false, false, false, false, false);

    private final boolean fullScreen;
    private final boolean background;
    private final boolean gameLayer;
    private final boolean uiLayer;
    private final boolean overlay;

    public RenderPlan(boolean fullScreen, boolean background, boolean gameLayer, boolean uiLayer, boolean overlay) {
        this.fullScreen = fullScreen;
        this.background = background;
        this.gameLayer = gameLayer;
        this.uiLayer = uiLayer;
        this.overlay = overlay;
    }

    public boolean isSkip() {
        return !fullScreen && !background && !gameLayer && !uiLayer && !overlay;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public boolean isBackground() {
        return background;
    }

    public boolean isGameLayer() {
        return gameLayer;
    }

    public boolean isUiLayer() {
        return uiLayer;
    }

    public boolean isOverlay() {
        return overlay;
    }

    public static RenderPlan fullScreen() {
        return new RenderPlan(true, false, false, false, false);
    }

    public static RenderPlan enteringPlaying() {
        return new RenderPlan(false, true, true, true, false);
    }

    public static RenderPlan playing(boolean gameLayer, boolean uiLayer, boolean overlay) {
        return new RenderPlan(false, false, gameLayer, uiLayer, overlay);
    }

    public static RenderPlan goalCelebration() {
        return new RenderPlan(false, false, false, false, true);
    }
}
