package Physics;

import Engine3d.Object3D;
import Engine3d.Scene;

public class CollidableObject extends Object3D implements Collidable {
    Collider collider;

    public CollidableObject(Scene scene) {
        super(scene);
        collider = new Collider(true, getMesh());
    }

    @Override
    public Collider getCollider() {
        return collider;
    }

    public void doesCollision(boolean doesCollision) {
        collider.isActive(doesCollision);
    }

    public void setMass(double newMass) {
        collider.setMass(newMass);
    }
}
