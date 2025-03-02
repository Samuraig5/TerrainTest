package Math.Vector;

public abstract class VectorW extends Vector {

    private double w = 1;

    public double w() {return w;}
    public void w(double w) {this.w = w;}

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) { return false; }
        return (w() == ((VectorW)obj).w());
    }
}
