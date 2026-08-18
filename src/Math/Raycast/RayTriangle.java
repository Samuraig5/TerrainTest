package Math.Raycast;

import Math.Vector.Vector3D;

public final class RayTriangle {
    private static final double EPS = 1e-8;

    /**
     * Möller–Trumbore ray/triangle intersection (two-sided).
     * @return distance t such that hit point = origin + dir*t, or NaN on a miss.
     *         If dir is a unit vector, t is the distance in world units.
     */
    public static double intersect(Vector3D origin, Vector3D dir,
                                   Vector3D v0, Vector3D v1, Vector3D v2) {
        Vector3D edge1 = v1.translated(v0.inverted());   // v1 - v0
        Vector3D edge2 = v2.translated(v0.inverted());   // v2 - v0

        Vector3D h = dir.crossProduct(edge2);
        double a = edge1.dotProduct(h);                  // determinant
        if (Math.abs(a) < EPS) return Double.NaN;        // ray parallel to the triangle plane

        double f = 1.0 / a;
        Vector3D s = origin.translated(v0.inverted());   // origin - v0
        double u = f * s.dotProduct(h);
        if (u < 0.0 || u > 1.0) return Double.NaN;        // outside edge v0→v1

        Vector3D q = s.crossProduct(edge1);
        double v = f * dir.dotProduct(q);
        if (v < 0.0 || u + v > 1.0) return Double.NaN;    // outside the other two edges

        double t = f * edge2.dotProduct(q);
        if (t < EPS) return Double.NaN;                  // hit is at/behind the origin
        return t;
    }
}
