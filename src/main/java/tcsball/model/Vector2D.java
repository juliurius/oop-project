package tcsball.model;

public class Vector2D {
    private double x;
    private double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // TODO 1: add(Vector2D other) -> dodawanie wektorów (ruch)
    // TODO 2: multiply(double scalar) -> mnożenie przez siłę
    // TODO 3: getLength() -> długość wektora (przydatne do wygaszania prędkości)

    // Gettery i Settery dla x i y

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}