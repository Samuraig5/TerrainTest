package Testing;

import Time.Updatable;
import WorldSpace.Matrix4x4;
import WorldSpace.Rotatable;
import WorldSpace.Vector3D;

public class RotatingCube extends Cube implements Updatable
{
    Vector3D rotationSpeed;
    public RotatingCube(double size, Vector3D rotationSpeed) {
        super(size);
        this.rotationSpeed = rotationSpeed;
    }

    @Override
    public void update(double deltaTime) {
        rotation.translate(new Vector3D(rotationSpeed.x() * deltaTime,
                                        rotationSpeed.y() * deltaTime,
                                        rotationSpeed.z() * deltaTime));
    }
}
