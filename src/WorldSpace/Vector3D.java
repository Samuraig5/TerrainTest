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
    public Vector3D(){this.x=0; this.y=0; this.z=0;}

    public Vector3D(Vector3D source) {
        this.x = source.x; this.y = source.y(); this.z = source.z();
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

    public void set(double newX, double newY, double newZ)
    {
        this.x = newX;
        this.y = newY;
        this.z = newZ;
    }
    public void set(Vector3D source)
    {
        set(source.x, source.y, source.z);
    }

    public Vector3D getScaledClone(double scalar)
    {
        Vector3D clone = new Vector3D(x,y,z);
        clone.scale(scalar);
        return clone;
    }
}
