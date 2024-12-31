package WorldSpace;

public class Vector3D implements Translatable
{
    protected double x;
    protected double y;
    protected double z;

    public Vector3D(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public double x() {return x;}
    public double y() {return y;}
    public double z() {return z;}
    @Override
    public void translate(Vector3D delta){
        this.x += delta.x;
        this.y += delta.y;
        this.z += delta.z;
    }
    public void translate(double deltaX, double deltaY, double deltaZ){
        this.x += deltaX;
        this.y += deltaY;
        this.z += deltaZ;
    }

    public void set(double newX, double newY, double newZ)
    {
        this.x = newX;
        this.y = newY;
        this.z = newZ;
    }
}
