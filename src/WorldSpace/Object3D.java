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

        Matrix4x4 rotX = Matrix4x4.getRotationMatrixX(rotation.x());
        Matrix4x4 rotY = Matrix4x4.getRotationMatrixY(rotation.y());
        Matrix4x4 rotZ = Matrix4x4.getRotationMatrixZ(rotation.z());
        Matrix4x4 trans = Matrix4x4.getTranslationMatrix(position);

        Matrix4x4 worldTransform = Matrix4x4.getIdentityMatrix();
        worldTransform = Matrix4x4.matrixMatrixMultiplication(rotZ, rotX);
        worldTransform = Matrix4x4.matrixMatrixMultiplication(worldTransform, rotY);
        worldTransform = Matrix4x4.matrixMatrixMultiplication(worldTransform, trans);

        for (Triangle tri : mesh)
        {
            Triangle triTransformed = worldTransform.multiplyWithTriangle(tri);

            //= Check if triangle normal is facing the camera =
            Vector3D triNormal = triTransformed.getNormal();

            //All three points lie on the same plane so we can choose any
            Vector3D vectorFromCameraToTriangle =  new Vector3D(triTransformed.getPoints()[0]);
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
            Triangle triProj = camera.projectTriangle(triTransformed);

            //= Move projection into view =
            triProj.translate(new Vector3D(1f, 1f, 0));

            //= Scale projection to screen =
            double centreX = 0.5f * camera.getScreenDimensions().x();
            double centreY = 0.5f * camera.getScreenDimensions().y();
            triProj.scale(new Vector3D(centreX, centreY, 1));

            //= Add triangle to list=
            triProj.setShadedColour(shadedColour);

            trianglesToDraw.add(triProj);
        }

        trianglesToDraw.sort(Comparator.comparingDouble(Triangle::getMidPoint).reversed());

        for (Triangle triangle : trianglesToDraw) {
            Drawer.fillTriangle(g2d, triangle);
            if (showWireFrame) { Drawer.drawTriangle(g2d, Color.black, triangle); }
        }
    }

    public void showWireFrame(boolean showWireFrame) {
        this.showWireFrame = showWireFrame;
    }
}
