package Engine3d.Math;

import Engine3d.Math.Vector.Vector3D;

public class Plane
{
    Vector3D position;
    Vector3D normal;

    Plane(Vector3D point, Vector3D normal) {
        this.position = point;
        this.normal = normal;
    }

    public Vector3D getPosition() {
        return position;
    }
    public Vector3D getNormal() {
        return normal;
    }
}
