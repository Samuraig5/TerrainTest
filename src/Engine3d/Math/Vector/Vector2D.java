package Engine3d.Math.Vector;

public class Vector2D extends VectorW {
    @Override
    protected void init() {
        components = new double[2];
    }

    public Vector2D(double u, double v, double w) {
        init();
        setComponents(new double[] {u,v});
        w(w);
    }
    public Vector2D(double u, double v) {
        init();
        setComponents(new double[] {u,v});
    }
    public Vector2D() {
        init();
    }
    public Vector2D(Vector2D source) {
        init();
        setComponents(source);
        w(source.w());
    }

    public double u() {return getValue(0);}
    public void u(double u) {setComponent(0,u);}
    public double v() {return getValue(1);}
    public void v(double v) {setComponent(1,v);}

    @Override
    public Vector clone() {
        return new Vector2D(this);
    }

    @Override
    public Vector3D getPosition() {
        return new Vector3D(u(), v(), 0);
    }
}
