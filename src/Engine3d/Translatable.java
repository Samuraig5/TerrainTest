package Engine3d;

import Engine3d.Math.Vector.Vector3D;

public interface Translatable
{
    void translate(Vector3D delta);
    Vector3D getPosition();
}
