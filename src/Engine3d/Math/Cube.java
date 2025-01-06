package Engine3d.Math;

import Engine3d.Math.Vector.Vector3D;

public class Cube
{
    Vector3D centre;
    double size;
    Box bounds;

    public Cube(Vector3D centre, double size) {
        this.centre = centre;
        this.size = size;

        Vector3D min = centre.translated(new Vector3D(size/2, size/2, size/2));
        Vector3D max = centre.translated(new Vector3D(size/2, size/2, size/2));

        bounds = new Box(min, max);
    }

    public boolean contains(Vector3D point) {
        return bounds.contains(point);
    }

    public boolean intersects(Box other) {
        return bounds.intersects(other);
    }

    public boolean intersects(Cube other) {
        return bounds.intersects(other.bounds);
    }

    public Vector3D getCenterPoint() {
        return centre;
    }
    public double getSize() {
        return size;
    }
}
