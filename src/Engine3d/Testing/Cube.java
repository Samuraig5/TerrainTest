package Engine3d.Testing;

import Engine3d.Physics.Object3D;
import Engine3d.Rendering.Material;
import Engine3d.Model.Mesh;
import Engine3d.Math.MeshTriangle;
import Engine3d.Math.Vector.Vector2D;
import Engine3d.Math.Vector.Vector3D;

public class Cube extends Mesh
{
    public Cube(Object3D object3D, double size)
    {
        super(object3D);
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

        MeshTriangle tri;

        //Front Face
        tri = new MeshTriangle(points[0], points[1], points[2]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(0,0),new Vector2D(1,0)));
        faces.add(tri);

        tri = new MeshTriangle(points[0], points[2], points[3]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(1,0),new Vector2D(1,1)));
        faces.add(tri);

        //Back Face
        tri = new MeshTriangle(points[6], points[5], points[4]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(0,0),new Vector2D(1,0)));
        faces.add(tri);
        tri = new MeshTriangle(points[6], points[4], points[7]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(1,0),new Vector2D(1,1)));
        faces.add(tri);

        //Right Face
        tri = new MeshTriangle(points[3], points[2], points[6]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(0,0),new Vector2D(1,0)));
        faces.add(tri);
        tri = new MeshTriangle(points[3], points[6], points[7]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(1,0),new Vector2D(1,1)));
        faces.add(tri);

        //Left Face
        tri = new MeshTriangle(points[4], points[5], points[1]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(0,0),new Vector2D(1,0)));
        faces.add(tri);
        tri = new MeshTriangle(points[4], points[1], points[0]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(1,0),new Vector2D(1,1)));
        faces.add(tri);

        //Top Face
        tri = new MeshTriangle(points[1], points[5], points[6]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(0,0),new Vector2D(1,0)));
        faces.add(tri);
        tri = new MeshTriangle(points[1], points[6], points[2]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(1,0),new Vector2D(1,1)));
        faces.add(tri);

        //Bottom Face
        tri = new MeshTriangle(points[3], points[7], points[4]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(0,0),new Vector2D(1,0)));
        faces.add(tri);
        tri = new MeshTriangle(points[3], points[4], points[0]);
        tri.setMaterial(new Material(new Vector2D(0,1),new Vector2D(1,0),new Vector2D(1,1)));
        faces.add(tri);

        for (MeshTriangle trig: faces)
        {
            trig.getMaterial().setTexturePath("src/Engine3d/Testing/Rock.png");
        }
    }
}
