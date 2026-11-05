package Physics;

import Engine3d.Objects.Object3D;
import Engine3d.Rendering.Camera;
import Math.Geometries.Box;
import Math.Matrix4x4;
import Math.Raycast.Ray;
import Engine3d.Model.UnrotatableBox;
import Math.Raycast.RayCollision;
import Physics.AABBCollisions.DynamicAABBObject;
import Math.Vector.Vector3D;
import Engine3d.Rendering.PlayerCamera;
import Engine3d.Scene;
import Physics.Shapes.CylinderShape;

import java.util.List;

public class PlayerObject extends PhysicsBody
{
    private Vector3D SIZE = new Vector3D(1, 1.8, 1);
    private Vector3D position = new Vector3D(0,0,0);
    private Vector3D rotation = new Vector3D(0,0,0); //BODY: yaw only
    private double pitch = 0; //LOOK: up/down, camera only
    private final Vector3D cameraOffset = new Vector3D(0,1.5,0);
    private Camera camera;
    private volatile boolean grounded = false;
    private volatile long lastGroundedNanos = 0;

    private static final double PROBE_UP = 0.5;   // start the ray above the feet
    private static final double SKIN     = 0.15;  // grounded if surface is within this of the
    private static final double INSET = 0.05;

    public PlayerObject(Scene scene, PlayerCamera camera)
    {
        super(scene);
        this.camera = camera;
        camera.setPlayerObject(this);
        Vector3D min = new Vector3D(-(SIZE.x()/2), 0, -(SIZE.z()/2));
        Vector3D max = new Vector3D(SIZE.x()/2, SIZE.y(), SIZE.z()/2);
        UnrotatableBox playerMesh = new UnrotatableBox(new Box(min, max));
        setMesh(playerMesh);
        getAABBCollider().setShape(new CylinderShape(this, SIZE.x() / 2, SIZE.y()));
    }

    public Vector3D getCameraOffset() {
        return cameraOffset;
    }

    @Override
    public boolean isGrounded() {
        return grounded;
    }

    private void updateGrounded() {
        Vector3D up = Vector3D.UP().scaled(PROBE_UP);
        Vector3D p  = getPosition();

        double r = SIZE.x() / 2 - INSET;
        double d = r / Math.sqrt(2);   // (±d, ±d) is r from the centre

        Vector3D[] offsets = {
                new Vector3D(0, 0, 0),
                new Vector3D( d, 0,  d), new Vector3D( d, 0, -d),
                new Vector3D(-d, 0,  d), new Vector3D(-d, 0, -d),
        };

        double dist = Double.POSITIVE_INFINITY;
        for (Vector3D off : offsets) {
            Vector3D origin = p.translated(off).translated(up);
            dist = Math.min(dist, getScene().groundDistanceBelow(origin));
        }

        boolean onRay = dist <= PROBE_UP + SKIN;
        grounded = (onRay || super.isGrounded()) && getVelocity().y() <= 0;

        if (grounded) lastGroundedNanos = System.nanoTime();
    }

    @Override
    public void rotate(Vector3D delta) {
        double maxPitch = Math.toRadians(89), minPitch = Math.toRadians(-89);
        pitch = Math.max(minPitch, Math.min(maxPitch, pitch + delta.x()));
        rotation = new Vector3D(0, rotation.y() + delta.y(), 0);   // yaw accumulates, no pitch/roll
    }

    @Override
    public Vector3D getRotation() {
        return rotation;
    }

    @Override
    public Vector3D getDirection() {
        return Matrix4x4.get3dRotationMatrix(rotation).matrixVectorMultiplication(Vector3D.FORWARD());
    }

    public Vector3D getLookRotation() { return new Vector3D(pitch, rotation.y(), 0); }


    public void resetMomentum() { setVelocity(new Vector3D(0, 0, 0)); }
    @Override
    public void translate(Vector3D delta) {
        position = position.translated(delta);
    }

    public void localTranslate(Vector3D delta) {
        Vector3D forwardMovement = getDirection();
        forwardMovement = forwardMovement.y(0);
        forwardMovement = forwardMovement.normalized();
        forwardMovement = forwardMovement.scaled(delta.z());
        Vector3D sidewardMovement = getDirection(Vector3D.LEFT()).scaled(delta.x());
        Vector3D movement = forwardMovement.translated(sidewardMovement);
        movement = movement.translated(new Vector3D(0,delta.y(),0));
        translate(movement);
    }

    @Override
    public Vector3D getPosition() {
        return position;
    }

    @Override
    public void update(double deltaTime) {
        updateGrounded();
        tryJump();
        super.update(deltaTime);
    }

    // PlayerObject:
    private volatile long lastJumpRequestNanos = 0;
    public void requestJump() { lastJumpRequestNanos = System.nanoTime(); }

    private static final long COYOTE = 100_000_000L;  // 100 ms
    private static final long BUFFER = 100_000_000L;

    private void tryJump() {
        long now = System.nanoTime();
        boolean recentlyGrounded = now - lastGroundedNanos < COYOTE;   // coyote time
        boolean bufferedPress   = now - lastJumpRequestNanos < BUFFER; // jump buffering
        if (recentlyGrounded && bufferedPress) {
            setVelocity(getVelocity().y(0.35));
            lastJumpRequestNanos = 0;   // consume it
            lastGroundedNanos = 0;      // prevent double-jump
        }
    }
}
