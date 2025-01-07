package Engine3d.Physics.AABBCollisions;

import Engine3d.Math.Vector.Vector3D;
import Engine3d.Physics.Object3D;

public abstract class AABBObject extends Object3D
{
    abstract public void onCollision(Vector3D appliedMove);
}
