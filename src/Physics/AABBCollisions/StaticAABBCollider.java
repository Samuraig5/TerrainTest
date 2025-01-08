package Physics.AABBCollisions;

import Engine3d.Model.Mesh;

public class StaticAABBCollider extends AABBCollider
{
    public StaticAABBCollider(StaticAABBObject object3D) {
        super(object3D);
    }

    public StaticAABBCollider(StaticAABBObject object3D, Mesh colliderMesh) {
        super(object3D, colliderMesh);
    }

    @Override
    public double getWeight() {
        return -1;
    }

    @Override
    public void setWeight(double weight) {
        //Do nothing
    }
}
