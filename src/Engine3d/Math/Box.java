package Engine3d.Math;

import Engine3d.Math.Vector.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class Box
{
    Vector3D min;
    Vector3D max;

    public Vector3D min() {return min;}
    public void min(Vector3D min) { this.min = min; }
    public Vector3D max() {return max;}
    public void max(Vector3D max) { this.max = max; }


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

    public Vector3D getCenterPoint() {
        return (min.translated(max.scaled(0.5f)));
    }

    public List<Plane> getPlanes() {
        List<Plane> planes = new ArrayList<>();

        // Left plane
        planes.add(new Plane(new Vector3D(min.x(), min.y(), min.z()), new Vector3D(-1, 0, 0)));
        // Right plane
        planes.add(new Plane(new Vector3D(max.x(), min.y(), min.z()), new Vector3D(1, 0, 0)));
        // Bottom plane
        planes.add(new Plane(new Vector3D(min.x(), min.y(), min.z()), new Vector3D(0, -1, 0)));
        // Top plane
        planes.add(new Plane(new Vector3D(min.x(), max.y(), min.z()), new Vector3D(0, 1, 0)));
        // Front plane
        planes.add(new Plane(new Vector3D(min.x(), min.y(), min.z()), new Vector3D(0, 0, -1)));
        // Back plane
        planes.add(new Plane(new Vector3D(min.x(), min.y(), max.z()), new Vector3D(0, 0, 1)));

        return planes;
    }
}
