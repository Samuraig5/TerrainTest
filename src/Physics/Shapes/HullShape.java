package Physics.Shapes;

import Engine3d.Model.Mesh;
import Engine3d.Objects.Object3D;
import Math.Vector.Vector3D;
import Physics.AABBCollisions.AABB;
import Math.Matrix4x4;

import java.util.ArrayList;
import java.util.List;

public class HullShape implements ConvexShape {
    private final Object3D owner;
    private final Mesh mesh;

    private Vector3D lastPos, lastRot, lastScale, lastMeshOff, lastMeshRot;
    private List<Vector3D> lastPoints;

    private final List<Vector3D> world = new ArrayList<>();
    private AABB bounds;

    public HullShape(Object3D owner, Mesh mesh) {
        if (mesh.getPoints().isEmpty()) {
            throw new IllegalStateException();
        }

        this.owner = owner;
        this.mesh = mesh;
    }

    private void refreshIfMoved() {
        List<Vector3D> pts = mesh.getPoints();  // read the reference once
        if (owner.getPosition() == lastPos && owner.getRotation() == lastRot
                && owner.getScale() == lastScale && pts == lastPoints
                && mesh.getPosition() == lastMeshOff && mesh.getRotation() == lastMeshRot) {
            return;
        }
        world.clear();

        lastPos = owner.getPosition();
        lastRot = owner.getRotation();
        lastScale = owner.getScale();
        lastPoints = pts;
        lastMeshOff = mesh.getPosition();
        lastMeshRot = mesh.getRotation();

        Matrix4x4 worldTransform =
                Matrix4x4.matrixMatrixMultiplication(
                        Matrix4x4.getScalingMatrix(lastScale),
                        Matrix4x4.matrixMatrixMultiplication(
                                Matrix4x4.get3dRotationMatrix(lastMeshRot.translated(lastRot)),
                                Matrix4x4.getTranslationMatrix(lastMeshOff.translated(lastPos)
                        )));


        double[] mins_max =
                {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
                        Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};
        for (int i = 0; i < lastPoints.size(); i++) {
            Vector3D res = worldTransform.matrixVectorMultiplication(lastPoints.get(i));
            world.add(res);

            if (res.x() < mins_max[0]) { mins_max[0] = res.x(); }
            if (res.x() > mins_max[3]) { mins_max[3] = res.x(); }

            if (res.y() < mins_max[1]) { mins_max[1] = res.y(); }
            if (res.y() > mins_max[4]) { mins_max[4] = res.y(); }

            if (res.z() < mins_max[2]) { mins_max[2] = res.z(); }
            if (res.z() > mins_max[5]) { mins_max[5] = res.z(); }
        }

        bounds = new AABB(new Vector3D(mins_max[0],mins_max[1],mins_max[2]), new Vector3D(mins_max[3],mins_max[4],mins_max[5]));
    }

    public Vector3D support(Vector3D d) {
        refreshIfMoved();

        Vector3D largestVertex = world.get(0);
        double largestDot = largestVertex.dotProduct(d);
        for (int i = 1; i < world.size(); i++) {
            Vector3D currentVertex = world.get(i);
            double currentDot = currentVertex.dotProduct(d);

            if (currentDot > largestDot) {
                largestDot = currentDot;
                largestVertex = currentVertex;
            }
        }
        return largestVertex;
    }

    public AABB bounds() {
        refreshIfMoved();
        return bounds;
    }
    public Vector3D center() {
        refreshIfMoved();

        return bounds.getCenterPoint();
    }
}
