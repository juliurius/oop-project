package pl.edu.tcs.tcsball.model;

public class PhysicsBody {
    private Vector2D position;
    private Vector2D velocity;

    private final double radius;
    private final double mass;
    private final double restitution;

    public PhysicsBody(Vector2D position, double radius, double mass, double restitution) {
        this.position = position;
        this.radius = radius;
        this.mass = mass;
        this.restitution = restitution;
        this.velocity = new Vector2D(0, 0);
    }

    public void updatePosition(double deltaTime){
        position = position.add(velocity.multiply(deltaTime));
    }

    public void applyImpulse(Vector2D impulse){
        velocity = velocity.add(impulse.multiply(1.0 / mass));
    }

    public Vector2D getPosition() {
        return position;
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    public double getRadius() {
        return radius;
    }

    public double getMass() {
        return mass;
    }

    public double getRestitution() {
        return restitution;
    }
}
