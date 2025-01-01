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
        double x = in.x() * mat[0][0]
                + in.y() * mat[1][0]
                + in.z() * mat[2][0]
                + mat[3][0];
        double y = in.x() * mat[0][1]
                + in.y() * mat[1][1]
                + in.z() * mat[2][1]
                + mat[3][1];
        double z = in.x() * mat[0][2]
                + in.y() * mat[1][2]
                + in.z() * mat[2][2]
                + mat[3][2];
        double w = in.x() * mat[0][3]
                + in.y() * mat[1][3]
                + in.z() * mat[2][3]
                + mat[3][3];
        if (w != 0) //Normalisation of the output vector to 'z'
        {
            x /= w;
            y /= w;
            z /= w;
        }

        return new Vector3D(x, y, z);
    }

    public Triangle multiplyWithTriangle(Triangle triangle)
    {
        Vector3D[] points = triangle.getPoints();
        Vector3D p1 = multiplyWithVect3D(points[0]);
        Vector3D p2 = multiplyWithVect3D(points[1]);
        Vector3D p3 = multiplyWithVect3D(points[2]);
        return new Triangle(p1, p2, p3);
    }
}
