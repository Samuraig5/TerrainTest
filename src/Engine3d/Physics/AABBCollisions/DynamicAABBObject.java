package Engine3d.Physics.AABBCollisions;

import Engine3d.Math.Ray;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Model.Mesh;
import Engine3d.Physics.Object3D;
import Engine3d.Rendering.Scene;

public class DynamicAABBObject extends AABBObject {
    private DynamicAABBCollider aabb;

    public DynamicAABBObject(Scene scene) {
        super(scene);
    }

    @Override
    public void setMesh(Mesh mesh) {
        super.setMesh(mesh);
        aabb = new DynamicAABBCollider(this, mesh);
    }

    public DynamicAABBCollider getAABBCollider() {
        return aabb;
    }

    @Override
    public void onCollision(Vector3D appliedMove) {

    }
}
