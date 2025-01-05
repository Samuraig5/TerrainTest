package Engine3d.Math;

import Engine3d.Math.Vector.Vector3D;

public class Box
{
    Vector3D min;
    Vector3D max;

    public Box(Vector3D min, Vector3D max) {
        this.min = min; this.max = max;
    }

    public boolean contains(Vector3D point) {
        return point.x() >= min.x() && point.x() <= max.x() &&
                point.y() >= min.y() && point.y() <= max.y() &&
                point.z() >= min.z() && point.z() <= max.z();
    }

    public boolean intersects(Box other) {
        return (max.x() >= other.min.x() && min.x() <= other.max.x()) &&
                (max.y() >= other.min.y() && min.y() <= other.max.y()) &&
                (max.z() >= other.min.z() && min.z() <= other.max.z());
    }
}
