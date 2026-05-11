package tcsball.physics;

public class Vector2D {
    private final double x;
    private final double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Vector2D add(Vector2D vector) {
        return new Vector2D(x + vector.getX(), y + vector.getY());
    }

    public Vector2D subtract(Vector2D vector) {
        return new Vector2D(x - vector.getX(), y - vector.getY());
    }

    public Vector2D multiply(double scalar) {
        return new Vector2D(x * scalar, y * scalar);
    }

    public double dot(Vector2D vector) {
        return x * vector.getX() + y * vector.getY();
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public double lengthSquared() {
        return x * x + y * y;
    }

    public Vector2D normalized() {
        double length = length();

        if (length == 0) {
            return new Vector2D(0, 0);
        }

        return new Vector2D(x / length, y / length);
    }

    public double distanceTo(Vector2D vector) {
        return this.subtract(vector).length();
    }

    public static final Vector2D ZERO = new Vector2D(0, 0);
}
