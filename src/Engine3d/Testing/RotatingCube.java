package Engine3d.Testing;

import Engine3d.Model.SimpleMeshes.CubeMesh;
import Engine3d.Physics.Object3D;
import Engine3d.Time.Updatable;
import Engine3d.Math.Vector.Vector3D;

public class RotatingCube extends CubeMesh implements Updatable
{
    Vector3D rotationSpeed;
    public RotatingCube(Object3D object3D, double size, Vector3D rotationSpeed) {
        super(object3D, size);
        this.rotationSpeed = rotationSpeed;
    }

    @Override
    public void update(double deltaTime) {
        meshOffrot.translate(new Vector3D(rotationSpeed.x() * deltaTime,
                                        rotationSpeed.y() * deltaTime,
                                        rotationSpeed.z() * deltaTime));
    }
}
