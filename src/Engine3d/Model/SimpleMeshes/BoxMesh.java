package Engine3d.Model.SimpleMeshes;

import Math.Box;
import Math.MeshTriangle;
import Math.Vector.Vector2D;
import Math.Vector.Vector3D;
import Engine3d.Model.Mesh;
import Physics.Object3D;
import Engine3d.Rendering.Material;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class BoxMesh extends Mesh
{
    Vector3D size;
    Vector3D min;
    Vector3D max;
    public BoxMesh(Object3D object3D, Vector3D size) {
        super(object3D);
        this.size = size;
        buildPoints(size);
        buildFaces(size);

        setDiffuseColour(new Color(0,0,0,0));
    }

    public BoxMesh(Object3D object3D, Box box) {
        super(object3D);
        Vector3D min = box.min();
        Vector3D max = box.max();
        double x = max.x() - min.x();
        double y = max.y() - min.y();
        double z = max.z() - min.z();
        this.size = new Vector3D(x,y,z);
        buildPoints(min, max);
        buildFaces(size);
        setDiffuseColour(new Color(0,0,0,0));
    }

    @Override
    public void scale(Vector3D delta) {
        super.scale(delta);
        size.scale(delta);
        min.scale(delta);
        max.scale(delta);
    }

    @Override
    public void translate(Vector3D delta) {
        super.translate(delta);
        min.translate(delta);
        max.translate(delta);
    }

    private void buildPoints(Vector3D min, Vector3D max) {
        this.min = min;
        this.max = max;
        points = List.of(new Vector3D[]{
                new Vector3D(min.x(), min.y(), min.z()),
                new Vector3D(min.x(), max.y(), min.z()),
                new Vector3D(max.x(), max.y(), min.z()),
                new Vector3D(max.x(), min.y(), min.z()),
                new Vector3D(min.x(), min.y(), max.z()),
                new Vector3D(min.x(), max.y(), max.z()),
                new Vector3D(max.x(), max.y(), max.z()),
                new Vector3D(max.x(), min.y(), max.z()),
        });
    }

    private void buildPoints(Vector3D size) {
        buildPoints(new Vector3D(0,0,0), new Vector3D(size));
    }

    private void buildFaces(Vector3D size) {
        double x = size.x();
        double y = size.y();
        double z = size.z();

        //Front Face
        MeshTriangle tri1 = new MeshTriangle(points.get(0), points.get(1), points.get(2));
        tri1.setMaterial(new Material(new Vector2D(0,y),new Vector2D(0,0),new Vector2D(x,0)));
        faces.add(tri1);

        MeshTriangle tri2 = new MeshTriangle(points.get(0), points.get(2), points.get(3));
        tri2.setMaterial(new Material(new Vector2D(0,y),new Vector2D(x,0),new Vector2D(x,y)));
        faces.add(tri2);

        //Back Face
        MeshTriangle tri3 = new MeshTriangle(points.get(7), points.get(6), points.get(5));
        tri3.setMaterial(new Material(new Vector2D(0,y),new Vector2D(0,0),new Vector2D(x,0)));
        faces.add(tri3);
        MeshTriangle tri4 = new MeshTriangle(points.get(7), points.get(5), points.get(4));
        tri4.setMaterial(new Material(new Vector2D(0,y),new Vector2D(x,0),new Vector2D(x,y)));
        faces.add(tri4);

        //Right Face
        MeshTriangle tri5 = new MeshTriangle(points.get(3), points.get(2), points.get(6));
        tri5.setMaterial(new Material(new Vector2D(0,y),new Vector2D(0,0),new Vector2D(z,0)));
        faces.add(tri5);
        MeshTriangle tri6 = new MeshTriangle(points.get(3), points.get(6), points.get(7));
        tri6.setMaterial(new Material(new Vector2D(0,y),new Vector2D(z,0),new Vector2D(z,y)));
        faces.add(tri6);

        //Left Face
        MeshTriangle tri7 = new MeshTriangle(points.get(4), points.get(5), points.get(1));
        tri7.setMaterial(new Material(new Vector2D(0,y),new Vector2D(0,0),new Vector2D(z,0)));
        faces.add(tri7);
        MeshTriangle tri8 = new MeshTriangle(points.get(4), points.get(1), points.get(0));
        tri8.setMaterial(new Material(new Vector2D(0,y),new Vector2D(z,0),new Vector2D(z,y)));
        faces.add(tri8);

        //Top Face
        MeshTriangle tri9 = new MeshTriangle(points.get(1), points.get(5), points.get(6));
        tri9.setMaterial(new Material(new Vector2D(0,z),new Vector2D(0,0),new Vector2D(x,0)));
        faces.add(tri9);
        MeshTriangle tri10 = new MeshTriangle(points.get(1), points.get(6), points.get(2));
        tri10.setMaterial(new Material(new Vector2D(0,z),new Vector2D(x,0),new Vector2D(x,z)));
        faces.add(tri10);

        //Bottom Face
        MeshTriangle tri11 = new MeshTriangle(points.get(3), points.get(7), points.get(4));
        tri11.setMaterial(new Material(new Vector2D(0,z),new Vector2D(0,0),new Vector2D(x,0)));
        faces.add(tri11);
        MeshTriangle tri12 = new MeshTriangle(points.get(3), points.get(4), points.get(0));
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

    public void centreOn(Vector3D target) {
        Vector3D centre = size.scaled(0.5);
        Vector3D newOffset = target.translated(centre.inverted());
        translate(meshOffset.inverted());
        translate(newOffset);
    }

    public void centreToMiddleBottom() {
        centreOn(new Vector3D());
        translate(new Vector3D(0, -meshOffset.y(), 0));
    }

    public Vector3D getSize() {
        return size;
    }

    public Vector3D getMin() {
        return min;
    }

    public Vector3D getMax() {
        return max;
    }
}
