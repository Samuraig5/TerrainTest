package Engine3d.Testing;

import Engine3d.Physics.Object3D;
import Engine3d.Rendering.Scene;
import Engine3d.Time.Updatable;
import Engine3d.Math.Vector.Vector3D;

public class RotatingObject extends Object3D implements Updatable
{
    Vector3D rotationSpeed;

    public RotatingObject(Scene scene) {
        super(scene);
    }

    public void setRotationSpeed(Vector3D rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    @Override
    public void update(double deltaTime) {
        getMesh().rotate(new Vector3D(rotationSpeed.x() * deltaTime,
                                        rotationSpeed.y() * deltaTime,
                                        rotationSpeed.z() * deltaTime));
    }
}