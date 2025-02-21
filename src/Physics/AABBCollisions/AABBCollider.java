package Physics.AABBCollisions;

import Math.Vector.Vector3D;
import Engine3d.Model.Mesh;
import Engine3d.Model.UnrotatableBox;
import Engine3d.Translatable;
import Physics.GJK_EPA.EPA;

public abstract class AABBCollider implements Translatable
{
    private AABBObject obj;
    private Mesh colliderMesh;
    //private AABB aabb;

    public AABBCollider(AABBObject object3D) {
        this.obj = object3D;
        colliderMesh = object3D.getMesh();
    }
    public AABBCollider(AABBObject object3D, Mesh colliderMesh) {
        this.obj = object3D;
        this.colliderMesh = colliderMesh;
    }

    public AABB getAABB() {
        AABB aabb = colliderMesh.getAABB();
        return aabb;
    }

    public UnrotatableBox getAABBMesh() {
        UnrotatableBox res = new UnrotatableBox(obj, colliderMesh.getAABB());
        res.translate(obj.getPosition().inverted());
        return res;
    }

    public boolean handleCollision(AABBCollider other) {
        try {
            //Vector3D move = getAABB().collision(other.getAABB());
            Vector3D move = EPA.solveEPA(other.obj, this.obj);
            move.scaled(1.0001);
            if (move.isEmpty()) {return false;}
            if (other.getWeight() <= 0 && getWeight() <= 0) { return false; }

            if (other.getWeight() <= 0 && getWeight() > 0) { translate(move); obj.onCollision(move);} //Other object is immovable but this isn't
            else if (other.getWeight() > 0 && getWeight() <= 0) { other.translate(move.inverted()); other.obj.onCollision(move.inverted());}
            else {
                double weightFactor = getWeight() / other.getWeight();

                Vector3D myMove = move.scaled(1/weightFactor);
                Vector3D otherMove = move.scaled(weightFactor);

                translate(myMove); obj.onCollision(myMove);
                other.translate(otherMove.inverted()); other.obj.onCollision(otherMove.inverted());
            }

            return true;
        }
        catch (NullPointerException e) {
            return false;
        }
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
