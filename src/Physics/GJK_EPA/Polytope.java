package Physics.GJK_EPA;

import Math.Vector.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class Polytope
{
    List<Vector3D> vertices = new ArrayList<>();
    List<Face> faces = new ArrayList<>();

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

    private void constructFacesFromSimplex(Simplex simplex) {
        // Simplex will have faces in 3D space. In a 3D simplex, we have 4 vertices and 4 faces.
        // Each face is a triangle, defined by 3 vertices.

        if (simplex.count() == 4) {
            // For a 3D simplex (tetrahedron), create faces with 3 vertices each
            faces.add(new Face(simplex.a(), simplex.b(), simplex.c()));
            faces.add(new Face(simplex.a(), simplex.c(), simplex.d()));
            faces.add(new Face(simplex.a(), simplex.d(), simplex.b()));
            faces.add(new Face(simplex.b(), simplex.c(), simplex.d()));
        }
        // Add logic for other dimensional cases if needed
    }

    // Step 3: Find the closest face to the origin
    public Face findClosestFace() {
        Face closestFace = null;
        double closestDistance = Double.MAX_VALUE;

        // Iterate through all faces
        for (Face face : faces) {
            double distance = face.getDistanceToOrigin();
            if (distance < closestDistance) {
                closestDistance = distance;
                closestFace = face;
            }
        }

        return closestFace;
    }
}
