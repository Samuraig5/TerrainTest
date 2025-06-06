package Physics.GJK_EPA;

import Engine3d.Model.Mesh;
import Math.Vector.Vector3D;
import Math.Line;
import Engine3d.Object3D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static Physics.GJK_EPA.GJK.solveGJK;

public class EPA
{
    private static final double THRESHOLD = 0.001;

    public static Vector3D solveEPA(Object3D o1, Object3D o2) {
        return solveEPA(o1.getMesh(), o2.getMesh());
    }

    public static Vector3D solveEPA(Mesh mesh1, Mesh mesh2) {
        Simplex solGJK = solveGJK(mesh1, mesh2);
        if (solGJK == null) { return new Vector3D(); }

        List<Vector3D> ver1 = mesh1.getPointsInWorld();
        List<Vector3D> ver2 = mesh2.getPointsInWorld();

        Polytope polytope = new Polytope(solGJK);

        double minDistance = Double.POSITIVE_INFINITY;
        Vector3D minNormal = new Vector3D();

        int stopper = 0;
        while (minDistance == Double.POSITIVE_INFINITY) {
            Face minFace = getMinFace(polytope);
            minDistance = minFace.normal().dotProduct(minFace.a());
            minNormal = minFace.normal();

            Vector3D support = GJK.calculateNewVertex(ver1,ver2,minNormal);
            double supportDistance = minNormal.dotProduct(support);

            if (Math.abs(supportDistance - minDistance) > THRESHOLD) {
                minDistance = Double.POSITIVE_INFINITY;

                List<Line> uniqueEdges = new ArrayList<>();

                //Remove all faces pointing in the direction of the support point
                Iterator<Face> iterator = polytope.faces.iterator();
                while (iterator.hasNext()) {
                    Face face = iterator.next();
                    if (face.normal().sameDirection(support.translated(face.a().inverted()))) {
                        addIfUniqueEdge(uniqueEdges, face.a(), face.b());
                        addIfUniqueEdge(uniqueEdges, face.b(), face.c());
                        addIfUniqueEdge(uniqueEdges, face.c(), face.a());

                        // Remove face efficiently
                        iterator.remove();
                    }
                }

                //Adding new faces
                List<Face> newFaces = new ArrayList<>();
                for (int i = 0; i < uniqueEdges.size(); i++) {
                    Line edge = uniqueEdges.get(i);
                    Face newFace = new Face(edge.p1(), edge.p2(), support);
                    newFaces.add(newFace);
                }

                polytope.vertices.add(support);
                polytope.faces.addAll(newFaces);
            }
            if (stopper == 100) {
                return Vector3D.UP().scaled(0.01f);
            }
            else {
                stopper++;
            }
        }

        return minNormal.scaled(minDistance + THRESHOLD);
    }

    private static Face getMinFace(Polytope polytope) {
        double minDistance = Double.POSITIVE_INFINITY;
        Face minFace = null;

        for (int i = 0; i < polytope.faces.size(); i++) {
            Face face = polytope.faces.get(i);
            double distance = face.normal().dotProduct(face.a());

            if (distance < 0) {
                distance *= -1;
                face.normal().invert();
            }

            if (distance < minDistance) {
                minDistance = distance;
                minFace = face;
            }
        }
        return minFace;
    }

    /**
     * Given a list of edges and a new edge (implied by two points a-b), it adds the new edge only if it is unique (meaning
     * its reverse b-a is not in the list). If the edge is not unique, it isn't added to the list and the reverse is removed
     * from the list.
     * @param edges List of edges
     * @param a Starting point for the new edge
     * @param b Ending point for the new edge
     */
    private static void addIfUniqueEdge(
            List<Line> edges,
            Vector3D a,
            Vector3D b) {

        for (int i = 0; i < edges.size(); i++) {
            Line edge = edges.get(i);
            if (edge.p1() == b && edge.p2() == a) {
                edges.remove(edge);
                return;
            }
        }

        edges.add(new Line(a, b));
    }
}
