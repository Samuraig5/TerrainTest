package Physics;

import Engine3d.Model.Mesh;
import Engine3d.Rendering.PlayerCamera;
import Engine3d.Scene;
import Math.Vector.Vector3D;
import Physics.GJK_EPA.GJK;

public abstract class Collider {
    boolean isActive;
    Mesh colliderMesh;

    protected void __init__(boolean isActive, Mesh colliderMesh) {
        isActive(isActive);
        setColliderMesh(colliderMesh);
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

    public boolean contains(Vector3D point) {
        if (!isActive()) { return false; }
        return GJK.boolSolveGJK(getColliderMesh(), point);
    }

    public boolean isCollidingWith(Collider other) {
        if (!isActive() || !other.isActive()) { return false; }
        return GJK.boolSolveGJK(this.getColliderMesh(), other.getColliderMesh());
    }
}
