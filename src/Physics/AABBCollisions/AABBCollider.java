package Physics.AABBCollisions;

import Math.Vector.Vector3D;
import Engine3d.Model.Mesh;
import Engine3d.Model.UnrotatableBox;
import Engine3d.Objects.Translatable;
import Physics.GJK_EPA.EPA;
import Physics.Shapes.ConvexShape;
import Physics.Shapes.HullShape;

public abstract class AABBCollider implements Translatable
{
    private AABBObject obj;
    private Mesh colliderMesh;
    private ConvexShape convexShape;
    //private AABB aabb;

    public AABBCollider(AABBObject object3D) {
        this.obj = object3D;
        colliderMesh = object3D.getMesh();
        convexShape = new HullShape(obj, colliderMesh);
    }
    public AABBCollider(AABBObject object3D, Mesh colliderMesh) {
        this.obj = object3D;
        this.colliderMesh = colliderMesh;
        convexShape = new HullShape(obj, colliderMesh);
    }

    public AABB getAABB() {
        //AABB aabb = colliderMesh.getAABB(obj.getPosition(), obj.getRotation());
        return convexShape.bounds();
    }

    public UnrotatableBox getAABBMesh() {
        UnrotatableBox res = new UnrotatableBox(colliderMesh.getAABB(obj.getPosition(), obj.getRotation()));
        res.translate(obj.getPosition().inverted());
        return res;
    }

    public boolean handleCollision(AABBCollider other) {
        if (other.getWeight() <= 0 && getWeight() <= 0) { return false; } // Skip Static Objects
        if (!getAABB().overlaps(other.getAABB())) { return false; } // Skip Objects who's AABB don't overlap

        Vector3D move = EPA.solveEPA(other.convexShape, this.convexShape);
        if (move == null) { return false; }

        move = move.scaled(1.0001);


        if (other.getWeight() <= 0 && getWeight() > 0) { translate(move); obj.onCollision(move);} //Other object is immovable but this isn't
        else if (other.getWeight() > 0 && getWeight() <= 0) { other.translate(move.inverted()); other.obj.onCollision(move.inverted());}
        else {
            double weightFactor = getWeight() + other.getWeight();

            Vector3D myMove = move.scaled(other.getWeight() / weightFactor);
            Vector3D otherMove = move.scaled(this.getWeight() / weightFactor);

            translate(myMove); obj.onCollision(myMove);
            other.translate(otherMove.inverted()); other.obj.onCollision(otherMove.inverted());
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
