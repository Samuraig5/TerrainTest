package Math.Vector;

import java.util.Objects;

public class Vector3D extends VectorW
{
    @Override
    protected void init() {
        components = new double[3];
    }

    public Vector3D(double x, double y, double z, double w) {
        init();
        setComponents(new double[] {x, y, z});
        w(w);
    }
    public Vector3D(double x, double y, double z) {
        init();
        setComponents(new double[] {x,y,z});
    }
    public Vector3D() {
        init();
    }

    public Vector3D(Vector3D source) {
        init();
        setComponents(new double[] {source.x(), source.y(), source.z()});
        w(source.w());
    }

    public double x() {return getValue(0);}
    public void x(double x) {setComponent(0,x);}
    public double y() {return getValue(1);}
    public void y(double y) {setComponent(1,y);}
    public double z() {return getValue(2);}
    public void z(double z) {setComponent(2,z);}

    public Vector3D crossProduct(Vector3D other)
    {
        double x = this.y() * other.z() - this.z() * other.y();
        double y = this.z() * other.x() - this.x() * other.z();
        double z = this.x() * other.y() - this.y() * other.x();
        return new Vector3D(x,y,z);
    }

    @Override
    public Vector3D getPosition() {
        return new Vector3D(this);
    }

    @Override
    public Vector3D translated(Vector3D delta){
        return (Vector3D) super.translated(delta);
    }

    @Override
    public Vector3D scaled(double scalar){
        return (Vector3D) super.scaled(scalar);
    }
    public Vector3D scaled(Vector scalars){
        return (Vector3D) super.scaled(scalars);
    }

    @Override
    public Vector3D inverted() {
        return (Vector3D) super.inverted();
    }

    @Override
    public Vector3D normalized(){
        return (Vector3D) super.normalized();
    }

    @Override
    public Vector clone() {
        return new Vector3D(x(), y(), z(), w());
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
    @Override
    public int hashCode() {
        return Objects.hash(x(), y(), z(), w());
    }
}
