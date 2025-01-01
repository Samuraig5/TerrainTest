package WorldSpace;

public class Vector3D implements Translatable
{
    private double x;
    private double y;
    private double z;

    public Vector3D(double x, double y, double z) {
        set(x, y, z);
    }
    public Vector3D(){set(0,0,0);}

    public Vector3D(Vector3D source) {
        set(source.x, source.y, source.z);
    }
    public double x() {return x;}
    public double y() {return y;}
    public double z() {return z;}
    public void set(double newX, double newY, double newZ) {
        this.x = newX; this.y = newY; this.z = newZ;
    }
    public void set(Vector3D source)
    {
        set(source.x, source.y, source.z);
    }
    public void translate(double deltaX, double deltaY, double deltaZ){
        set(x+deltaX, y+deltaY, z+deltaZ);
    }
    @Override
    public void translate(Vector3D delta){
        translate(delta.x, delta.y, delta.z);
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
        set(x*scalars.x,y*scalars.y,z*scalars.z);
    }
    public void scale(double scalar)
    {
        scale(new Vector3D(scalar, scalar, scalar));
    }

    /**
     * Inverts the vector.
     */
    public void invert(){
        set(-x,-y,-z);
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
        double length = Math.sqrt(x*x+y*y+z*z);
        set(x/length, y/length, z/length);
    }
    /**
     * Calculates the dot product of the two vectors.
     * Vectors should be normalized!
     * @param other the other vector
     * @return the dot product between this vector and the other vector
     */
    public double dotProduct(Vector3D other) {
        return x * other.x + y * other.y + z * other.z;
    }
}
