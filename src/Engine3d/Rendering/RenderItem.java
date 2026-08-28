package Engine3d.Rendering;

import Engine3d.Model.Mesh;
import Math.Vector.Vector3D;

public record RenderItem(
        Mesh mesh,
        Vector3D scale,
        Vector3D position,
        Vector3D rotation
) {}

