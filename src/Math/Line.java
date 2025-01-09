package Math;

import Math.Vector.Vector3D;

public class Line
{
    Vector3D p1;
    Vector3D p2;

    double distanceToLastIntersect;

    public Line(Vector3D p1, Vector3D p2)
    {
        this.p1 = new Vector3D(p1);
        this.p2 = new Vector3D(p2);
    }

    public Vector3D p1() {return p1;}
    public Vector3D p2() {return p2;}

    /**
     * Finds the point at which a given plane intersects with this line.
     * It also returns the distance of the point from the other points in tOut. Range = [0, 1]
     * @param planePosition Position of the plane.
     * @param planeNormal Normal of the plane.
     * @return Intersection point (if any).
     */
    public Vector3D getIntersectToPlane(Vector3D planePosition, Vector3D planeNormal)
    {
        planeNormal.normalize();
        double plane_d = -planeNormal.dotProduct(planePosition);
        double ad = p1.dotProduct(planeNormal);
        double bd = p2.dotProduct(planeNormal);
        distanceToLastIntersect = (-plane_d - ad) / (bd - ad);

        Vector3D lineStartToEnd = p2.translated(p1.inverted());
        Vector3D lineToIntersect = lineStartToEnd.scaled(distanceToLastIntersect);
        return p1.translated(lineToIntersect);
    }

    public Vector3D getIntersectToPlane(Plane plane) {
        return getIntersectToPlane(plane.getPosition(), plane.getNormal());
    }

    public double getDistanceToLastIntersect() {
        return distanceToLastIntersect;
    }
}
