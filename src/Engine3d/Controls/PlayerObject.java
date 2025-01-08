package Engine3d.Controls;

import Engine3d.Math.Matrix4x4;
import Engine3d.Math.Ray;
import Engine3d.Model.SimpleMeshes.BoxMesh;
import Physics.AABBCollisions.DynamicAABBObject;
import Physics.Gravitational;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Rendering.PlayerCamera;
import Engine3d.Rendering.Scene;

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
        BoxMesh playerMesh = new BoxMesh(this, new Vector3D(1,1.8,1));
        playerMesh.centreOn(new Vector3D(0,0,0));
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
        Ray ray = new Ray(this, getPosition(), Vector3D.DOWN().scaled(0.5));
        boolean res = getScene().checkForCollision(3, ray);
        //System.out.println(res);
        return res;
    }

    @Override
    public void rotate(Vector3D delta) {
        rotation.translate(delta);
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
        Vector3D forwardMovement = getDirection().scaled(delta.z());
        Vector3D movement = forwardMovement.translated(new Vector3D(0,delta.y(),0));
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
