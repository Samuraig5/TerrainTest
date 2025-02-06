package Physics.GJK_EPA;

import Math.Vector.Vector3D;

public class Face {
    private Vector3D a, b, c;
    private Vector3D normal;

    public Face(Vector3D a, Vector3D b, Vector3D c) {
        this.a = a;
        this.b = b;
        this.c = c;

        // Calculate the normal of the face (cross product of two edges)
        Vector3D ab = b.translated(a.inverted());  // b - a
        Vector3D ac = c.translated(a.inverted());  // c - a
        this.normal = ab.crossProduct(ac).normalized();  // Normalized normal vector
    }

    // Get the normal vector of the face
    public Vector3D getNormal() {
        return normal;
    }

    public double getDistanceToOrigin() {
        return normal.dotProduct(a);
    }

    public Vector3D getPoint() {
        // return the centroid (average of the three vertices)
        return a.translated(b).translated(c).scaled(1.0 / 3.0);
    }
}

