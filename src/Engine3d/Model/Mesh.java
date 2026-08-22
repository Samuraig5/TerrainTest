package Engine3d.Model;

import Engine3d.Lighting.LightSource;
import Engine3d.Rendering.Frustum;
import Math.*;
import Math.Vector.Vector2D;
import Math.Vector.Vector3D;
import Engine3d.Rendering.DrawInstructions;
import Engine3d.Scalable;
import Physics.AABBCollisions.AABB;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.Material;
import Engine3d.Rotatable;
import Engine3d.Translatable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class Mesh implements Translatable, Rotatable, Scalable
{
    protected List<Vector3D> points = new ArrayList<>();
    protected List<MeshTriangle> faces = new CopyOnWriteArrayList<>();
    protected Vector3D meshOffrot = new Vector3D(0,0,0,1);
    protected Vector3D meshOffset = new Vector3D(0,0,0,1);
    protected Vector3D localMin, localMax;
    private record copyPnF(List<Vector3D> copiedPoints, List<MeshTriangle> copiedFaces) { }
    private DrawInstructions drawInstructions;
    public Mesh() {
        drawInstructions = new DrawInstructions(false,false,true,true);
    }
    @Override
    public void translate(Vector3D delta) {
        meshOffset = meshOffset.translated(delta);
    }
    @Override
    public void rotate(Vector3D delta) {
        meshOffrot = meshOffrot.translated(delta);
    }
    @Override
    public Vector3D getRotation() {
        return meshOffrot;
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
        Map<Vector3D, Vector3D> pointMap = new HashMap<>();
        List<Vector3D> newPoints = new ArrayList<>(points.size());
        for (Vector3D p : points) {
            Vector3D scaled = p.scaled(delta);   // new vector, old one untouched
            pointMap.put(p, scaled);             // remember old -> new
            newPoints.add(scaled);
        }
        points = newPoints;                                              // replace the list
        faces = new CopyOnWriteArrayList<>(generateCopyFaces(pointMap, faces)); // rebuild faces onto new vectors
        recomputeLocalBounds();
    }
    public Vector3D getPosition() {return meshOffset;}
    public DrawInstructions getDrawInstructions() {
        return drawInstructions;
    }
    public void setDrawInstructions(DrawInstructions drawInstructions) {
        this.drawInstructions = drawInstructions;
    }
    public List<Vector3D> getPoints() { return points; }

    public List<Vector3D> getPointsInWorld(Vector3D worldPos, Vector3D worldRot) {
        return localToWorld(new ArrayList<>(points),
                getPosition().translated(worldPos),
                getRotation().translated(worldRot));
    }

    public void translatePoint(Vector3D targetPoint, Vector3D delta) {
        for (int i = 0; i < faces.size(); i++) {
            faces.get(i).translatePoint(targetPoint, delta);
        }
    }

    public record ProjectedTriangles(List<MeshTriangle> meshTriangles, DrawInstructions drawInstructions) { }
    public ProjectedTriangles computeGeometry(Vector3D position, Vector3D rotation, Camera camera, Vector3D cameraPos,
                                              Frustum frustum, Matrix4x4 viewMatrix, List<LightSource> lightSources) {
        try {
            Matrix4x4 worldTransform = Matrix4x4.matrixMatrixMultiplication(
                    Matrix4x4.get3dRotationMatrix(getRotation().translated(rotation)),
                    Matrix4x4.getTranslationMatrix(getPosition().translated(position))
            );

            // --- Frustum cull: skip the whole mesh if its world AABB is off-screen ---
            if (localMin != null) {
                Vector3D[] mm = new Vector3D[2];
                worldAABB(worldTransform, localMin, localMax, mm);
                if (!frustum.isVisible(mm[0], mm[1])) {
                    return new ProjectedTriangles(new ArrayList<>(), drawInstructions);
                }
            }

            copyPnF result = transform(worldTransform, points, faces);

            // --- Backface cull (world space): keep only triangles facing the camera ---
            List<MeshTriangle> visible = new ArrayList<>();
            for (MeshTriangle tri : result.copiedFaces()) {
                Vector3D n = tri.getNormal();
                Vector3D camRay = tri.getPoints()[0].translated(cameraPos.inverted());  // point - camera
                if (n.dotProduct(camRay) >= 0) { continue; } //skip back-face
                visible.add(tri);   // front-facing

                if (drawInstructions.doShading) {
                    shadeTriangle(tri, n, lightSources);
                }
            }

            result = transform(viewMatrix, result.copiedPoints, visible);

            List<MeshTriangle> trianglesToRaster = clipAgainstNearPlane(camera, result.copiedFaces());

            trianglesToRaster = projectTriangles(camera, trianglesToRaster);

            trianglesToRaster = clipAgainstFrustum(camera, trianglesToRaster);

            return new ProjectedTriangles(trianglesToRaster, drawInstructions);

            //drawTriangles(camera, trianglesToRaster);

        } catch (NullPointerException e) {
            //If a point is null, then the mesh is not drawable
            System.err.println("Mesh has a null point and is not drawable");
            //return an empty raster instruction
            return new ProjectedTriangles(new ArrayList<>(), drawInstructions);
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
            List<MeshTriangle> newTrigs = clipTriangleAgainstPlane(planePosition, planeNormal, tri);

            clippedTriangles.addAll(newTrigs);
        }
        return clippedTriangles;
    }

    /**
     * Translates the points from local space to world space.
     */
    private static List<Vector3D> localToWorld(List<Vector3D> points, Vector3D position, Vector3D rotation) {
        //tm.startMeasurement("localToWorld");
        Matrix4x4 trans = Matrix4x4.getTranslationMatrix(position);
        Matrix4x4 worldTransform = Matrix4x4.matrixMatrixMultiplication(Matrix4x4.get3dRotationMatrix(rotation), trans);

        return worldTransform.matrixVectorManipulation(points);
        //tm.pauseMeasurement("localToWorld");
    }

    /**
     * Translates a point from world space to local space.
     * @param worldPoint to be translated
     * @return the world point in local space
     */
    public Vector3D worldToLocal(Vector3D worldPoint, Vector3D worldPos, Vector3D worldRot) {
        Matrix4x4 trans = Matrix4x4.getTranslationMatrix(getPosition().translated(worldPos));
        Matrix4x4 worldTransform = Matrix4x4.matrixMatrixMultiplication(Matrix4x4.get3dRotationMatrix(getRotation().translated(worldRot)), trans);
        Matrix4x4 inverse = worldTransform.quickMatrixInverse();
        return inverse.matrixVectorMultiplication(worldPoint);
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
        return projected;
    }

    private void shadeTriangle(MeshTriangle tri, Vector3D normal, List<LightSource> lights) {
        tri.getMaterial().setLuminance(0);
        for (LightSource ls : lights) {
            double intensity = ls.getLightIntensity(tri.getMidPoint().distanceTo(ls.getPosition()));
            if (intensity == 0) continue;
            Vector3D lightDir = ls.getDirection().normalized().inverted();
            double dot = normal.dotProduct(lightDir) * intensity;
            if (tri.getMaterial().getLuminance() < dot) tri.getMaterial().setLuminance(dot);
        }
    }

    private List<MeshTriangle> clipTriangleAgainstPlane(Vector3D planePosition, Vector3D planeNormal, MeshTriangle in) {
        Vector3D[] inPoints = new Vector3D[3]; int numInPoints = 0;
        Vector3D[] outPoints = new Vector3D[3]; int numOutPoints = 0;
        Vector2D[] texInPoints = new Vector2D[3]; int numTexInPoints = 0;
        Vector2D[] texOutPoints = new Vector2D[3]; int numTexOutPoints = 0;


        Vector3D[] points = in.getPoints();
        Vector2D[] texPoints = in.getMaterial().getTextureCoords();

        for (int i = 0; i < 3; i++) {
            double distance = signedDistance(planeNormal, planePosition, points[i]);
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

    private static double signedDistance(Vector3D n, Vector3D planePos, Vector3D p) {
        return n.x()*p.x() + n.y()*p.y() + n.z()*p.z() - n.dotProduct(planePos);
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

    private copyPnF transform(Matrix4x4 transformMatrix, List<Vector3D> points, List<MeshTriangle> faces) {
        int n = points.size();
        Map<Vector3D, Vector3D> pointMap = new HashMap<>(n * 2);
        List<Vector3D> newPoints = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            Vector3D p = points.get(i);
            Vector3D tp = transformMatrix.matrixVectorManipulation(p);
            pointMap.put(p, tp);
            newPoints.add(tp);
        }
        return new copyPnF(newPoints, generateCopyFaces(pointMap, faces));
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

    public AABB getAABB(Vector3D worldPos, Vector3D worldRot) {
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

        // Iterate through the points to find the min and max coordinates
        for (Vector3D point : getPointsInWorld(worldPos, worldRot)) {
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

    public List<MeshTriangle> getFacesInWorld(Vector3D worldPos, Vector3D worldRot) {
        Matrix4x4 tf = Matrix4x4.matrixMatrixMultiplication(
                Matrix4x4.get3dRotationMatrix(getRotation().translated(worldRot)),
                Matrix4x4.getTranslationMatrix(getPosition().translated(worldPos)));
        List<MeshTriangle> out = new ArrayList<>(faces.size());
        for (MeshTriangle f : faces) {
            Vector3D[] p = f.getPoints();
            out.add(new MeshTriangle(
                    tf.matrixVectorManipulation(p[0]),
                    tf.matrixVectorManipulation(p[1]),
                    tf.matrixVectorManipulation(p[2])));
        }
        return out;
    }

    /** Scan the local-space points once and cache the bounds. */
    public void recomputeLocalBounds() {
        if (points == null || points.isEmpty()) { localMin = localMax = null; return; }
        double mnX=Double.MAX_VALUE, mnY=Double.MAX_VALUE, mnZ=Double.MAX_VALUE;
        double mxX=-Double.MAX_VALUE, mxY=-Double.MAX_VALUE, mxZ=-Double.MAX_VALUE;
        for (Vector3D p : points) {
            if (p.x()<mnX) mnX=p.x();  if (p.x()>mxX) mxX=p.x();
            if (p.y()<mnY) mnY=p.y();  if (p.y()>mxY) mxY=p.y();
            if (p.z()<mnZ) mnZ=p.z();  if (p.z()>mxZ) mxZ=p.z();
        }
        localMin = new Vector3D(mnX,mnY,mnZ);
        localMax = new Vector3D(mxX,mxY,mxZ);
    }

    private static void worldAABB(Matrix4x4 wt, Vector3D lo, Vector3D hi,
                                  Vector3D[] outMinMax) {
        double mnX=Double.MAX_VALUE, mnY=Double.MAX_VALUE, mnZ=Double.MAX_VALUE;
        double mxX=-Double.MAX_VALUE, mxY=-Double.MAX_VALUE, mxZ=-Double.MAX_VALUE;
        for (int i=0;i<8;i++) {
            Vector3D c = new Vector3D((i&1)==0?lo.x():hi.x(),
                    (i&2)==0?lo.y():hi.y(),
                    (i&4)==0?lo.z():hi.z());
            Vector3D w = wt.matrixVectorManipulation(c);   // local → world
            if (w.x()<mnX) mnX=w.x(); if (w.x()>mxX) mxX=w.x();
            if (w.y()<mnY) mnY=w.y(); if (w.y()>mxY) mxY=w.y();
            if (w.z()<mnZ) mnZ=w.z(); if (w.z()>mxZ) mxZ=w.z();
        }
        outMinMax[0] = new Vector3D(mnX,mnY,mnZ);
        outMinMax[1] = new Vector3D(mxX,mxY,mxZ);
    }
}
