package Math.Raycast;

import Math.Vector.Vector3D;
import Physics.CollidableObject;

public class RayCollision
{
    public Vector3D collisionPoint;
    public CollidableObject collisionTarget;
    public RayCollision(Vector3D collisionPoint, CollidableObject collisionTarget){
        this.collisionPoint = collisionPoint;
        this.collisionTarget = collisionTarget;
    }
}
