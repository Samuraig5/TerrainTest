package WorldSpace;

public class Vector2D
{
    private double u;
    private double v;

    public Vector2D(double u, double v) {
        set(u,v);
    }
    public Vector2D(){set(0,0);}
    public Vector2D(Vector2D source){set(source.u(), source.v());}

    public void set(double newU, double newV) {
        this.u = newU; this.v = newV;
    }

    public void translate(double deltaU, double deltaV){
    set(u+deltaU, v+deltaV);
}


    public double u() {return u;}
    public double v() {return v;}
}
