package Physics.GJK_EPA;

import Math.Vector.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class Polytope
{
    List<Vector3D> vertices = new ArrayList<>();

    public Polytope(Simplex source) {
        if (source.a() != null) {
            vertices.add(source.a());
        }
        if (source.b() != null) {
            vertices.add(source.b());
        }
        if (source.c() != null) {
            vertices.add(source.c());
        }
        if (source.d() != null) {
            vertices.add(source.d());
        }
    }

    public void addVertex(Vector3D newVert) {
        vertices.add(newVert);
    }
}
