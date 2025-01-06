package Engine3d.Model.SimpleMeshes;

import Engine3d.Math.MeshTriangle;
import Engine3d.Math.Vector.Vector2D;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Model.Mesh;
import Engine3d.Physics.Object3D;
import Engine3d.Rendering.Material;

import java.awt.*;

public class BoxMesh extends Mesh
{
    public BoxMesh(Object3D object3D, Vector3D size) {
        super(object3D);
        buildPoints(size);
        buildFaces();

        for (MeshTriangle face : faces) {
            face.getMaterial().setBaseColour(new Color(0,0,0,0));
        }
    }

    private void buildPoints(Vector3D size) {
        points = new Vector3D[]{
                new Vector3D(0, 0, 0),
                new Vector3D(0, size.y(), 0),
                new Vector3D(size.x(), size.y(), 0),
                new Vector3D(size.x(), 0, 0),
                new Vector3D(0, 0, size.z()),
                new Vector3D(0, size.y(), size.z()),
                new Vector3D(size.x(), size.y(), size.z()),
                new Vector3D(size.x(), 0, size.z()),
        };
    }

    private void buildFaces() {
        // Bottom face (0, 3, 7, 4)
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

    }
}
