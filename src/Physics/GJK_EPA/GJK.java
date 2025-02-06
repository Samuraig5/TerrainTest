package Physics.GJK_EPA;

import Math.Vector.Vector3D;
import Physics.Object3D;

import java.util.List;

public class GJK
{
    public static boolean boolSolveGJK(Object3D o1, Object3D o2) {
        Simplex sim = solveGJK(o1, o2);
        return sim != null;
    }

    public static Simplex solveGJK(Object3D o1, Object3D o2) {
        List<Vector3D> ver1 = o1.getMesh().getPointsInWorld();
        List<Vector3D> ver2 = o2.getMesh().getPointsInWorld();

        Simplex simplex = new Simplex();

        Vector3D dir = o1.getPosition().translated(o2.getPosition().inverted()); //o1.pos - o2.pos
        simplex.a(calculateNewVertex(ver1, ver2, dir));
        simplex.incrementCount();

        dir = simplex.a().inverted();

        while (true)
        {
            simplex.shiftBack();
            simplex.a(calculateNewVertex(ver1, ver2, dir));
            simplex.incrementCount();

            //If new support point isn't on opposite side of origin, its impossible for the simplex to enclose the origin.
            if (simplex.a().dotProduct(dir) < 0) {
                return null;
            }

            switch (simplex.count()) {
                case 2:
                    dir = solveSimplex2(simplex, dir);
                    break;
                case 3:
                    dir = solveSimplex3(simplex, dir);
                    break;
                case 4:
                    dir = solveSimplex4(simplex, dir);
                    if (dir == null) {
                        return simplex;
                    }
                    break;
            }
        }
    }

    /**
     * Returns to most extreme vertex from the list in the provided direction.
     * @param vertices list of vertices.
     * @param direction direction to be checked.
     * @return the most extreme vertex in the direction.
     */
    protected static Vector3D supportFunction(List<Vector3D> vertices, Vector3D direction) {
        Vector3D largestVertex = vertices.get(0);
        double largestDot = largestVertex.dotProduct(direction);
        for (int i = 1; i < vertices.size(); i++) {
            Vector3D currentVertex = vertices.get(i);
            double currentDot = currentVertex.dotProduct(direction);

            if (currentDot > largestDot) {
                largestDot = currentDot;
                largestVertex = currentVertex;
            }
        }
        return largestVertex;
    }

    /**
     * Returns a new vertex based on the difference of the most extreme vertices in the lists.
     * @param ver1 List of vertices 1.
     * @param ver2 List of vertices 2.
     * @param dir Direction to be checked.
     * @return the difference of the most extreme vertices.
     */
    private static Vector3D calculateNewVertex(List<Vector3D> ver1, List<Vector3D> ver2, Vector3D dir) {
        Vector3D exrm1 = supportFunction(ver1, dir);
        Vector3D exrm2 = supportFunction(ver2, dir.inverted());
        return exrm1.translated(exrm2.inverted());
    }

    /**
     * Finds the next point for the 1D (line) simplex to expand to.
     * @param simplex the simplex.
     * @param dir the previous direction.
     * @return the new direction.
     */
    private static Vector3D solveSimplex2(Simplex simplex, Vector3D dir) {
        Vector3D ab = simplex.b().translated(simplex.a().inverted()); //b - a
        Vector3D ao = simplex.a().inverted(); // -a

        if (ab.sameDirection(ao)) {
            //(Projected) Origin falls on the line
            Vector3D res = ab.crossProduct(ao);
            res = res.crossProduct(ab);
            return res;
        }
        else {
            //Origin is closer to a (but doesn't fall on the line)
            simplex.count(1);
            return ao;
        }
    }

    /**
     * Finds the next point for the 2D (triangle) simplex to expand to.
     * @param simplex the simplex.
     * @param dir the previous direction.
     * @return the new direction.
     */
    private static Vector3D solveSimplex3(Simplex simplex, Vector3D dir) {
        Vector3D ab = simplex.b().translated(simplex.a().inverted()); //b - a
        Vector3D ac = simplex.c().translated(simplex.a().inverted()); //c - a
        Vector3D abc = ab.crossProduct(ac);
        Vector3D ao = simplex.a().inverted(); // -a

        if (abc.crossProduct(ac).sameDirection(ao)) {
            if (ac.sameDirection(ao)) {
                //Origin is closest to line ac
                simplex.b(simplex.c());
                simplex.count(2);
                return ac.crossProduct(ao).crossProduct(ac);
            }
            else {
                if (ab.sameDirection(ao)) {
                    //Origin is closest to line ab
                    simplex.count(2);
                    return ab.crossProduct(ao).crossProduct(ab);
                }
                else {
                    //Origin is closest to point a
                    simplex.count(1);
                    return ao;
                }
            }
        }
        else {
            if (ab.crossProduct(abc).sameDirection(ao)) {
                if (ab.sameDirection(ao)) {
                    //Origin is closest to line ab
                    simplex.count(2);
                    return ab.crossProduct(ao).crossProduct(ab);
                }
                else {
                    //Origin is closest to point a
                    simplex.count(1);
                    return ao;
                }
            }
            else {
                if (abc.sameDirection(ao)) {
                    //Origin is closest to triangle abc
                    return abc;
                }
                else {
                    //Origin is closest to triangle abc (but facing away)
                    Vector3D temp = simplex.b();
                    simplex.b(simplex.c());
                    simplex.c(temp);

                    return abc.inverted();
                }
            }
        }
    }

    /**
     * Finds the next point for the 3D (tetrahedron) simplex to expand to.
     * @param simplex the simplex.
     * @param dir the previous direction.
     * @return the new direction.
     */
    private static Vector3D solveSimplex4(Simplex simplex, Vector3D dir) {
        Vector3D vec1 = simplex.b().translated(simplex.a().inverted());
        Vector3D vec2 = simplex.c().translated(simplex.a().inverted());
        Vector3D vec3 = simplex.d().translated(simplex.a().inverted());

        Vector3D ao = simplex.a().inverted();
        Vector3D abc = vec1.crossProduct(vec2);
        Vector3D acd = vec2.crossProduct(vec3);
        Vector3D adb = vec3.crossProduct(vec1);

        if (abc.sameDirection(ao)) {
            //Origin is closest to triangle abc
            simplex.count(3);
            return solveSimplex3(simplex, dir);
        }

        if (acd.sameDirection(ao)) {
            //Origin is closest to triangle acd
            simplex.b(simplex.c());
            simplex.c(simplex.d());
            simplex.count(3);
            return solveSimplex3(simplex, dir);
        }

        if (adb.sameDirection(ao)) {
            //Origin is closest to triangle adb
            simplex.c(simplex.b());
            simplex.b(simplex.d());
            simplex.count(3);
            return solveSimplex3(simplex, dir);
        }
        return null;
    }
}
