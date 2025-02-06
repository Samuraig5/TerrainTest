package Physics.GJK_EPA;

import Math.Vector.Vector3D;
import Physics.Object3D;

import static Physics.GJK_EPA.GJK.solveGJK;

public class EPA
{
    public static Vector3D solveEPA(Object3D o1, Object3D o2) {
        Simplex solGJK = solveGJK(o1, o2);
        if (solGJK == null) { return new Vector3D(); }

        Polytope polytope = new Polytope(solGJK);

        //EPA HERE

        return new Vector3D(1,1,1);
    }
}
