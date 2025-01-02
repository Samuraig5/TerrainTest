package Testing;

import Rendering.Material;
import WorldSpace.Object3D;
import WorldSpace.Triangle;
import WorldSpace.Vector2D;
import WorldSpace.Vector3D;

public class Cube extends Object3D
{
    public Cube(double size)
    {
        points = new Vector3D[]{
                new Vector3D(0, 0, 0),
                new Vector3D(0, size, 0),
                new Vector3D(size, size, 0),
                new Vector3D(size, 0, 0),
                new Vector3D(0, 0, size),
                new Vector3D(0, size, size),
                new Vector3D(size, size, size),
                new Vector3D(size, 0, size),
        };

        Triangle tri;

        //Front Face
        tri = new Triangle(points[0], points[1], points[2]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(0,0),new Vector2D(1,0)));
        mesh.add(tri);

        tri = new Triangle(points[0], points[2], points[3]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(1,0),new Vector2D(1,1)));
        mesh.add(tri);

        //Back Face
        tri = new Triangle(points[6], points[5], points[4]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(0,0),new Vector2D(1,0)));
        mesh.add(tri);
        tri = new Triangle(points[6], points[4], points[7]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(1,0),new Vector2D(1,1)));
        mesh.add(tri);

        //Right Face
        tri = new Triangle(points[3], points[2], points[6]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(0,0),new Vector2D(1,0)));
        mesh.add(tri);
        tri = new Triangle(points[3], points[6], points[7]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(1,0),new Vector2D(1,1)));
        mesh.add(tri);

        //Left Face
        tri = new Triangle(points[4], points[5], points[1]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(0,0),new Vector2D(1,0)));
        mesh.add(tri);
        tri = new Triangle(points[4], points[1], points[0]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(1,0),new Vector2D(1,1)));
        mesh.add(tri);

        //Top Face
        tri = new Triangle(points[1], points[5], points[6]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(0,0),new Vector2D(1,0)));
        mesh.add(tri);
        tri = new Triangle(points[1], points[6], points[2]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(1,0),new Vector2D(1,1)));
        mesh.add(tri);

        //Bottom Face
        tri = new Triangle(points[3], points[7], points[4]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(0,0),new Vector2D(1,0)));
        mesh.add(tri);
        tri = new Triangle(points[3], points[4], points[0]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(1,0),new Vector2D(1,1)));
        mesh.add(tri);
    }
}
