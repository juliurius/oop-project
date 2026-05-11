package tcsball;

public class TestLauncher {
    public static void main(String[] args) {
        // Ta klasa ukrywa przed Javą fakt, że startujemy aplikację JavaFX,
        // dzięki czemu omija błąd z brakującymi komponentami.
        RendererTestApp.main(args);
    }
}