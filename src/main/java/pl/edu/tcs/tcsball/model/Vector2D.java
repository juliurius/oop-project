package pl.edu.tcs.tcsball.model;

public class Vector2D {
    private double x;
    private double y;

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

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
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

    public Vector2D rotate(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        double newX = x * cos - y * sin;
        double newY = x * sin + y * cos;

        return new Vector2D(newX, newY);
    }

    public void addInPlace(Vector2D vector) {
        this.x += vector.getX();
        this.y += vector.getY();
    }

    public void subtractInPlace(Vector2D vector) {
        this.x -= vector.getX();
        this.y -= vector.getY();
    }

    public void multiplyInPlace(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }

    public double dot(Vector2D vector) {
        return x * vector.getX() + y * vector.getY();
    }

    public Vector2D normalized() {
        double length = length();

        if (length == 0) {
            return new Vector2D(0, 0);
        }

        return new Vector2D(x / length, y / length);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public double lengthSquared() {
        return x * x + y * y;
    }

    public double distanceTo(Vector2D vector) {
        return this.subtract(vector).length();
    }

    public static Vector2D zero() {
        return new Vector2D(0, 0);
    }
}
