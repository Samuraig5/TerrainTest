package Math.Vector;

public class Vector5D extends Vector
{
    @Override
    protected void init() {
        components = new double[5];
    }

    public Vector5D(double a, double b, double c, double d, double e) {
        init();
        setComponents(new double[] {a, b, c, d ,e});
    }

    public double a() { return components[0]; }
    public void a(double a) { components[0] = a; }
    public double b() { return components[1]; }
    public void b(double b) { components[1] = b; }
    public double c() { return components[2]; }
    public void c(double c) { components[2] = c; }
    public double d() { return components[3]; }
    public void d(double d) { components[3] = d; }
    public double e() { return components[4]; }
    public void e(double e) { components[4] = e; }

    @Override
    public Vector3D getPosition() {
        return new Vector3D(a(), b(), c());
    }

    @Override
    public Vector clone() {
        return new Vector5D(a(), b(), c(), d(), e());
    }
}
