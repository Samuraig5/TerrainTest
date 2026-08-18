package Physics;

import Engine3d.Rendering.Camera;
import Math.Box;
import Math.Matrix4x4;
import Math.Raycast.Ray;
import Engine3d.Model.UnrotatableBox;
import Math.Raycast.RayCollision;
import Physics.AABBCollisions.AABB;
import Physics.AABBCollisions.DynamicAABBObject;
import Math.Vector.Vector3D;
import Engine3d.Rendering.PlayerCamera;
import Engine3d.Scene;

import java.util.List;

public class PlayerObject extends DynamicAABBObject implements Gravitational
{
    private Vector3D SIZE = new Vector3D(1, 1.8, 1);
    private Vector3D position = new Vector3D(0,0,0);
    private Vector3D rotation = new Vector3D(0,0,0); //BODY: yaw only
    private double pitch = 0; //LOOK: up/down, camera only
    private final Vector3D cameraOffset = new Vector3D(0,1.5,0);
    private Camera camera;
    private Vector3D momentum = new Vector3D(0,0,0);
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
    }

    public void addMomentum(Vector3D delta) {
        momentum = momentum.translated(delta);
    }

    @Override
    public void onCollision(Vector3D appliedMove) {
        if (appliedMove.magnitude() == 0) return;
        Vector3D n = appliedMove.normalized();
        double into = momentum.dotProduct(n);   // negative = moving into the surface
        if (into < 0) {
            momentum = new Vector3D(momentum.x() * 0.8, Math.max(momentum.y(), 0), momentum.z() * 0.8);
        }
    }

    public Vector3D getCameraOffset() {
        return cameraOffset;
    }

    @Override
    public void applyGravity(double g, double deltaTime) {
        Vector3D delta = Vector3D.DOWN();
        delta = delta.scaled(g);
        delta = delta.scaled(deltaTime);
        if (!isGrounded()) {
            momentum = momentum.translated(delta);
        }
        else {
            momentum = momentum.y(Math.max(momentum.y(), 0));
        }
    }

    @Override
    public boolean isGrounded() {
        return grounded;
    }

    private void updateGrounded() {
        double hx = SIZE.x()/2 - INSET, hz = SIZE.z()/2 - INSET;
        Vector3D up = Vector3D.UP().scaled(PROBE_UP);
        Vector3D p  = getPosition();

        Vector3D[] offsets = {
                new Vector3D(0, 0, 0),
                new Vector3D( hx, 0,  hz), new Vector3D( hx, 0, -hz),
                new Vector3D(-hx, 0,  hz), new Vector3D(-hx, 0, -hz),
        };

        double dist = Double.POSITIVE_INFINITY;
        for (Vector3D off : offsets) {
            Vector3D origin = p.translated(off).translated(up);
            dist = Math.min(dist, getScene().groundDistanceBelow(origin));
        }

        grounded = dist <= PROBE_UP + SKIN;
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
        translate(momentum);
    }

    public RayCollision cursorRayCast(int numSteps, double raySize) {
        Ray ray = new Ray(this, getPosition().translated(getCameraOffset()) , camera.getDirection().scaled(raySize));
        return getScene().checkAndGetCollision(numSteps, ray);
    }

    public Vector3D findClosestPointToCollision(RayCollision rayCollision) {
        if (rayCollision == null) {return new Vector3D(0,0,0);}
        Vector3D col = rayCollision.collisionPoint;
        //getScene().createCollisionMarker(col); //Spawns a cube at the collision for debuggung

        Object3D target = rayCollision.collisionTarget;
        Vector3D localCol = rayCollision.collisionTarget.getMesh().worldToLocal(col, target.getPosition(), target.getRotation());
        List<Vector3D> targetPoints = rayCollision.collisionTarget.getMesh().getPoints();

        Vector3D minPoint = targetPoints.get(0);
        double minDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < targetPoints.size(); i++) {
            Vector3D targetPoint = targetPoints.get(i);
            double targetDistance = localCol.distanceTo(targetPoint);
            if (targetDistance < minDistance) {
                minPoint = targetPoint;
                minDistance = targetDistance;
            }
        }

        return minPoint;
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
            addMomentum(Vector3D.UP().scaled(0.35f));
            lastJumpRequestNanos = 0;   // consume it
            lastGroundedNanos = 0;      // prevent double-jump
        }
    }
}
