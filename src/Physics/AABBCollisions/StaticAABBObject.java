package Physics.AABBCollisions;

import Math.Vector.Vector3D;
import Engine3d.Model.Mesh;
import Engine3d.Scene;

public class StaticAABBObject extends AABBObject
{
    private StaticAABBCollider aabb;

    public StaticAABBObject(Scene scene) {
        super(scene);
    }

    @Override
    public void setMesh(Mesh mesh) {
        super.setMesh(mesh);
        aabb = new StaticAABBCollider(this, mesh);
    }

    public StaticAABBCollider getAABBCollider() {
        return aabb;
    }

    @Override
    public void onCollision(Vector3D appliedMove) {

    }
}
