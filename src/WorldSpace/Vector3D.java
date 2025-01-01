package WorldSpace;

public class Vector3D implements Translatable
{
    private double x;
    private double y;
    private double z;

    public Vector3D(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
    }
    public Vector3D(){this.x=0; this.y=0; this.z=0;}

    public Vector3D(Vector3D source) {
        this.x = source.x; this.y = source.y(); this.z = source.z();
    }
    public double x() {return x;}
    public double y() {return y;}
    public double z() {return z;}
    public void translate(double deltaX, double deltaY, double deltaZ){
        this.x += deltaX; this.y += deltaY; this.z += deltaZ;
    }
    @Override
    public void translate(Vector3D delta){
        translate(delta.x, delta.y, delta.z);
    }
    public void translate(double uniformTranslation)
    {
        translate(uniformTranslation, uniformTranslation, uniformTranslation);
    }

    public void scale(Vector3D scalars)
    {
        set(x* scalars.x,y* scalars.y,z* scalars.z);
    }
    public void scale(double scalar)
    {
        scale(new Vector3D(scalar, scalar, scalar));
    }

    public void set(double newX, double newY, double newZ) {
        this.x = newX; this.y = newY; this.z = newZ;
    }
    public void set(Vector3D source)
    {
        set(source.x, source.y, source.z);
    }

    /**
     * @return The inverse of the vector.
     */
    public Vector3D inverse() {
        Vector3D clone = new Vector3D(-x,-y,-z); return clone;
    }
    /**
     * Inverts the vector.
     */
    public void invert(){
        set(-x,-y,-z);
    }
    /**
     * Normalizes the vector to length 1.
     */
    public void normalize(){
        double length = Math.sqrt(x*x+y*y+z*z);
        x /= length; y /= length; z /= length;
    }
    /**
     * @param other the other vector
     * @return the dot product between this vector and the other vector
     */
    public double dotProduct(Vector3D other) {
        return x * other.x + y * other.y + z * other.z;
    }
}
