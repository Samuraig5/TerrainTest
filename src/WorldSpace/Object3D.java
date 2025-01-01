package WorldSpace;

import Rendering.Camera;
import Rendering.Drawer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;

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

        Matrix4x4 worldTransform;
        worldTransform = Matrix4x4.matrixMatrixMultiplication(rotZ, rotX);
        worldTransform = Matrix4x4.matrixMatrixMultiplication(worldTransform, rotY);
        worldTransform = Matrix4x4.matrixMatrixMultiplication(worldTransform, trans);

        Vector3D up = new Vector3D(0,1,0);
        Vector3D cameraLookDirection = camera.getLookDirection();
        Vector3D target = cameraLookDirection.translation(camera.getPosition());

        Matrix4x4 cameraMatrix = Matrix4x4.getPointAtMatrix(camera.getPosition(), target, up);
        Matrix4x4 viewMatrix = cameraMatrix.quickMatrixInverse();

        for (Triangle tri : mesh)
        {
            Triangle triTransformed = worldTransform.multiplyWithTriangle(tri);

            //= Check if triangle normal is facing the camera =
            Vector3D triNormal = triTransformed.getNormal();

            //All three points lie on the same plane so we can choose any
            Vector3D cameraRay = new Vector3D(triTransformed.getPoints()[0]);
            cameraRay.translate(camera.getPosition().inverse());

            //If the camera can't see the triangle, don't draw it
            if (triNormal.dotProduct(cameraRay) >= 0) { continue; }

            //= Primitive Lighting (Replace later) =
            Vector3D lightDirection = new Vector3D(0f, 0f, -1f);
            lightDirection.normalize();
            double lightDotProduct = triNormal.dotProduct(lightDirection);
            Color shadedColour = Drawer.getColourShade(tri.getBaseColour(), lightDotProduct);
            tri.setShadedColour(shadedColour);

            // = Convert World Space -> View Space =
            Triangle triViewed = viewMatrix.multiplyWithTriangle(triTransformed);

            // = Clip Viewed Triangle =
            Vector3D planePosition = camera.getNearPlane();
            Vector3D planeNormal = new Vector3D(0,0,1);
            List<Triangle> clippedTriangles = clipTriangleAgainstPlane(planePosition, planeNormal, triViewed);

            for (Triangle triClipped : clippedTriangles)
            {
                //= Apply Projection (3D -> 2D) =
                Triangle triProj = camera.projectTriangle(triClipped);

                //= Move projection into view =
                triProj.translate(new Vector3D(1f, 1f, 0));

                //= Scale projection to screen =
                double centreX = 0.5f * camera.getScreenDimensions().x();
                double centreY = 0.5f * camera.getScreenDimensions().y();
                triProj.scale(new Vector3D(centreX, centreY, 1));

                //= Add triangle to list=
                triProj.setShadedColour(Drawer.getColourShade(triProj.getBaseColour(), lightDotProduct));
                trianglesToDraw.add(triProj);
            }
        }

        trianglesToDraw.sort(Comparator.comparingDouble(Triangle::getMidPoint).reversed());

        for (Triangle triangle : trianglesToDraw) {
            List<Triangle> triangleQueue = new ArrayList<>();
            triangleQueue.add(triangle);
            int numNewTriangles = 1;

            for (int p = 0; p < 4; p++) {
                List<Triangle> triToAdd = new ArrayList<>();
                while (numNewTriangles > 0)
                {
                    Triangle curr = triangleQueue.remove(0);
                    numNewTriangles--;

                    switch (p) {
                        case 0 -> triToAdd = clipTriangleAgainstPlane(
                                new Vector3D(0, 0, 0),
                                new Vector3D(0, 1, 0),
                                curr);
                        case 1 -> triToAdd = clipTriangleAgainstPlane(
                                new Vector3D(0, camera.getScreenDimensions().y() - 1, 0),
                                new Vector3D(0, -1, 0),
                                curr);
                        case 2 -> triToAdd = clipTriangleAgainstPlane(
                                new Vector3D(0, 0, 0),
                                new Vector3D(1, 0, 0),
                                curr);
                        case 3 -> triToAdd = clipTriangleAgainstPlane(
                                new Vector3D(camera.getScreenDimensions().x() - 1, 0, 0),
                                new Vector3D(-1, 0, 0),
                                curr);
                    }
                    triangleQueue.addAll(triToAdd);
                }
                numNewTriangles = triangleQueue.size();
            }
            for (Triangle triToDraw : triangleQueue) {
                camera.drawer.fillTriangle(g2d, triToDraw);
                if (showWireFrame) { camera.drawer.drawTriangle(g2d, Color.black, triToDraw); }
            }
        }
    }

    private List<Triangle> clipTriangleAgainstPlane(Vector3D planePosition, Vector3D planeNormal, Triangle in)
    {
        planeNormal.normalize();

        Function<Vector3D, Double> dist = (Vector3D p) -> {
            Vector3D n = p.normalized();
            return (planeNormal.x() * p.x() + planeNormal.y() * p.y() + planeNormal.z() * p.z()
                    - planeNormal.dotProduct(planePosition));
        };

        Vector3D[] inPoints = new Vector3D[3]; int numInPoints = 0;
        Vector3D[] outPoints = new Vector3D[3]; int numOutPoints = 0;

        Vector3D[] points = in.getPoints();

        for (int i = 0; i < 3; i++) {
            double distance = dist.apply(points[i]);
            if (distance >= 0) { inPoints[numInPoints++] = points[i]; }
            else {outPoints[numOutPoints++] = points[i];}
        }

        List<Triangle> out = new ArrayList<>();
        if (numInPoints == 0) {return out;} // The triangle was fully outside the clipping area
        if (numInPoints == 3) {out.add(in); return out;} // The triangle was fully inside the clipping area
        if (numInPoints == 1) // Only one point of the triangle was inside the clipping area
        {
            Vector3D p0 = inPoints[0];

            Line l1 = new Line(inPoints[0], outPoints[0]);
            Vector3D p1 = l1.getIntersectToPlane(planePosition, planeNormal);
            Line l2 = new Line(inPoints[0], outPoints[1]);
            Vector3D p2 = l2.getIntersectToPlane(planePosition, planeNormal);

            Triangle newTriangle = new Triangle(p0, p1, p2);
            newTriangle.copyColour(in);
            out.add(newTriangle);

            return out;
        }
        if (numInPoints == 2) // Two point of the triangle was inside the clipping area
        {
            Vector3D p0 = inPoints[0];
            Vector3D p1 = inPoints[1];

            Line l2 = new Line(inPoints[0], outPoints[0]);
            Vector3D p2 = l2.getIntersectToPlane(planePosition, planeNormal);

            Line l3 = new Line(inPoints[1], outPoints[0]);
            Vector3D p3 = l3.getIntersectToPlane(planePosition, planeNormal);

            Triangle tri1 = new Triangle(p0, p1, p2);
            tri1.copyColour(in);
            out.add(tri1);

            Triangle tri2 = new Triangle(p1, p2, p3);
            tri2.copyColour(in);
            out.add(tri2);

            return out;
        }
        return out;
    }

    public void showWireFrame(boolean showWireFrame) {
        this.showWireFrame = showWireFrame;
    }
}
