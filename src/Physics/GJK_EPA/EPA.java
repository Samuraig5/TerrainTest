package Physics.GJK_EPA;

import Math.Vector.Vector3D;
import Physics.Shapes.ConvexShape;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static Physics.GJK_EPA.GJK.solveGJK;
import static Physics.GJK_EPA.Polytope.outwardFace;

public class EPA
{
    private static final double THRESHOLD = 0.001;
    private record Edge(Vector3D p1, Vector3D p2) {}
    public static Vector3D solveEPA(ConvexShape a, ConvexShape b) {
        Simplex solGJK = solveGJK(a, b);

        if (solGJK == null) { return null; }

        Polytope polytope = new Polytope(solGJK);

        double minDistance = Double.POSITIVE_INFINITY;
        Vector3D minNormal = new Vector3D(0,0,0);

        int stopper = 0;
        while (minDistance == Double.POSITIVE_INFINITY) {
            Face minFace = getMinFace(polytope);
            if (minFace == null) { return null; }
            minDistance = minFace.normal().dotProduct(minFace.a());
            minNormal = minFace.normal();

            Vector3D support = GJK.minkowskiSupport(a,b,minNormal);
            double supportDistance = minNormal.dotProduct(support);

            if (Math.abs(supportDistance - minDistance) > THRESHOLD) {
                minDistance = Double.POSITIVE_INFINITY;

                List<Edge> uniqueEdges = new ArrayList<>();

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
                    Edge edge = uniqueEdges.get(i);
                    Face newFace = outwardFace(edge.p1(), edge.p2(), support);
                    newFaces.add(newFace);
                }

                polytope.vertices.add(support);
                polytope.faces.addAll(newFaces);

                if (polytope.faces.size() > 256) {
                    return minNormal.scaled(supportDistance + THRESHOLD);
                }
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
            List<Edge> edges,
            Vector3D a,
            Vector3D b) {

        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            if (edge.p1() == b && edge.p2() == a) {
                edges.remove(edge);
                return;
            }
        }

        edges.add(new Edge(a, b));
    }
}
