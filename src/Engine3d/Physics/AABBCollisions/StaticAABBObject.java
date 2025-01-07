package Engine3d.Physics.AABBCollisions;

import Engine3d.Model.Mesh;
import Engine3d.Physics.Object3D;

public class StaticAABBObject extends Object3D
{
    private StaticAABBCollider aabb;

    @Override
    public void setMesh(Mesh mesh) {
        super.setMesh(mesh);
        aabb = new StaticAABBCollider(this, mesh);
    }

    public StaticAABBCollider getAABBCollider() {
        return aabb;
    }
}
