package Physics.Shapes;

import Math.Vector.Vector3D;
import Physics.AABBCollisions.AABB;

public interface ConvexShape {
    /** Farthest point of the shape in world-space direction d (d need not be normalized). */
    Vector3D support(Vector3D d);

    /** Any point inside the shape; used for GJK's first search direction. */
    Vector3D center();

    /** World-space bounds, used to skip pairs that can't touch. */
    AABB bounds();
}