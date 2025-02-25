package Math.Raycast;

import Math.Vector.Vector3D;
import Physics.AABBCollisions.AABBObject;

public class RayCollision
{
    public Vector3D collisionPoint;
    public AABBObject collisionTarget;
    public RayCollision(Vector3D collisionPoint, AABBObject collisionTarget){
        this.collisionPoint = collisionPoint;
        this.collisionTarget = collisionTarget;
    }
}
