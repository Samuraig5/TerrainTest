package WorldSpace;

import Rendering.Camera;
import Rendering.Drawer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Object3D implements Translatable, Rotatable
{
    protected Vector3D[] points;
    protected List<Triangle> mesh = new ArrayList<>();
    protected Vector3D rotation = new Vector3D();
    protected Vector3D position = new Vector3D();
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

        Matrix4x4 rotZ = getRotZ(rotation.z());
        Matrix4x4 rotX = getRotX(rotation.x());

        for (Triangle tri : mesh)
        {
            Vector3D[] triPoints = tri.getPoints();

            Vector3D p1rotZ = rotZ.multiplyWithVect3D(triPoints[0]);
            Vector3D p2rotZ = rotZ.multiplyWithVect3D(triPoints[1]);
            Vector3D p3rotZ = rotZ.multiplyWithVect3D(triPoints[2]);

            Vector3D p1rotZX = rotX.multiplyWithVect3D(p1rotZ);
            Vector3D p2rotZX = rotX.multiplyWithVect3D(p2rotZ);
            Vector3D p3rotZX = rotX.multiplyWithVect3D(p3rotZ);

            Vector3D p1trans = new Vector3D(p1rotZX);
            p1trans.translate(position);
            Vector3D p2trans = new Vector3D(p2rotZX);
            p2trans.translate(position);
            Vector3D p3trans = new Vector3D(p3rotZX);
            p3trans.translate(position);

            Vector3D p1proj = camera.projectVector(p1trans);
            Vector3D p2proj = camera.projectVector(p2trans);
            Vector3D p3proj = camera.projectVector(p3trans);

            p1proj.translate(new Vector3D(1f, 1f, 0));
            p2proj.translate(new Vector3D(1f, 1f, 0));
            p3proj.translate(new Vector3D(1f, 1f, 0));

            double centreX = 0.5f * camera.getScreenDimensions().x;
            double centreY = 0.5f * camera.getScreenDimensions().y;

            p1proj.scale(new Vector3D(centreX, centreY, 1));
            p2proj.scale(new Vector3D(centreX, centreY, 1));
            p3proj.scale(new Vector3D(centreX, centreY, 1));

            Triangle triProjected = new Triangle(p1proj, p2proj, p3proj);

            Drawer.drawTriangle(g2d, triProjected);
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
}
