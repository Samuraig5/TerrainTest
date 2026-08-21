package Math.Vector;

public class Vector3D {
    private final double x;
    private final double y;
    private final double z;
    private final double w;

    public Vector3D(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;

    }
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1;
    }

    public Vector3D(Vector3D source) {
        this.x = source.x();
        this.y = source.y();
        this.z = source.z();
        this.w = source.w();
    }

    public double x() {return x;}
    public double y() {return y;}
    public double z() {return z;}
    public double w() {return w;}

    public Vector3D x(double x) {return new Vector3D(x, this.y, this.z, this.w);}
    public Vector3D y(double y) {return new Vector3D(this.x, y, this.z, this.w);}
    public Vector3D z(double z) {return new Vector3D(this.x, this.y, z, this.w);}
    public Vector3D w(double w) {return new Vector3D(this.x, this.y, this.z, w);}

    public Vector3D translated(Vector3D delta){
        return new Vector3D(x+delta.x, y+delta.y, z+delta.z, w+delta.w);
    }

    public Vector3D scaled(Vector3D delta){
        return new Vector3D(x*delta.x, y*delta.y, z*delta.z, w*delta.w);
    }
    public Vector3D scaled(double delta){
        return new Vector3D(x*delta, y*delta, z*delta, w*delta);
    }

    public Vector3D inverted() {
        return new Vector3D(-x, -y, -z, -w);
    }

    public Vector3D normalized(){
        return scaled(1/magnitude());
    }

    public double magnitude() {
        double sumOfSquares = Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2);
        return Math.sqrt(sumOfSquares);
    }

    /**
     * Calculates the euclidian distance of the points represented by the vectors.
     * @param other the other vector.
     * @return positive distance
     */
    public double distanceTo(Vector3D other) {
        double result = Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2) + Math.pow(z - other.z, 2);
        return Math.abs(Math.sqrt(result));
    }

    public boolean isEmpty() {
        if (x==0 && y==0 && z==0 && w==0) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if the dot product with the other vector is greater than 0.
     * @param other other vector to be tested against.
     * @return true if this.dotProduct(other) > 0.
     */
    public boolean sameDirection (Vector3D other) {
        return dotProduct(other) > 0;
    }

    public Vector3D crossProduct(Vector3D other) {
        double x = this.y() * other.z() - this.z() * other.y();
        double y = this.z() * other.x() - this.x() * other.z();
        double z = this.x() * other.y() - this.y() * other.x();
        return new Vector3D(x,y,z);
    }

    /**
     * Calculates the dot product of the vector and another vector.
     * The dot product projects one vector onto the other.
     * The magnitude of the projected vector is returned as the result.
     *
     * The dot product is a measure for the "likeness" of two vectors.
     * A larger positive number means the vectors are alike (point in the same general direction).
     * A larger negative number means the vectors are opposing (point away from each other).
     * If the result is 0, the two vectors are orthogonal to each other.
     * @param other the other vector.
     * @return a double value of arbitrary size.
     */
    public double dotProduct(Vector3D other) {
        return x*other.x+y*other.y+z*other.z;
    }

    public String toStringRounded() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(Math.round(x() * 10.0) / 10.0); sb.append(", ");
        sb.append(Math.round(y() * 10.0) / 10.0); sb.append(", ");
        sb.append(Math.round(z() * 10.0) / 10.0); sb.append(", ");
        sb.append(Math.round(w() * 10.0) / 10.0); sb.append(", ");
        sb.append(")");
        return sb.toString();
    }

    public static Vector3D lerp(Vector3D a, Vector3D b, double t) {
        return new Vector3D(a.x()+(b.x()-a.x())*t, a.y()+(b.y()-a.y())*t, a.z()+(b.z()-a.z())*t);
    }

    public static Vector3D FORWARD() {
        return new Vector3D(0,0,1);
    }
    public static Vector3D BACK() {
        return new Vector3D(0,0,-1);
    }
    public static Vector3D RIGHT() {
        return new Vector3D(1,0,0);
    }
    public static Vector3D LEFT() {
        return new Vector3D(-1,0,0);
    }
    public static Vector3D DOWN() {
        return new Vector3D(0,-1,0);
    }
    public static Vector3D UP() {
        return new Vector3D(0,1,0);
    }
}
