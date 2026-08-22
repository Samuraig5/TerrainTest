package Engine3d.Rendering;

import Math.Vector.Vector3D;
import Math.Matrix4x4;

public final class Frustum {
    private final double[][] p = new double[6][4];   // 6 planes, each (a,b,c,d)

    public Frustum(Matrix4x4 M) {
        double[][] m = M.mat;
        // colK components:
        double[] c0={m[0][0],m[1][0],m[2][0],m[3][0]};
        double[] c1={m[0][1],m[1][1],m[2][1],m[3][1]};
        double[] c2={m[0][2],m[1][2],m[2][2],m[3][2]};
        double[] c3={m[0][3],m[1][3],m[2][3],m[3][3]};
        for (int i=0;i<4;i++){
            p[0][i]=c3[i]+c0[i];   // left
            p[1][i]=c3[i]-c0[i];   // right
            p[2][i]=c3[i]+c1[i];   // bottom
            p[3][i]=c3[i]-c1[i];   // top
            p[4][i]=c2[i];         // near
            p[5][i]=c3[i]-c2[i];   // far
        }
    }

    public boolean isVisible(Vector3D min, Vector3D max) {
        for (double[] pl : p) {
            double px = (pl[0] >= 0) ? max.x() : min.x();
            double py = (pl[1] >= 0) ? max.y() : min.y();
            double pz = (pl[2] >= 0) ? max.z() : min.z();
            if (pl[0]*px + pl[1]*py + pl[2]*pz + pl[3] < 0)
                return false;   // fully outside this plane
        }
        return true;
    }
}
