package WorldSpace;

public class Matrix4x4
{
    public double[][] mat = new double[4][4];

    /**
     * Multiplies a given input vector with the matrix.
     * Since the input vector is 3D, it has an implied fourth element set to '1'.
     * So given a vector 'in', the implied vector 'in~' would be 'in~=(in.x, in.y, in.z, 1.0f)'.
     * The vector is also normalized in the 'z' component.
     *
     * @param in The vector that should be multiplied with the matrix.
     * @return The resulting vector.
     */
    public Vector3D multiplyWithVect3D(Vector3D in)
    {
        Vector3D res = matrixVectorMultiplication(new Vector3D(in.x(), in.y(), in.z()));
        if (res.w() != 0) { //Normalisation of the output vector to 'z'
            double x = res.x()/ res.w();
            double y = res.y()/ res.w();
            double z = res.z()/ res.w();
            return new Vector3D(x, y, z);
        } else {
            System.err.println("Matrix4x4: w component of resulting vector is = 0");
            return new Vector3D(0,0,0);
        }
    }

    public Triangle multiplyWithTriangle(Triangle triangle)
    {
        Vector3D[] points = triangle.getPoints();
        Vector3D p1 = multiplyWithVect3D(points[0]);
        Vector3D p2 = multiplyWithVect3D(points[1]);
        Vector3D p3 = multiplyWithVect3D(points[2]);
        return new Triangle(p1, p2, p3);
    }

    public Vector3D matrixVectorMultiplication(Vector3D in)
    {
        double x = in.x() * mat[0][0]
                + in.y() * mat[1][0]
                + in.z() * mat[2][0]
                + in.w() * mat[3][0];
        double y = in.x() * mat[0][1]
                + in.y() * mat[1][1]
                + in.z() * mat[2][1]
                + in.w() * mat[3][1];
        double z = in.x() * mat[0][2]
                + in.y() * mat[1][2]
                + in.z() * mat[2][2]
                + in.w() * mat[3][2];
        double w = in.x() * mat[0][3]
                + in.y() * mat[1][3]
                + in.z() * mat[2][3]
                + in.w() * mat[3][3];
        return new Vector3D(x, y, z, w);
    }

    public static Matrix4x4 matrixMatrixMultiplication(Matrix4x4 m1, Matrix4x4 m2)
    {
        Matrix4x4 res = new Matrix4x4();
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 4; row++) {
                res.mat[row][col] = m1.mat[row][0] * m2.mat[0][col] +
                                    m1.mat[row][1] * m2.mat[1][col] +
                                    m1.mat[row][2] * m2.mat[2][col] +
                                    m1.mat[row][3] * m2.mat[3][col];
            }
        }
        return res;
    }

    public static Matrix4x4 getIdentityMatrix()
    {
        Matrix4x4 iMat = new Matrix4x4();
        iMat.mat[0][0] = 1f;
        iMat.mat[1][1] = 1f;
        iMat.mat[2][2] = 1f;
        iMat.mat[3][3] = 1f;
        return iMat;
    }

    public static Matrix4x4 getRotationMatrixX(double angle)
    {
        Matrix4x4 rotX = new Matrix4x4();
        rotX.mat[0][0] = 1f;
        rotX.mat[1][1] = Math.cos(angle);
        rotX.mat[1][2] = Math.sin(angle);
        rotX.mat[2][1] = -Math.sin(angle);
        rotX.mat[2][2] = Math.cos(angle);
        rotX.mat[3][3] = 1f;
        return rotX;
    }
    public static Matrix4x4 getRotationMatrixY(double angle)
    {
        Matrix4x4 rotZ = new Matrix4x4();
        rotZ.mat[0][0] = Math.cos(angle);
        rotZ.mat[2][0] = Math.sin(angle);
        rotZ.mat[0][2] = Math.sin(-angle);
        rotZ.mat[1][1] = 1f;
        rotZ.mat[2][2] = Math.cos(angle);
        rotZ.mat[3][3] = 1f;
        return rotZ;
    }
    public static Matrix4x4 getRotationMatrixZ(double angle)
    {
        Matrix4x4 rotZ = new Matrix4x4();
        rotZ.mat[0][0] = Math.cos(angle);
        rotZ.mat[0][1] = Math.sin(angle);
        rotZ.mat[1][0] = -Math.sin(angle);
        rotZ.mat[1][1] = Math.cos(angle);
        rotZ.mat[2][2] = 1f;
        rotZ.mat[3][3] = 1f;
        return rotZ;
    }
    public static Matrix4x4 getTranslationMatrix(Vector3D vec)
    {
        Matrix4x4 trans = new Matrix4x4();
        trans.mat[0][0] = 1;
        trans.mat[1][1] = 1;
        trans.mat[2][2] = 1;
        trans.mat[3][3] = 1;
        trans.mat[3][0] = vec.x();
        trans.mat[3][1] = vec.y();
        trans.mat[3][2] = vec.z();
        return trans;
    }

    public static Matrix4x4 getProjectionMatrix(double fov, double aspectRatio, double zNear, double zFar) {
    double fovRad = 1.0f / Math.tan(fov * 0.5f / 180.0f * Math.PI);
    Matrix4x4 projectionMatrix = new Matrix4x4();
    projectionMatrix.mat[0][0] = aspectRatio * fovRad;
    projectionMatrix.mat[1][1] = fovRad;
    projectionMatrix.mat[2][2] = zFar / (zFar - zNear);
    projectionMatrix.mat[3][2] = (-zFar * zNear) / (zFar - zNear);
    projectionMatrix.mat[2][3] = 1.0f;
    projectionMatrix.mat[3][3] = 0.0f;
    return projectionMatrix;
    }
}
