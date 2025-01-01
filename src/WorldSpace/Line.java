package WorldSpace;

public class Line
{
    Vector3D p1;
    Vector3D p2;

    public Line(Vector3D p1, Vector3D p2)
    {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Vector3D p1() {return p1;}
    public Vector3D p2() {return p2;}

    /**
     * Finds the point at which a given plane intersects with this line.
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
        double t = (-plane_d - ad) / (bd - ad);

        Vector3D lineStartToEnd = p2.translation(p1.inverse());
        Vector3D lineToIntersect = lineStartToEnd.scaled(t);
        return p1.translation(lineToIntersect);
    }
}
