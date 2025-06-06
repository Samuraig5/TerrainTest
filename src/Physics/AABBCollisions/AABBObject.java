package Physics.AABBCollisions;

import Math.Vector.Vector3D;
import Engine3d.Object3D;
import Engine3d.Scene;

public abstract class AABBObject extends Object3D
{
    public AABBObject(Scene scene) {
        super(scene);
    }

    abstract public void onCollision(Vector3D appliedMove);

    abstract public AABBCollider getAABBCollider();
}
