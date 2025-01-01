package Testing;

import WorldSpace.Object3D;
import WorldSpace.Triangle;
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
        //Front Face
        mesh.add(new Triangle(points[0], points[1], points[2]));
        mesh.add(new Triangle(points[0], points[2], points[3]));

        //Back Face
        mesh.add(new Triangle(points[6], points[5], points[4]));
        mesh.add(new Triangle(points[6], points[4], points[7]));

        //Right Face
        mesh.add(new Triangle(points[3], points[2], points[6]));
        mesh.add(new Triangle(points[3], points[6], points[7]));

        //Left Face
        mesh.add(new Triangle(points[4], points[5], points[1]));
        mesh.add(new Triangle(points[4], points[1], points[0]));

        //Top Face
        mesh.add(new Triangle(points[1], points[5], points[6]));
        mesh.add(new Triangle(points[1], points[6], points[2]));

        //Bottom Face
        mesh.add(new Triangle(points[3], points[7], points[4]));
        mesh.add(new Triangle(points[4], points[4], points[0]));
    }
}
