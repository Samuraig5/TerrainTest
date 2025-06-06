package Physics;

import Engine3d.Model.Mesh;
import Math.Vector.Vector3D;
import Physics.GJK_EPA.EPA;
import Physics.GJK_EPA.GJK;

public abstract class Collider {
    boolean isActive;
    Mesh colliderMesh;
    double mass;

    protected void __init__(boolean isActive, Mesh colliderMesh) {
        isActive(isActive);
        setColliderMesh(colliderMesh);
        mass = 0; //Mass of 0 or lower means the collider is static (can't be moved)
    }

    public void isActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isActive() {
        try {
            return isActive;
        }
        catch (NullPointerException e) {
            System.out.println(e.getMessage() + " - Did you forget to call __init__ on collider?");
            return false;
        }
    }

    public void setColliderMesh(Mesh colliderMesh) {
        this.colliderMesh = colliderMesh;
    }

    public Mesh getColliderMesh() {
        try {
            return colliderMesh;
        }
        catch (NullPointerException e) {
            System.out.println(e.getMessage() + " - Did you forget to call __init__ on collider?");
            return null;
        }
    }

    public void setMass (double mass) {
        this.mass = mass;
    }

    public double getMass() {
        return mass;
    }

    public boolean isStatic() {
        return (mass <= 0); //If the object has mass <= 0, then it is a static object.
    }

    public boolean contains(Vector3D point) {
        if (!isActive()) { return false; }
        return GJK.boolSolveGJK(getColliderMesh(), point);
    }

    public boolean isCollidingWith(Collider other) {
        if (!isActive() || !other.isActive()) { return false; }
        return GJK.boolSolveGJK(this.getColliderMesh(), other.getColliderMesh());
    }

    public CollisionMove handleCollision(Collider other) {
        if (!this.isActive || !other.isActive) { return CollisionMove.empty(); } // If either object is inactive, they don't collide
        if (this.isStatic() && other.isStatic()) { return CollisionMove.empty(); } // If both objects are static, neither is moved

        try {
            Vector3D move = EPA.solveEPA(other.getColliderMesh(), this.getColliderMesh()); // The move that will separate the objects
            move.scaled(1.0001);
            if (move.isEmpty()) {return CollisionMove.empty();} // If the objects don't collide, neither is moved

            // If the other object is static but this one isn't, move this object by the entire move
            if (!this.isStatic() && other.isStatic()) {
                return new CollisionMove(move, 1);
            }
            // If this object is static but the other one isn't, move the other object by the entire move
            else if (this.isStatic() && !other.isStatic()) {
                return new CollisionMove(move, 0);
            }
            // If neither object is static, distribute the moves according to their mass.
            else {
                double totalMass = this.getMass() + other.getMass();
                double firstFraction = this.getMass() / totalMass;
                return new CollisionMove(move, firstFraction);
            }
        }
        catch (NullPointerException e) {
            return CollisionMove.empty();
        }
    }
}
