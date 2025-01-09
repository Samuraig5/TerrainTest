package Engine3d.Model;

import Engine3d.Lighting.LightSource;
import Math.*;
import Math.Vector.Vector2D;
import Math.Vector.Vector3D;
import Engine3d.Rendering.DrawInstructions;
import Engine3d.Scalable;
import Physics.AABBCollisions.AABB;
import Physics.Object3D;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.Material;
import Engine3d.Rotatable;
import Engine3d.Time.TimeMeasurer;
import Engine3d.Translatable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class Mesh implements Translatable, Rotatable, Scalable
{
    private Object3D object3D;
    protected List<Vector3D> points = new ArrayList<>();
    protected List<MeshTriangle> faces = new CopyOnWriteArrayList<>();
    protected Vector3D meshOffrot = new Vector3D(0,0,0,1);
    protected Vector3D meshOffset = new Vector3D(0,0,0,1);
    private TimeMeasurer tm = new TimeMeasurer();
    private record copyPnF(List<Vector3D> copiedPoints, List<MeshTriangle> copiedFaces) { }
    private DrawInstructions drawInstructions;
    public Mesh(Object3D object3D) {
        this.object3D = object3D;
        drawInstructions = new DrawInstructions(false,false,true,true);
        //object3D.setMesh(this);
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
    @Override
    public Vector3D getDirection(Vector3D base) {
        return Matrix4x4.get3dRotationMatrix(getRotation()).matrixVectorMultiplication(base);
    }

    @Override
    public void scale(Vector3D delta) {
        for (int i = 0; i < points.size(); i++) {
            points.get(i).scale(delta);
        }
    }
    public Vector3D getPosition() {return meshOffset.translated(object3D.getPosition());}
    public DrawInstructions getDrawInstructions() {
        return drawInstructions;
    }
    public void setDrawInstructions(DrawInstructions drawInstructions) {
        this.drawInstructions = drawInstructions;
    }

    public void drawMesh(Camera camera, Vector3D cameraPos, Matrix4x4 viewMatrix, List<LightSource> lightSources, TimeMeasurer tm)
    {
        this.tm = tm;

        try {
            copyPnF result = getCopyPnF();

            localToWorld(result.copiedPoints());

            if (drawInstructions.doShading) {
                calculateLuminance(cameraPos, lightSources, result.copiedFaces());
            }

            tm.startMeasurement("ObjWorldToScreen");
            viewMatrix.matrixVectorManipulation(result.copiedPoints());
            tm.pauseMeasurement("ObjWorldToScreen");

            List<MeshTriangle> trianglesToRaster = clipAgainstNearPlane(camera, result.copiedFaces());

            trianglesToRaster = projectTriangles(camera, trianglesToRaster);

            trianglesToRaster = clipAgainstFrustum(camera, trianglesToRaster);

            drawTriangles(camera, trianglesToRaster);

        } catch (NullPointerException e) {
            System.err.println("Mesh has a null point and is not drawable");
            //If a point is null, then the mesh is not drawable
        }
    }

    /**
     * Draws the triangles to the screen.
     * @param camera the camera to which the triangle is drawn.
     * @param triangles the list of triangles to be drawn.
     */
    private void drawTriangles(Camera camera, List<MeshTriangle> triangles) {
        for (MeshTriangle triToDraw : triangles)
        {
            if (drawInstructions.drawWireFrame) {
                camera.drawer.drawTriangle(drawInstructions.wireFrameColour, triToDraw, drawInstructions.ignorePixelDepth);
            }
            if (drawInstructions.drawFlatColour) {
                if (triToDraw.getMaterial().getBaseColour().getAlpha() > 0) {
                    camera.drawer.fillTriangle(triToDraw);
                }
            }
            else if (drawInstructions.drawTexture) {
                if ((triToDraw.getMaterial().getTexture() != null)) {
                    tm.startMeasurement("Texturizer");
                    camera.drawer.textureTriangle(triToDraw);
                    tm.pauseMeasurement("Texturizer");
                }
            }
        }
    }

    /**
     * Clips the triangles against the near plane of the camera.
     * @param camera the camera against which the triangles are to be clipped.
     * @param triangles the triangles to be clipped.
     * @return the clipped triangles.
     */
    private List<MeshTriangle> clipAgainstNearPlane(Camera camera, List<MeshTriangle> triangles) {
        List<MeshTriangle> clippedTriangles = new ArrayList<>();
        for (MeshTriangle tri : triangles) {
            Vector3D planePosition = camera.getNearPlane();
            Vector3D planeNormal = new Vector3D(0,0,1);
            List<MeshTriangle> newTrigs = clipTriangleAgainstPlane(planePosition, planeNormal, tri, tm);

            clippedTriangles.addAll(newTrigs);
        }
        return clippedTriangles;
    }

    /**
     * Generates copies of the points and faces. The copied faces reference the copied points.
     * @return a copyPnF result. The points can be accessed with result.copiedPoints() and the faces with result.copiedFaces()
     */
    private copyPnF getCopyPnF() {
        Map<Vector3D, Vector3D> pointMap = new HashMap<>();
        List<Vector3D> copiedPoints = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            copiedPoints.add(new Vector3D(points.get(i)));
            pointMap.put(points.get(i), copiedPoints.get(i));
        }

        List<MeshTriangle> copiedFaces = generateCopyFaces(pointMap, faces);
        copyPnF result = new copyPnF(copiedPoints, copiedFaces);
        return result;
    }

    /**
     * Translates the points from local space to world space.
     * @param copiedPoints the points to be translated.
     */
    private void localToWorld(List<Vector3D> copiedPoints) {
        tm.startMeasurement("localToWorld");
        Matrix4x4 trans = Matrix4x4.getTranslationMatrix(getPosition());
        Matrix4x4 worldTransform = Matrix4x4.matrixMatrixMultiplication(Matrix4x4.get3dRotationMatrix(getRotation()), trans);

        worldTransform.matrixVectorManipulation(copiedPoints);
        tm.pauseMeasurement("localToWorld");
    }

    /**
     * Clips the triangles against the sides of the camera's frustum.
     * @param camera The camera to which the triangles should be clipped.
     * @param triangles The triangles to be clipped.
     * @return The list of clipped triangles.
     */
    private List<MeshTriangle> clipAgainstFrustum(Camera camera, List<MeshTriangle> triangles) {
        List<MeshTriangle> clippedTriangles = new ArrayList<>();
        for (MeshTriangle triangle : triangles) {
            List<MeshTriangle> triangleQueue = new ArrayList<>();
            triangleQueue.add(triangle);
            int numNewTriangles = 1;

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
                                curr, tm);
                        case 1 -> triToAdd = clipTriangleAgainstPlane(
                                new Vector3D(0, camera.getResolution().y() - 1, 0),
                                new Vector3D(0, -1, 0),
                                curr, tm);
                        case 2 -> triToAdd = clipTriangleAgainstPlane(
                                new Vector3D(0, 0, 0),
                                new Vector3D(1, 0, 0),
                                curr, tm);
                        case 3 -> triToAdd = clipTriangleAgainstPlane(
                                new Vector3D(camera.getResolution().x() - 1, 0, 0),
                                new Vector3D(-1, 0, 0),
                                curr, tm);
                    }
                    triangleQueue.addAll(triToAdd);
                }
                numNewTriangles = triangleQueue.size();
            }
            clippedTriangles.addAll(triangleQueue);
        }
        return clippedTriangles;
    }

    /**
     * Calculates the projection of the triangles.
     * @param camera camera to which the triangles are projected.
     * @param triangles triangles to be projected.
     * @return projection of the triangles.
     */
    private List<MeshTriangle> projectTriangles(Camera camera, List<MeshTriangle> triangles) {
        tm.startMeasurement("ObjWorldToScreen");
        List<MeshTriangle> projected = new ArrayList<>();
        for (MeshTriangle triClipped : triangles)
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
            projected.add(triProj);
        }
        tm.pauseMeasurement("ObjWorldToScreen");
        return projected;
    }

    /**
     * Calculates the luminance of each mesh triangle and writes the result to the triangle's material.
     * @param cameraPos position of the camera
     * @param lightSources list of lightSources
     * @param copiedFaces the faces that should be illuminated
     */
    private void calculateLuminance(Vector3D cameraPos, List<LightSource> lightSources, List<MeshTriangle> copiedFaces) {
        tm.startMeasurement("Lighting");
        for (MeshTriangle tri : copiedFaces) {
            tri.getMaterial().setLuminance(0);

            //= Check if triangle normal is facing the camera =
            Vector3D triNormal = tri.getNormal();
            //All three points lie on the same plane, so we can choose any
            Vector3D cameraRay = new Vector3D(tri.getPoints()[0]);
            cameraRay.translate(cameraPos.inverted());

            //If the camera can't see the triangle, don't draw it
            if (triNormal.dotProduct(cameraRay) >= 0) {
                continue;
            }

            for (LightSource ls : lightSources) {
                double lightIntensity = ls.getLightIntensity(tri.getMidPoint().distanceTo(ls.getPosition()));
                if (lightIntensity == 0) {continue;}

                Vector3D lightDirection = ls.getDirection().normalized().inverted();

                double lightDotProduct = (triNormal.dotProduct(lightDirection)*lightIntensity);
                if (tri.getMaterial().getLuminance() < lightDotProduct) {
                    tri.getMaterial().setLuminance(lightDotProduct);
                }
            }
        }
        tm.pauseMeasurement("Lighting");
    }

    private List<MeshTriangle> clipTriangleAgainstPlane(Vector3D planePosition, Vector3D planeNormal, MeshTriangle in, TimeMeasurer tm)
    {
        tm.startMeasurement("TriangleClipping");

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

            tm.pauseMeasurement("TriangleClipping");
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

            tm.pauseMeasurement("TriangleClipping");
            return out;
        }
        tm.pauseMeasurement("TriangleClipping");
        return out;
    }

    public void copy(Mesh source) {
        points = new ArrayList<>(source.points.size());

        Map<Vector3D, Vector3D> pointMap = new HashMap<>();

        for (int i = 0; i < source.points.size(); i++) {
            Vector3D newVec = new Vector3D(source.points.get(i));
            points.add(newVec);
            pointMap.put(source.points.get(i), newVec);
        }

        faces = generateCopyFaces(pointMap, source.faces);

        meshOffrot = new Vector3D(source.meshOffrot);
        meshOffset = new Vector3D(source.meshOffset);
        DrawInstructions diSoruce = source.drawInstructions;
        drawInstructions = new DrawInstructions(diSoruce.drawWireFrame, diSoruce.drawFlatColour, diSoruce.drawTexture, diSoruce.doShading);
    }

    /**
     * Since the faces of a mesh necessarily need to reference the points of the mesh, when the points of a mesh are changed,
     * the faces need to be updated to use the new points.
     * @param pointMap A HashMap that maps the old points to the new points.
     * @param originalFaces The original list of faces.
     * @return The list of new faces.
     */
    public List<MeshTriangle> generateCopyFaces(Map<Vector3D, Vector3D> pointMap, List<MeshTriangle> originalFaces) {
        List<MeshTriangle> copiedFaces = new ArrayList<>(originalFaces.size());

        for (MeshTriangle originalFace : originalFaces) {
            Vector3D[] newTrianglePoints = new Vector3D[3];
            Vector3D[] oldPoints = originalFace.getPoints();
            for (int j = 0; j < 3; j++) {
                newTrianglePoints[j] = pointMap.get(oldPoints[j]);
            }
            MeshTriangle newTri = new MeshTriangle(newTrianglePoints[0], newTrianglePoints[1], newTrianglePoints[2]);
            newTri.setMaterial(originalFace);
            copiedFaces.add(newTri);
        }
        return copiedFaces;
    }

    public AABB getAABB() {
        if (points == null || points.isEmpty()) {
            throw new IllegalStateException("Mesh contains no points");
        }

        // Initialize min and max with the coordinates of the first point
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;

        copyPnF result = getCopyPnF();
        localToWorld(result.copiedPoints());

        // Iterate through the points to find the min and max coordinates
        for (Vector3D point : result.copiedPoints) {
            if (point.x() < minX) { minX = point.x(); }
            if (point.y() < minY) { minY = point.y(); }
            if (point.z() < minZ) { minZ = point.z(); }

            if (point.x() > maxX) { maxX = point.x(); }
            if (point.y() > maxY) { maxY = point.y(); }
            if (point.z() > maxZ) { maxZ = point.z(); }
        }

        Vector3D min = new Vector3D(minX, minY, minZ);
        Vector3D max = new Vector3D(maxX, maxY, maxZ);

        return new AABB(min, max);
    }
}
