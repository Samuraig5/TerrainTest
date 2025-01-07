package Engine3d.Physics.AABBCollisions;

import Engine3d.Model.Mesh;
import Engine3d.Physics.Object3D;

public class DynamicAABBObject extends Object3D {
    private DynamicAABBCollider aabb;

    @Override
    public void setMesh(Mesh mesh) {
        super.setMesh(mesh);
        aabb = new DynamicAABBCollider(this, mesh);
    }

    public DynamicAABBCollider getAABBCollider() {
        return aabb;
    }
}
