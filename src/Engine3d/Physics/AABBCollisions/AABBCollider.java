package Engine3d.Physics.AABBCollisions;

import Engine3d.Math.Vector.Vector3D;
import Engine3d.Model.Mesh;
import Engine3d.Physics.Object3D;
import Engine3d.Translatable;

public abstract class AABBCollider implements Translatable
{
    private Object3D obj;
    private Mesh colliderMesh;
    //private AABB aabb;

    public AABBCollider(Object3D object3D) {
        this.obj = object3D;
        colliderMesh = object3D.getMesh();
    }
    public AABBCollider(Object3D object3D, Mesh colliderMesh) {
        this.obj = object3D;
        this.colliderMesh = colliderMesh;
    }

    public AABB getAABB() {
        //This is a potential optimazation if we can make sure aabb is updated when the mesh changes.
        //if (aabb != null) { return aabb; }
        //return colliderMesh.getAABB();
        return colliderMesh.getAABB();
    }

    public boolean handleCollision(AABBCollider other) {
        Vector3D move = getAABB().collision(other.getAABB());
        if (move.isEmpty()) {return false;}

        if (other.getWeight() <= 0 && getWeight() <= 0) { return false; }

        if (other.getWeight() <= 0 && getWeight() > 0) { translate(move); } //Other object is immovable but this isn't
        else if (other.getWeight() > 0 && getWeight() <= 0) { other.translate(move.inverted()); }
        else {
            double weightFactor = getWeight() / other.getWeight();

            Vector3D myMove = move.scaled(1/weightFactor);
            Vector3D otherMove = move.scaled(weightFactor);

            translate(myMove);
            other.translate(otherMove.inverted());
        }

        return true;
    }

    abstract public double getWeight();

    abstract public void setWeight(double weight);

    @Override
    public void translate(Vector3D delta) {
        obj.translate(delta);
    }

    @Override
    public Vector3D getPosition() {
        return obj.getPosition();
    }
}
