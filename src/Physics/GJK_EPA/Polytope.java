package Physics.GJK_EPA;

import Math.Vector.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class Polytope
{
    List<Vector3D> vertices = new ArrayList<>();
    List<Face> faces = new ArrayList<>();

    public Polytope(Simplex source) {
        if (source.a() == null || source.b() == null || source.c() == null || source.d() == null) {
            System.err.println("Polytope: Input simplex is not a tetrahedron!");
        }
        try {
            Vector3D a = source.a();
            Vector3D b = source.b();
            Vector3D c = source.c();
            Vector3D d = source.d();

            vertices.add(a);
            vertices.add(b);
            vertices.add(c);
            vertices.add(d);

            faces.add(new Face(a,b,c));
            faces.add(new Face(a,d,b));
            faces.add(new Face(a,c,d));
            faces.add(new Face(b,d,c));
        }
        catch (NullPointerException e) {
            System.err.println("Polytope: Unable to generate polytope!");
        }
    }

    public void splice(double minIndex, int i, Vector3D support) {
    }
}
