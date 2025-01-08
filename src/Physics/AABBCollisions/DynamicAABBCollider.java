package Physics.AABBCollisions;

import Engine3d.Model.Mesh;

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
