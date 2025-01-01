package WorldSpace;

import Rendering.Camera;
import Rendering.Drawer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Object3D implements Translatable, Rotatable
{
    protected Vector3D[] points;
    protected List<Triangle> mesh = new ArrayList<>();
    protected Vector3D rotation = new Vector3D();
    protected Vector3D position = new Vector3D();
    protected boolean showWireFrame = false;
    public Object3D(){}
    @Override
    public void translate(Vector3D delta) {
        position.translate(delta);
    }
    @Override
    public void rotate(Vector3D delta) {
        rotation.translate(delta);
    }

    public void drawObject(Graphics g, Camera camera)
    {
        Graphics2D g2d = (Graphics2D) g;

        List<Triangle> trianglesToDraw = new ArrayList<>();

        Matrix4x4 rotZ = getRotZ(rotation.z());
        Matrix4x4 rotX = getRotX(rotation.x());

        for (Triangle tri : mesh)
        {
            Vector3D[] triPoints = tri.getPoints();
            //= Apply rotation in Z =
            Vector3D p1rotZ = rotZ.multiplyWithVect3D(triPoints[0]);
            Vector3D p2rotZ = rotZ.multiplyWithVect3D(triPoints[1]);
            Vector3D p3rotZ = rotZ.multiplyWithVect3D(triPoints[2]);

            //= Apply rotation in X =
            Vector3D p1rotZX = rotX.multiplyWithVect3D(p1rotZ);
            Vector3D p2rotZX = rotX.multiplyWithVect3D(p2rotZ);
            Vector3D p3rotZX = rotX.multiplyWithVect3D(p3rotZ);

            //= Apply translation =
            Vector3D p1trans = new Vector3D(p1rotZX);
            p1trans.translate(position);
            Vector3D p2trans = new Vector3D(p2rotZX);
            p2trans.translate(position);
            Vector3D p3trans = new Vector3D(p3rotZX);
            p3trans.translate(position);

            //= Check if triangle normal is facing the camera =
            Vector3D triNormal = new Triangle(p1trans, p2trans, p3trans).getNormal();
            //All three points lie on the same plane so we can choose any
            Vector3D vectorFromCameraToTriangle =  new Vector3D(p1trans);
            vectorFromCameraToTriangle.translate(camera.getPosition().inverse());
            //If the camera can't see the triangle, don't draw it
            if (triNormal.dotProduct(vectorFromCameraToTriangle) > 0) { continue; }

            //= Primitive Lighting (Replace later) =
            Vector3D lightDirection = new Vector3D(0f, 0f, -1f);
            lightDirection.normalize();
            double lightDotProduct = triNormal.dotProduct(lightDirection);
            Color shadedColour = Drawer.getColourShade(tri.getBaseColour(), lightDotProduct);
            tri.setShadedColour(shadedColour);

            //= Apply Projection (3D -> 2D) =
            Vector3D p1proj = camera.projectVector(p1trans);
            Vector3D p2proj = camera.projectVector(p2trans);
            Vector3D p3proj = camera.projectVector(p3trans);

            //= Move projection into view =
            p1proj.translate(new Vector3D(1f, 1f, 0));
            p2proj.translate(new Vector3D(1f, 1f, 0));
            p3proj.translate(new Vector3D(1f, 1f, 0));

            //= Scale projection to screen =
            double centreX = 0.5f * camera.getScreenDimensions().x();
            double centreY = 0.5f * camera.getScreenDimensions().y();
            p1proj.scale(new Vector3D(centreX, centreY, 1));
            p2proj.scale(new Vector3D(centreX, centreY, 1));
            p3proj.scale(new Vector3D(centreX, centreY, 1));


            //= Add triangle to list=
            Triangle triProjected = new Triangle(p1proj, p2proj, p3proj);
            triProjected.setShadedColour(shadedColour);

            trianglesToDraw.add(triProjected);
        }

        trianglesToDraw.sort(Comparator.comparingDouble(Triangle::getMidPoint).reversed());

        for (Triangle triangle : trianglesToDraw) {
            Drawer.fillTriangle(g2d, triangle);
            if (showWireFrame) { Drawer.drawTriangle(g2d, Color.black, triangle); }
        }
    }

    Matrix4x4 getRotZ(double angle)
    {
        Matrix4x4 rotZ = new Matrix4x4();
        rotZ.mat[0][0] = Math.cos(angle);
        rotZ.mat[0][1] = Math.sin(angle);
        rotZ.mat[1][0] = -Math.sin(angle);
        rotZ.mat[1][1] = Math.cos(angle);
        rotZ.mat[2][2] = 1f;
        rotZ.mat[3][3] = 1f;
        return rotZ;
    }

    Matrix4x4 getRotX(double angle)
    {
        Matrix4x4 rotX = new Matrix4x4();
        rotX.mat[0][0] = 1f;
        rotX.mat[1][1] = Math.cos(angle * 0.5f);
        rotX.mat[1][2] = Math.sin(angle * 0.5f);
        rotX.mat[2][1] = -Math.sin(angle * 0.5f);
        rotX.mat[2][2] = Math.cos(angle * 0.5f);
        rotX.mat[3][3] = 1f;
        return rotX;
    }

    public void showWireFrame(boolean showWireFrame) {
        this.showWireFrame = showWireFrame;
    }
}
