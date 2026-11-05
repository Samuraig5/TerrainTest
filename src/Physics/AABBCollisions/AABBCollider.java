package Physics.AABBCollisions;

import Math.Vector.Vector3D;
import Engine3d.Model.Mesh;
import Engine3d.Model.UnrotatableBox;
import Engine3d.Objects.Translatable;
import Physics.GJK_EPA.EPA;
import Physics.Shapes.ConvexShape;
import Physics.Shapes.HullShape;

import java.util.ArrayList;
import java.util.List;

public abstract class AABBCollider implements Translatable
{
    private AABBObject obj;
    private Mesh colliderMesh;
    private List<ConvexShape> convexShapes = new ArrayList<>();
    //private AABB aabb;

    public AABBCollider(AABBObject object3D) {
        this.obj = object3D;
        colliderMesh = object3D.getMesh();
        convexShapes.add(new HullShape(obj, colliderMesh));
    }
    public AABBCollider(AABBObject object3D, Mesh colliderMesh) {
        this.obj = object3D;
        this.colliderMesh = colliderMesh;
        convexShapes.add(new HullShape(obj, colliderMesh));
    }

    public void setShapes(List<ConvexShape> shapes) {
        convexShapes = shapes;
    }

    public void setShape(ConvexShape shape) {
        convexShapes = new ArrayList<>();
        convexShapes.add(shape);
    }


    public AABB getAABB() {
        double[] min_max = {
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY
        };

        for (int i = 0; i < convexShapes.size(); i++) {
            AABB bound = convexShapes.get(i).bounds();
            Vector3D min = bound.min();
            Vector3D max = bound.max();
            min_max = new double[]{
                    Math.min(min_max[0], min.x()), Math.min(min_max[1], min.y()), Math.min(min_max[2], min.z()),
                    Math.max(min_max[3], max.x()), Math.max(min_max[4], max.y()), Math.max(min_max[5], max.z())
            };
        }
        return new AABB(
                new Vector3D(min_max[0], min_max[1], min_max[2]),
                new Vector3D(min_max[3], min_max[4], min_max[5])
        );
    }

    public UnrotatableBox getAABBMesh() {
        UnrotatableBox res = new UnrotatableBox(colliderMesh.getAABB(obj.getPosition(), obj.getRotation()));
        res.translate(obj.getPosition().inverted());
        return res;
    }

    public boolean handleCollision(AABBCollider other) {
        if (other.getWeight() <= 0 && getWeight() <= 0) { return false; } // Skip Static Objects
        if (!getAABB().overlaps(other.getAABB())) { return false; } // Skip Objects who's AABB don't overlap

        boolean hit = false;
        for (ConvexShape mine : convexShapes) {
            for (ConvexShape theirs : other.convexShapes) {
                if (!mine.bounds().overlaps(theirs.bounds())) {
                    continue;   // cheap per-piece check
                }

                Vector3D move = EPA.solveEPA(theirs, mine);
                if (move == null) { continue; }
                resolveCollision(other, move);
                hit = true;
            }
        }
        return hit;
    }

    private boolean resolveCollision(AABBCollider other, Vector3D move) {
        Vector3D n = move.normalized();
        if (n.y() > 0.7) { move = new Vector3D(0, move.magnitude() / n.y(), 0); }   // walkable ground: push straight up

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
