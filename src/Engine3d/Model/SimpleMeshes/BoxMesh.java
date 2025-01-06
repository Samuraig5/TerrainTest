package Engine3d.Model.SimpleMeshes;

import Engine3d.Math.MeshTriangle;
import Engine3d.Math.Vector.Vector2D;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Model.Mesh;
import Engine3d.Physics.Object3D;
import Engine3d.Rendering.Material;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BoxMesh extends Mesh
{
    public BoxMesh(Object3D object3D, Vector3D size) {
        super(object3D);
        buildPoints(size);
        buildFaces(size);

        setDiffuseColour(new Color(0,0,0,0));
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

    private void buildFaces(Vector3D size) {
        double x = size.x();
        double y = size.y();
        double z = size.z();

        //Front Face
        MeshTriangle tri1 = new MeshTriangle(points[0], points[1], points[2]);
        tri1.setMaterial(new Material(new Vector2D(0,y),new Vector2D(0,0),new Vector2D(x,0)));
        faces.add(tri1);

        MeshTriangle tri2 = new MeshTriangle(points[0], points[2], points[3]);
        tri2.setMaterial(new Material(new Vector2D(0,y),new Vector2D(x,0),new Vector2D(x,y)));
        faces.add(tri2);

        //Back Face
        MeshTriangle tri3 = new MeshTriangle(points[7], points[6], points[5]);
        tri3.setMaterial(new Material(new Vector2D(0,y),new Vector2D(0,0),new Vector2D(x,0)));
        faces.add(tri3);
        MeshTriangle tri4 = new MeshTriangle(points[7], points[5], points[4]);
        tri4.setMaterial(new Material(new Vector2D(0,y),new Vector2D(x,0),new Vector2D(x,y)));
        faces.add(tri4);

        //Right Face
        MeshTriangle tri5 = new MeshTriangle(points[3], points[2], points[6]);
        tri5.setMaterial(new Material(new Vector2D(0,y),new Vector2D(0,0),new Vector2D(z,0)));
        faces.add(tri5);
        MeshTriangle tri6 = new MeshTriangle(points[3], points[6], points[7]);
        tri6.setMaterial(new Material(new Vector2D(0,y),new Vector2D(z,0),new Vector2D(z,y)));
        faces.add(tri6);

        //Left Face
        MeshTriangle tri7 = new MeshTriangle(points[4], points[5], points[1]);
        tri7.setMaterial(new Material(new Vector2D(0,y),new Vector2D(0,0),new Vector2D(z,0)));
        faces.add(tri7);
        MeshTriangle tri8 = new MeshTriangle(points[4], points[1], points[0]);
        tri8.setMaterial(new Material(new Vector2D(0,y),new Vector2D(z,0),new Vector2D(z,y)));
        faces.add(tri8);

        //Top Face
        MeshTriangle tri9 = new MeshTriangle(points[1], points[5], points[6]);
        tri9.setMaterial(new Material(new Vector2D(0,z),new Vector2D(0,0),new Vector2D(x,0)));
        faces.add(tri9);
        MeshTriangle tri10 = new MeshTriangle(points[1], points[6], points[2]);
        tri10.setMaterial(new Material(new Vector2D(0,z),new Vector2D(x,0),new Vector2D(x,z)));
        faces.add(tri10);

        //Bottom Face
        MeshTriangle tri11 = new MeshTriangle(points[3], points[7], points[4]);
        tri11.setMaterial(new Material(new Vector2D(0,z),new Vector2D(0,0),new Vector2D(x,0)));
        faces.add(tri11);
        MeshTriangle tri12 = new MeshTriangle(points[3], points[4], points[0]);
        tri12.setMaterial(new Material(new Vector2D(0,z),new Vector2D(x,0),new Vector2D(x,z)));
        faces.add(tri12);

    }

    public void setTexture(BufferedImage image) {
        for (int i = 0; i < faces.size(); i++) {
            faces.get(i).getMaterial().setTexture(image);
        }
    }

    public void setDiffuseColour(Color colour) {
        for (int i = 0; i < faces.size(); i++) {
            faces.get(i).getMaterial().setBaseColour(colour);
        }
    }
}
