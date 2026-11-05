package Physics;

import Engine3d.Scene;
import Math.Vector.Vector3D;
import Physics.AABBCollisions.DynamicAABBObject;

public class PhysicsBody extends DynamicAABBObject implements Gravitational {
    private Vector3D velocity = new Vector3D(0, 0, 0);   // per frame, same as PlayerObject.momentum
    private long lastGroundNanos = 0;

    public PhysicsBody(Scene scene) { super(scene); }

    @Override public void update(double dt) {              // updatables run before physics
        translate(velocity);
    }

    private static final double MAX_FALL = 0.45;   // per frame; under half a crate's height
    @Override public void applyGravity(double g, double dt) {
        velocity = velocity.translated(Vector3D.DOWN().scaled(g * dt));
        if (velocity.y() < -MAX_FALL) { velocity = velocity.y(-MAX_FALL); }
    }

    @Override public void onCollision(Vector3D appliedMove) {
        if (appliedMove.magnitude() == 0) return;
        Vector3D n = appliedMove.normalized();
        double into = velocity.dotProduct(n);

        if (into < 0) {
            velocity = velocity.translated(n.scaled(-into));   // remove only the into-surface part

            double speed = velocity.magnitude();
            double maxFriction = 0.6 * -into;
            velocity = speed <= maxFriction ? new Vector3D(0, 0, 0) : velocity.scaled(1 - maxFriction / speed);
        }

        if (n.y() > 0.7) lastGroundNanos = System.nanoTime(); // pushed mostly upward = standing on something
    }

    @Override public boolean isGrounded() {
        return System.nanoTime() - lastGroundNanos < 100_000_000L;
    }
}