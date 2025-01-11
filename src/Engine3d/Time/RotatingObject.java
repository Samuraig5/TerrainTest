package Engine3d.Time;

import Engine3d.Scene;
import Physics.Object3D;
import Math.Vector.Vector3D;

public class RotatingObject extends Object3D implements Updatable
{
    Vector3D rotationSpeed;

    public RotatingObject(Scene scene) {
        super(scene);
    }

    public RotatingObject(Object3D source) {
        super(source);
    }

    public RotatingObject(RotatingObject source) {
        super(source);
        rotationSpeed = new Vector3D(source.rotationSpeed);
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
