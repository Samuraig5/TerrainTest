package WorldSpace;

import Rendering.Material;

import java.awt.*;

public class Triangle implements Translatable
{
    private final Vector3D[] points = new Vector3D[3];
    private Material material = new Material();
    public Triangle(Vector3D point0, Vector3D point1, Vector3D point2)
    {
        points[0] = point0;
        points[1] = point1;
        points[2] = point2;
    }
    public Triangle(Triangle tri)
    {
        material = new Material(tri.getMaterial());
        points[0] = new Vector3D(tri.points[0].x(), tri.points[0].y(), tri.points[0].z());
        points[1] = new Vector3D(tri.points[1].x(), tri.points[1].y(), tri.points[1].z());
        points[2] = new Vector3D(tri.points[2].x(), tri.points[2].y(), tri.points[2].z());
    }

    public Vector3D[] getPoints() {return points;}

    public Vector3D getNormal()
    {
        Vector3D line1 = new Vector3D(points[1]);
        Vector3D line2 = new Vector3D(points[2]);

        line1.translate(points[0].inverse());
        line2.translate(points[0].inverse());

        Vector3D normal = line1.crossProduct(line2);
        normal.normalize();
        return normal;
    }

    public double getMidPoint()
    {
        return (points[0].z() + points[1].z() + points[2].z()) / 3f;
    }

    @Override
    public void translate(Vector3D delta) {
        points[0].translate(delta);
        points[1].translate(delta);
        points[2].translate(delta);
    }
    public Triangle translation(Vector3D delta){
        Vector3D p1 = points[0].translation(delta);
        Vector3D p2 = points[1].translation(delta);
        Vector3D p3 = points[2].translation(delta);
        return new Triangle(p1, p2, p3);
    }
    public void scale(Vector3D scalar) {
        points[0].scale(scalar);
        points[1].scale(scalar);
        points[2].scale(scalar);
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
    public void setMaterial(Triangle tri){
        setMaterial(new Material (tri.getMaterial()));
    }
}
