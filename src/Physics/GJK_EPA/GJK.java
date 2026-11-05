package Physics.GJK_EPA;

import Math.Vector.Vector3D;
import Engine3d.Objects.Object3D;
import Math.Raycast.Ray;
import Physics.Shapes.ConvexShape;

import java.util.ArrayList;
import java.util.List;

public class GJK
{
    static Vector3D minkowskiSupport(ConvexShape a, ConvexShape b, Vector3D d) {
        return a.support(d).translated(b.support(d.inverted()).inverted());   // a(d) - b(-d)
    }

    public static Simplex solveGJK(ConvexShape a, ConvexShape b) {
        Simplex simplex = new Simplex();

        Vector3D dir = a.center().translated(b.center().inverted());

        // Guardrail against zero vector if the centers line up.
        if (dir.magnitude() < 1e-9) {
            dir = Vector3D.RIGHT();
        }

        simplex.a(minkowskiSupport(a,b,dir));
        simplex.incrementCount();

        dir = simplex.a().inverted();

        int iterations = 0;
        while (true)
        {
            if (++iterations > 64) return null;           // 64 is plenty for convex shapes
            if (dir.magnitude() < 1e-12) return null;     // origin on the simplex = touching, zero depth

            simplex.shiftBack();
            simplex.a(minkowskiSupport(a,b,dir));
            simplex.incrementCount();

            //If new support point isn't on opposite side of origin, it's impossible for the simplex to enclose the origin.
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
     * Finds the next point for the 1D (line) simplex to expand to.
     * @param simplex the simplex.
     * @param dir the previous direction.
     * @return the new direction.
     */
    private static Vector3D solveSimplex2(Simplex simplex, Vector3D dir) {
        Vector3D ab = simplex.b().translated(simplex.a().inverted()); //b - a
        Vector3D ao = simplex.a().inverted(); // -a

        if (ab.sameDirection(ao)) {
            Vector3D res = ab.crossProduct(ao).crossProduct(ab);
            if (res.magnitude() < 1e-12) {   // origin lies on the line ab
                res = ab.crossProduct(Vector3D.RIGHT());
                if (res.magnitude() < 1e-12) res = ab.crossProduct(Vector3D.UP());
            }
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
