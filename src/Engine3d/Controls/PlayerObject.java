package Engine3d.Controls;

import Engine3d.Physics.Gravitational;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Rendering.Camera;
import Engine3d.Rotatable;
import Engine3d.Translatable;

public class PlayerObject implements Translatable, Rotatable, Gravitational
{
    private final Camera camera;
    private final Vector3D momentum = new Vector3D();

    public PlayerObject(Camera camera)
    {
        this.camera = camera;
    }

    public void addMomentum(Vector3D delta) {
        momentum.translate(delta);
    }

    @Override
    public void applyGravity(double g, double deltaTime) {
        Vector3D delta = Vector3D.DOWN();
        delta.scale(g);
        delta.scale(deltaTime);
        if (!isGrounded()) {
            momentum.translate(delta);
        }
        else {
            momentum.y(Math.max(momentum.y(), 0));
        }
    }

    @Override
    public boolean isGrounded() {
        return camera.getPosition().y() <= 5;
    }

    @Override
    public void rotate(Vector3D delta) {
        camera.rotate(delta);
    }

    @Override
    public Vector3D getRotation() {
        return camera.getRotation();
    }

    @Override
    public Vector3D getDirection() {
        return camera.getDirection();
    }

    @Override
    public void update(double deltaTime) {
        translate(momentum);

    }

    @Override
    public void translate(Vector3D delta) {
        camera.translate(delta);
    }

    @Override
    public Vector3D getPosition() {
        return camera.getPosition();
    }
}
