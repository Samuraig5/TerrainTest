package Engine3d.Math.Vector;

public class Vector2D extends Vector {
    @Override
    protected void init() {
        components = new double[3];
        w(1);
    }

    public Vector2D(double u, double v, double w) {
        init();
        setComponents(new double[] {u,v,w});
    }
    public Vector2D(double u, double v) {
        init();
        setComponents(new double[] {u,v,1});
    }
    public Vector2D() {
        init();
    }
    public Vector2D(Vector2D source) {
        init();
        setComponents(source);
    }

    public double u() {return getValue(0);}
    public void u(double u) {setComponent(0,u);}
    public double v() {return getValue(1);}
    public void v(double v) {setComponent(1,v);}
    public double w() {return getValue(2);}
    public void w(double w) {setComponent(2,w);}
    @Override
    public Vector clone() {
        return new Vector2D(this);
    }
}
