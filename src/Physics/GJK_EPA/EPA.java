package Physics.GJK_EPA;

import Math.Vector.Vector3D;
import Physics.Object3D;

import java.util.List;

import static Physics.GJK_EPA.GJK.solveGJK;

public class EPA
{
    //https://www.youtube.com/watch?v=0XQ2FSz3EK8&t=335s
    private static final double THRESHOLD = 0.001;

    public static Vector3D solveEPA(Object3D o1, Object3D o2) {
        Simplex solGJK = solveGJK(o1, o2);
        if (solGJK == null) { return new Vector3D(); }

        List<Vector3D> ver1 = o1.getMesh().getPointsInWorld();
        List<Vector3D> ver2 = o2.getMesh().getPointsInWorld();

        Polytope polytope = new Polytope(solGJK);

        double minDistance = Double.MAX_VALUE;
        Vector3D minNormal = new Vector3D();

        while (minDistance == Double.MAX_VALUE) {
            Face minFace = getMinFace(polytope);
            minDistance = minFace.normal().dotProduct(minFace.a());
            minNormal = minFace.normal();

            Vector3D support = GJK.calculateNewVertex(ver1,ver2,minNormal);
            double supportDistance = minNormal.dotProduct(support);

            if (Math.abs(supportDistance - minDistance) > THRESHOLD) {
                minDistance = Double.MAX_VALUE;
                //Add new point
            }
        }

        return minNormal.scaled(minDistance + THRESHOLD);
    }

    private static Face getMinFace(Polytope polytope) {
        double minDistance = Double.MAX_VALUE;
        Face minFace = polytope.faces.get(0);

        for (int i = 0; i < polytope.faces.size(); i++) {
            Face face = polytope.faces.get(i);
            double distance = face.normal().dotProduct(face.a());

            if (distance < 0) {
                distance += -1;
                face.normal().invert();
            }

            if (distance < minDistance) {
                minDistance = distance;
                minFace = face;
            }
        }
        return minFace;
    }
}
