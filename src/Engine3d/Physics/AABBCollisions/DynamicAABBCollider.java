package Engine3d.Physics.AABBCollisions;

import Engine3d.Math.Vector.Vector3D;
import Engine3d.Model.Mesh;
import Engine3d.Physics.Object3D;

public class DynamicAABBCollider extends AABBCollider
{
    private double weight = 1;

    public DynamicAABBCollider(DynamicAABBObject object3D) {
        super(object3D);
    }
    public DynamicAABBCollider(DynamicAABBObject object3D, Mesh colliderMesh) {
        super(object3D, colliderMesh);
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
