package Physics.GJK;

import Math.Vector.Vector3D;

import java.util.List;

public class Simplex {

    private int count;
    private Vector3D a;
    private Vector3D b;
    private Vector3D c;
    private Vector3D d;

    public int count() { return count; }
    public void count(int count) { this.count = count; }
    public void incrementCount() { count++; }
    public Vector3D a() { return a; }
    public void a(Vector3D a) { this.a = a; }
    public Vector3D b() { return b; }
    public void b(Vector3D b) { this.b = b; }
    public Vector3D c() { return c; }
    public void c(Vector3D c) { this.c = c; }
    public Vector3D d() { return d; }
    public void d(Vector3D d) { this.d = d; }

    /**
     * Shifts the vertices of the simplex back.
     * After the simplex has been shifted, a is free to be assigned a new vertex.
     * (c -> d, b -> c, a -> b)
     */
    public void shiftBack() {
        d = c;
        c = b;
        b = a;
    }
}
