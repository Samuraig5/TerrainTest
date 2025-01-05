package Engine3d.Math.Vector;

public class Vector3D extends VectorW
{
    @Override
    protected void init() {
        components = new double[3];
    }

    public Vector3D(double x, double y, double z, double w) {
        init();
        set(x, y, z, w);
    }
    public Vector3D(double x, double y, double z) {
        init();
        set(x, y, z);
    }
    public Vector3D() {
        init();
        set(0,0,0, 1);
    }

    public Vector3D(Vector3D source) {
        init();
        set(source.x(), source.y(), source.z(), source.w());
    }

    public double x() {return getValue(0);}
    public void x(double x) {setComponent(0,x);}
    public double y() {return getValue(1);}
    public void y(double y) {setComponent(1,y);}
    public double z() {return getValue(2);}
    public void z(double z) {setComponent(2,z);}
    public void set(double newX, double newY, double newZ, double newW) {
        x(newX); y(newY); z(newZ); w(newW);
    }
    public void set(double newX, double newY, double newZ) {
        set(newX, newY, newZ, w());
    }
    public void translate(double deltaX, double deltaY, double deltaZ){
        set(x()+deltaX, y()+deltaY, z()+deltaZ);
    }

    @Override
    public void translate(Vector3D delta){
        translate(delta.x(), delta.y(), delta.z());
    }

    @Override
    public Vector3D getPosition() {
        return new Vector3D(this);
    }

    public void translate(double uniformTranslation) {
        translate(uniformTranslation, uniformTranslation, uniformTranslation);
    }
    /**
     * Returns a new vector that is the result of translating this vector by a given vector.
     * @param delta translation vector
     * @return the result of the translation
     */
    public Vector3D translation(Vector3D delta){
        Vector3D out = new Vector3D(this);
        out.translate(delta);
        return out;
    }
    public void scale(Vector3D scalars) {
        set(x()*scalars.x(),y()*scalars.y(),z()*scalars.z(), w()*scalars.w());
    }
    public void scale(double scalar)
    {
        scale(new Vector3D(scalar, scalar, scalar, scalar));
    }
    public Vector3D scaled(double scalar){
        Vector3D res = new Vector3D(this);
        res.scale(scalar);
        return res;
    }

    /**
     * Inverts the vector.
     */
    public void invert(){
        set(-x(),-y(),-z());
    }
    /**
     * @return The inverse of the vector.
     */
    public Vector3D inverse() {
        Vector3D clone = new Vector3D(this);
        clone.invert();
        return clone;
    }
    /**
     * Normalizes the vector to length 1.
     */
    public void normalize(){
        double length = magnitude();
        set(x()/length, y()/length, z()/length);
    }
    public Vector3D normalized(){
        Vector3D clone = new Vector3D(this);
        clone.normalize();
        return clone;
    }

    public double distanceTo(Vector3D other)
    {
        double dx = this.x() - other.x();
        double dy = this.y() - other.y();
        double dz = this.z() - other.z();
        double dw = this.w() - other.w();
        return Math.sqrt(dx * dx + dy * dy + dz * dz + dw * dw);
    }

    /**
     * Calculates the dot product of the two vectors.
     * Vectors should be normalized!
     * @param other the other vector
     * @return the dot product between this vector and the other vector
     */
    public double dotProduct(Vector3D other) {
        return x() * other.x() + y() * other.y() + z() * other.z();
    }

    public Vector3D crossProduct(Vector3D other)
    {
        double x = this.y() * other.z() - this.z() * other.y();
        double y = this.z() * other.x() - this.x() * other.z();
        double z = this.x() * other.y() - this.y() * other.x();
        return new Vector3D(x,y,z);
    }

    public void clear() {x(0);y(0);z(0);w(1);}

    public String toStringRounded() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(Math.round(x() * 10.0) / 10.0); sb.append(", ");
        sb.append(Math.round(y() * 10.0) / 10.0); sb.append(", ");
        sb.append(Math.round(z() * 10.0) / 10.0); sb.append(", ");
        sb.append(Math.round(w() * 10.0) / 10.0); sb.append(", ");
        sb.append(")");
        return sb.toString();
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(x()); sb.append(", ");
        sb.append(y()); sb.append(", ");
        sb.append(z()); sb.append(", ");
        sb.append(w()); sb.append(", ");
        sb.append(")");
        return sb.toString();
    }

    @Override
    public Vector clone() {
        return null;
    }

    public static Vector3D DOWN()
    {
        return new Vector3D(0,-1,0);
    }
    public static Vector3D UP()
    {
        return new Vector3D(0,1,0);
    }
}
