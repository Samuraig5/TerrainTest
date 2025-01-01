package Testing;

import Time.Updatable;
import WorldSpace.Matrix4x4;
import WorldSpace.Rotatable;
import WorldSpace.Vector3D;

public class RotatingCube extends Cube implements Updatable
{
    double rotationSpeed;
    public RotatingCube(double size, double rotationSpeed) {
        super(size);
        this.rotationSpeed = rotationSpeed;
    }

    @Override
    public void update(double deltaTime)
    {
        rotation.translate(rotationSpeed * deltaTime);
    }
}
