package Engine3d.Objects;

import Math.Vector.Vector3D;

public interface Translatable
{
    void translate(Vector3D delta);
    Vector3D getPosition();
}
