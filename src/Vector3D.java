import java.util.Vector;

public class Vector3D
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

    public void translate(Vector3D delta){
        this.x += delta.x;
        this.y += delta.y;
        this.z += delta.z;
    }
}
