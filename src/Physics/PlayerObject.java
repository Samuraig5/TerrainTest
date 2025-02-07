package Physics;

import Math.Box;
import Math.Matrix4x4;
import Math.Ray;
import Engine3d.Model.UnrotatableBox;
import Physics.AABBCollisions.DynamicAABBObject;
import Math.Vector.Vector3D;
import Engine3d.Rendering.PlayerCamera;
import Engine3d.Scene;

public class PlayerObject extends DynamicAABBObject implements Gravitational
{
    private Vector3D position = new Vector3D();
    private Vector3D rotation = new Vector3D();
    private final Vector3D cameraOffset = new Vector3D(0,1.5,0);
    private final Vector3D momentum = new Vector3D();

    public PlayerObject(Scene scene, PlayerCamera camera)
    {
        super(scene);
        camera.setPlayerObject(this);
        double size = 0.5;
        Vector3D min = new Vector3D(-size, 0, -size);
        Vector3D max = new Vector3D(size, 1.8, size);
        UnrotatableBox playerMesh = new UnrotatableBox(this, new Box(min, max));
        setMesh(playerMesh);
    }

    public void addMomentum(Vector3D delta) {
        momentum.translate(delta);
    }

    @Override
    public void onCollision(Vector3D appliedMove) {

    }
    public Vector3D getCameraOffset() {
        return cameraOffset;
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
        Ray ray = new Ray(this, getPosition(), Vector3D.DOWN().scaled(0.25));
        boolean res = getScene().checkForCollision(3, ray);
        return res;
    }

    @Override
    public void rotate(Vector3D delta) {
        Vector3D newRot = rotation.translated(delta);

        double maxPitch = Math.toRadians(89);
        double minPitch = Math.toRadians(-89);

        newRot = new Vector3D(
                Math.max(minPitch, Math.min(maxPitch, newRot.x())),
                newRot.y(),
                newRot.z()
        );

        rotation = newRot;
    }

    @Override
    public Vector3D getRotation() {
        return rotation;
    }

    @Override
    public Vector3D getDirection() {
        return Matrix4x4.get3dRotationMatrix(rotation).matrixVectorMultiplication(Vector3D.FORWARD());
    }

    @Override
    public void translate(Vector3D delta) {
        position.translate(delta);
    }

    public void localTranslate(Vector3D delta) {
        Vector3D forwardMovement = getDirection();
        forwardMovement.y(0);
        forwardMovement.normalize();
        forwardMovement.scale(delta.z());
        Vector3D sidewardMovement = getDirection(Vector3D.LEFT()).scaled(delta.x());
        Vector3D movement = forwardMovement.translated(sidewardMovement);
        movement.translate(new Vector3D(0,delta.y(),0));
        translate(movement);
    }

    @Override
    public Vector3D getPosition() {
        return position;
    }

    @Override
    public void update(double deltaTime) {
        translate(momentum);
    }

}
