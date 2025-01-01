package WorldSpace;

public class Triangle implements Translatable
{
    private final Vector3D[] points = new Vector3D[3];

    public Triangle(Vector3D point0, Vector3D point1, Vector3D point2)
    {
        points[0] = point0;
        points[1] = point1;
        points[2] = point2;
    }
    public Triangle(Triangle tri)
    {
        points[0] = new Vector3D(tri.points[0].x(), tri.points[0].y(), tri.points[0].z());
        points[1] = new Vector3D(tri.points[1].x(), tri.points[1].y(), tri.points[1].z());
        points[2] = new Vector3D(tri.points[2].x(), tri.points[2].y(), tri.points[2].z());
    }

    public Vector3D[] getPoints() {return points;}

    @Override
    public void translate(Vector3D delta) {
        points[0].translate(delta);
        points[1].translate(delta);
        points[2].translate(delta);
    }
}
