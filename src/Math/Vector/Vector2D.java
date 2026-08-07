package Math.Vector;

public class Vector2D {
    private final double u;
    private final double v;
    private final double w;

    public Vector2D(double u, double v, double w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }
    public Vector2D(double u, double v) {
        this.u = u;
        this.v = v;
        this.w = 1;
    }
    public Vector2D(Vector2D source) {
        this.u = source.u();
        this.v = source.v();
        this.w = source.w();
    }

    public double u() {return u;}
    public double v() {return v;}
    public double w() {return w;}

    public Vector2D u(double u) {return new Vector2D(u, this.v, this.w);}
    public Vector2D v(double v) {return new Vector2D(this.u, v, this.w);}
    public Vector2D w(double w) {return new Vector2D(this.u, this.v, w);}

    public Vector2D translate(Vector2D delta) {return new Vector2D(u+delta.u, v+delta.v, w+delta.w);}
    public Vector2D scale(Vector2D delta) {return new Vector2D(u*delta.u, v*delta.v, w*delta.w);}
}
