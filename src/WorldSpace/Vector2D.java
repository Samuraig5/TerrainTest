package WorldSpace;

public class Vector2D
{
    private double u;
    private double v;
    private double w = 1;

    public Vector2D(double u, double v, double w) {
        set(u,v, w);
    }
    public Vector2D(double u, double v) {
        set(u,v,1);
    }
    public Vector2D(){set(0,0);}
    public Vector2D(Vector2D source){set(source.u(), source.v());}

    public void set(double newU, double newV, double newW) {
        this.u = newU; this.v = newV; this.w = newW;
    }
    public void set(double newU, double newV) {
        set(newU, newV, 1f);
    }

    public void translate(double deltaU, double deltaV){
    set(u+deltaU, v+deltaV);
}


    public double u() {return u;}
    public double v() {return v;}
    public double w() {return w;}
}
