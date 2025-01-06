package Engine3d.Physics;

import Engine3d.Model.Mesh;
import Engine3d.Physics.AABBCollider;
import Engine3d.Physics.CollidableObject;
import Engine3d.Physics.Object3D;

public class AABBObject extends Object3D implements CollidableObject {
    private AABBCollider aabb;

    @Override
    public void setMesh(Mesh mesh) {
        super.setMesh(mesh);
        aabb = new AABBCollider(this, mesh);
    }

    @Override
    public AABBCollider getAABBCollider() {
        return aabb;
    }
}
