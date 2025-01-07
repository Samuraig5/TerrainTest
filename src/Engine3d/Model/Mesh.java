package Engine3d.Model;

import Engine3d.Lighting.LightSource;
import Engine3d.Math.*;
import Engine3d.Math.Vector.Vector2D;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Physics.AABBCollisions.AABB;
import Engine3d.Physics.Object3D;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.Material;
import Engine3d.Rotatable;
import Engine3d.Time.TimeMeasurer;
import Engine3d.Translatable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Mesh implements Translatable, Rotatable
{
    private Object3D object3D;
    protected Vector3D[] points;
    protected List<MeshTriangle> faces = new ArrayList<>();
    protected Vector3D meshOffrot = new Vector3D(0,0,0,1);
    protected Vector3D meshOffset = new Vector3D(0,0,0,1);
    protected boolean showWireFrame = false;
    public Mesh(Object3D object3D) {
        this.object3D = object3D;
        object3D.setMesh(this);
    }
    @Override
    public void translate(Vector3D delta) {
        meshOffset.translate(delta);
    }
    @Override
    public void rotate(Vector3D delta) {
        meshOffrot.translate(delta);
    }
    @Override
    public Vector3D getRotation() {
        return meshOffrot.translated(object3D.getRotation());
    }
    @Override
    public Vector3D getDirection() {
        return Matrix4x4.get3dRotationMatrix(getRotation()).matrixVectorMultiplication(Vector3D.FORWARD());
    }

    public Vector3D getPosition() {return meshOffset.translated(object3D.getPosition());}

    public void drawObject(Camera camera, Vector3D cameraPos, Matrix4x4 viewMatrix, List<LightSource> lightSources, TimeMeasurer tm)
    {
        tm.startMeasurement("Get Matrices");
        List<MeshTriangle> trianglesToRaster = new ArrayList<>();

        Matrix4x4 trans = Matrix4x4.getTranslationMatrix(getPosition());
        Matrix4x4 worldTransform = Matrix4x4.matrixMatrixMultiplication(Matrix4x4.get3dRotationMatrix(getRotation()), trans);
        tm.pauseAndEndMeasurement("Get Matrices");

        for (MeshTriangle tri : faces)
        {
            tm.startMeasurement("ObjWorldToScreen");
            MeshTriangle triTransformed = worldTransform.multiplyWithTriangle(tri);
            triTransformed.setMaterial(tri);

            //= Check if triangle normal is facing the camera =
            Vector3D triNormal = triTransformed.getNormal();

            //All three points lie on the same plane, so we can choose any
            Vector3D cameraRay = new Vector3D(triTransformed.getPoints()[0]);
            cameraRay.translate(cameraPos.inverted());

            //If the camera can't see the triangle, don't draw it
            if (triNormal.dotProduct(cameraRay) >= 0)
            {
                tm.pauseMeasurement("ObjWorldToScreen");
                continue;
            }

            for (LightSource ls : lightSources) {
                double lightIntensity = ls.getLightIntensity(triTransformed.getMidPoint().distanceTo(ls.getPosition()));
                if (lightIntensity == 0) {continue;}

                Vector3D lightDirection = ls.getDirection().normalized().inverted();

                double lightDotProduct = (triNormal.dotProduct(lightDirection)*lightIntensity);
                if (triTransformed.getMaterial().getLuminance() < lightDotProduct) {
                    triTransformed.getMaterial().setLuminance(lightDotProduct);
                }
            }

            //if (triTransformed.getMaterial().getLuminance() == 0) {continue;}

            // = Convert World Space -> View Space =
            MeshTriangle triViewed = viewMatrix.multiplyWithTriangle(triTransformed);
            triViewed.setMaterial(triTransformed);
            tm.pauseMeasurement("ObjWorldToScreen");

            tm.startMeasurement("TriangleClipping");
            // = Clip Viewed Triangle =
            Vector3D planePosition = camera.getNearPlane();
            Vector3D planeNormal = new Vector3D(0,0,1);
            List<MeshTriangle> clippedTriangles = clipTriangleAgainstPlane(planePosition, planeNormal, triViewed);
            tm.pauseMeasurement("TriangleClipping");

            tm.startMeasurement("ObjWorldToScreen");
            for (MeshTriangle triClipped : clippedTriangles)
            {
                //= Apply Projection (3D -> 2D) =
                MeshTriangle triProj = camera.projectTriangle(triClipped);

                Vector3D[] points = triProj.getPoints();
                Vector2D[] texPoints = triClipped.getMaterial().getTextureCoords();
                double u1 = texPoints[0].u() / points[0].w();
                double u2 = texPoints[1].u() / points[1].w();
                double u3 = texPoints[2].u() / points[2].w();
                double v1 = texPoints[0].v() / points[0].w();
                double v2 = texPoints[1].v() / points[1].w();
                double v3 = texPoints[2].v() / points[2].w();
                double w1 = 1f / points[0].w();
                double w2 = 1f / points[1].w();
                double w3 = 1f / points[2].w();
                Vector2D tex1 = new Vector2D(u1, v1, w1);
                Vector2D tex2 = new Vector2D(u2, v2, w2);
                Vector2D tex3 = new Vector2D(u3, v3, w3);

                Material newMat = new Material(triClipped.getMaterial());
                newMat.setTextureCoords(tex1, tex2, tex3);
                triProj.setMaterial(newMat);

                triProj.dividePointsByW();

                //= Move projection into view =
                triProj.translate(new Vector3D(1f, 1f, 0));

                //= Scale projection to screen =
                double centreX = 0.5f * camera.getResolution().x();
                double centreY = 0.5f * camera.getResolution().y();
                triProj.scale(new Vector3D(centreX, centreY, 1));

                //= Add triangle to list=
                trianglesToRaster.add(triProj);
            }
            tm.pauseMeasurement("ObjWorldToScreen");
        }

        //Sorting no longer needed due to depth buffer
        //trianglesToRaster.sort(Comparator.comparingDouble(Triangle::getMidPoint).reversed());

        for (MeshTriangle triangle : trianglesToRaster) {
            List<MeshTriangle> triangleQueue = new ArrayList<>();
            triangleQueue.add(triangle);
            int numNewTriangles = 1;

            tm.startMeasurement("TriangleClipping");
            for (int p = 0; p < 4; p++) {
                List<MeshTriangle> triToAdd = new ArrayList<>();
                while (numNewTriangles > 0)
                {
                    MeshTriangle curr = triangleQueue.remove(0);
                    numNewTriangles--;

                    switch (p) {
                        case 0 -> triToAdd = clipTriangleAgainstPlane(
                                new Vector3D(0, 0, 0),
                                new Vector3D(0, 1, 0),
                                curr);
                        case 1 -> triToAdd = clipTriangleAgainstPlane(
                                new Vector3D(0, camera.getResolution().y() - 1, 0),
                                new Vector3D(0, -1, 0),
                                curr);
                        case 2 -> triToAdd = clipTriangleAgainstPlane(
                                new Vector3D(0, 0, 0),
                                new Vector3D(1, 0, 0),
                                curr);
                        case 3 -> triToAdd = clipTriangleAgainstPlane(
                                new Vector3D(camera.getResolution().x() - 1, 0, 0),
                                new Vector3D(-1, 0, 0),
                                curr);
                    }
                    triangleQueue.addAll(triToAdd);
                }
                numNewTriangles = triangleQueue.size();
            }
            tm.pauseMeasurement("TriangleClipping");

            for (MeshTriangle triToDraw : triangleQueue) {
                if (showWireFrame) { camera.drawer.drawDebugTriangle(Color.white, triToDraw); }
                if (!(triToDraw.getMaterial().getTexture() == null)) {
                    tm.startMeasurement("Texturizer");
                    camera.drawer.textureTriangle(triToDraw);
                    tm.pauseMeasurement("Texturizer");
                }
                else if (triToDraw.getMaterial().getBaseColour().getAlpha() > 0) {
                    camera.drawer.fillTriangle(triToDraw);
                }
            }
        }
        tm.endMeasurement("ObjWorldToScreen");
        tm.endMeasurement("TriangleClipping");
        tm.endMeasurement("Texturizer");
    }

    private List<MeshTriangle> clipTriangleAgainstPlane(Vector3D planePosition, Vector3D planeNormal, MeshTriangle in)
    {
        planeNormal.normalize();

        Function<Vector3D, Double> dist = (Vector3D p) -> {
            //Vector3D n = p.normalized();
            return (planeNormal.x() * p.x() + planeNormal.y() * p.y() + planeNormal.z() * p.z()
                    - planeNormal.dotProduct(planePosition));
        };

        Vector3D[] inPoints = new Vector3D[3]; int numInPoints = 0;
        Vector3D[] outPoints = new Vector3D[3]; int numOutPoints = 0;
        Vector2D[] texInPoints = new Vector2D[3]; int numTexInPoints = 0;
        Vector2D[] texOutPoints = new Vector2D[3]; int numTexOutPoints = 0;


        Vector3D[] points = in.getPoints();
        Vector2D[] texPoints = in.getMaterial().getTextureCoords();

        for (int i = 0; i < 3; i++) {
            double distance = dist.apply(points[i]);
            if (distance >= 0)
            {
                inPoints[numInPoints++] = points[i];
                texInPoints[numTexInPoints++] = texPoints[i];
            }
            else
            {
                outPoints[numOutPoints++] = points[i];
                texOutPoints[numTexOutPoints++] = texPoints[i];
            }
        }

        List<MeshTriangle> out = new ArrayList<>();
        if (numInPoints == 0) {return out;} // The triangle was fully outside the clipping area
        if (numInPoints == 3) {out.add(in); return out;} // The triangle was fully inside the clipping area
        if (numInPoints == 1) // Only one point of the triangle was inside the clipping area
        {
            Vector3D p0 = inPoints[0];
            Vector2D t0 = texInPoints[0];

            Line l1 = new Line(inPoints[0], outPoints[0]);
            Vector3D p1 = l1.getIntersectToPlane(planePosition, planeNormal);

            double u1 = l1.getDistanceToLastIntersect() * (texOutPoints[0].u() - texInPoints[0].u()) + texInPoints[0].u();
            double v1 = l1.getDistanceToLastIntersect() * (texOutPoints[0].v() - texInPoints[0].v()) + texInPoints[0].v();
            double w1 = l1.getDistanceToLastIntersect() * (texOutPoints[0].w() - texInPoints[0].w()) + texInPoints[0].w();
            Vector2D t1 = new Vector2D(u1, v1, w1);

            Line l2 = new Line(inPoints[0], outPoints[1]);
            Vector3D p2 = l2.getIntersectToPlane(planePosition, planeNormal);

            double u2 = l2.getDistanceToLastIntersect() * (texOutPoints[1].u() - texInPoints[0].u()) + texInPoints[0].u();
            double v2 = l2.getDistanceToLastIntersect() * (texOutPoints[1].v() - texInPoints[0].v()) + texInPoints[0].v();
            double w2 = l2.getDistanceToLastIntersect() * (texOutPoints[1].w() - texInPoints[0].w()) + texInPoints[0].w();
            Vector2D t2 = new Vector2D(u2, v2, w2);

            Material newMaterial = new Material(in.getMaterial());
            newMaterial.setTextureCoords(t0, t1, t2);

            MeshTriangle newTriangle = new MeshTriangle(p0, p1, p2);
            newTriangle.setMaterial(newMaterial);
            out.add(newTriangle);

            return out;
        }
        if (numInPoints == 2) // Two point of the triangle was inside the clipping area
        {
            Vector3D p0 = inPoints[0];
            Vector2D t0 = texInPoints[0];
            Vector3D p1 = inPoints[1];
            Vector2D t1 = texInPoints[1];

            Line l2 = new Line(inPoints[0], outPoints[0]);
            Vector3D p2 = l2.getIntersectToPlane(planePosition, planeNormal);

            double u2 = l2.getDistanceToLastIntersect() * (texOutPoints[0].u() - texInPoints[0].u()) + texInPoints[0].u();
            double v2 = l2.getDistanceToLastIntersect() * (texOutPoints[0].v() - texInPoints[0].v()) + texInPoints[0].v();
            double w2 = l2.getDistanceToLastIntersect() * (texOutPoints[0].w() - texInPoints[0].w()) + texInPoints[0].w();
            Vector2D t2 = new Vector2D(u2, v2, w2);

            Line l3 = new Line(inPoints[1], outPoints[0]);
            Vector3D p3 = l3.getIntersectToPlane(planePosition, planeNormal);

            double u3 = l3.getDistanceToLastIntersect() * (texOutPoints[0].u() - texInPoints[1].u()) + texInPoints[1].u();
            double v3 = l3.getDistanceToLastIntersect() * (texOutPoints[0].v() - texInPoints[1].v()) + texInPoints[1].v();
            double w3 = l3.getDistanceToLastIntersect() * (texOutPoints[0].w() - texInPoints[1].w()) + texInPoints[1].w();
            Vector2D t3 = new Vector2D(u3, v3, w3);

            Material mat1 = new Material(in.getMaterial());
            mat1.setTextureCoords(t0, t1, t2);

            MeshTriangle tri1 = new MeshTriangle(p0, p1, p2);
            tri1.setMaterial(mat1);
            out.add(tri1);

            Material mat2 = new Material(in.getMaterial());
            mat2.setTextureCoords(t1, t2, t3);

            MeshTriangle tri2 = new MeshTriangle(p1, p2, p3);
            tri2.setMaterial(mat2);
            out.add(tri2);

            return out;
        }
        return out;
    }

    public AABB getAABB() {
        if (points == null || points.length == 0) {
            throw new IllegalStateException("Mesh contains no points");
        }

        // Initialize min and max with the coordinates of the first point
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = 0;
        double maxY = 0;
        double maxZ = 0;

        // Iterate through the points to find the min and max coordinates
        for (Vector3D point : points) {
            if (point.x() < minX) { minX = point.x(); }
            if (point.y() < minY) { minY = point.y(); }
            if (point.z() < minZ) { minZ = point.z(); }
            if (point.x() > maxX) { maxX = point.x(); }
            if (point.y() > maxY) { maxY = point.y(); }
            if (point.z() > maxZ) { maxZ = point.z(); }
        }

        // Create and return the AABB
        Vector3D min = new Vector3D(minX, minY, minZ).translated(getPosition());
        Vector3D max = new Vector3D(maxX, maxY, maxZ).translated(getPosition());
        return new AABB(min, max);
    }
    public void showWireFrame(boolean showWireFrame) {
        this.showWireFrame = showWireFrame;
    }
}