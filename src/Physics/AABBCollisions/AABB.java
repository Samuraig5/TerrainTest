package Physics.AABBCollisions;

import Math.Geometries.Box;
import Math.Raycast.Ray;
import Math.Vector.Vector3D;

public class AABB extends Box
{
    public AABB(Vector3D min, Vector3D max) {
        super(min, max);
    }

    public Vector3D collision(Vector3D otherMin, Vector3D otherMax) {
        double totalX = (max().x() - min().x()) + (otherMax.x() - otherMin.x());
        double projXLen = Math.max(
                max().x() - otherMin.x(),
                otherMax.x() - min().x());
        if (totalX < projXLen) { return new Vector3D(0,0,0); }

        double totalY = (max().y() - min().y()) + (otherMax.y() - otherMin.y());
        double projYLen = Math.max(
                max().y() - otherMin.y(),
                otherMax.y() - min().y());
        if (totalY < projYLen) { return new Vector3D(0,0,0); }

        double totalZ = (max().z() - min().z()) + (otherMax.z() - otherMin.z());
        double projZLen = Math.max(
                max().z() - otherMin.z(),
                otherMax.z() - min().z());
        if (totalZ < projZLen) { return new Vector3D(0,0,0); }

        double overlapX = totalX - projXLen;
        double overlapY = totalY - projYLen;
        double overlapZ = totalZ - projZLen;

        if (overlapX <= overlapY && overlapX <= overlapZ) {
            // X-axis has the smallest overlap
            Vector3D deltaX = new Vector3D(1, 0, 0);
            if (max().x() < otherMax.x()) { deltaX = deltaX.inverted();}
            return deltaX.scaled(overlapX);
        }
        else if (overlapY <= overlapX && overlapY <= overlapZ) {
            // Y-axis has the smallest overlap
            Vector3D deltaY = new Vector3D(0, 1, 0);
            if (max().y() < otherMax.y()) { deltaY = deltaY.inverted();}
            return deltaY.scaled(overlapY);
        }
        else {
            // Z-axis has the smallest overlap
            Vector3D deltaZ = new Vector3D(0, 0, 1);
            if (max().z() < otherMax.z()) { deltaZ = deltaZ.inverted();}
            return deltaZ.scaled(overlapZ);
        }
    }

    public Vector3D collision(AABB other) {
        return collision(other.min(), other.max());
    }

    public Vector3D collision(Ray ray) {
        return collision(ray.getOrigin(), ray.getOrigin());
    }

    public boolean overlaps(AABB other) {
        return max().x() > other.min().x() && min().x() < other.max().x()
                && max().y() > other.min().y() && min().y() < other.max().y()
                && max().z() > other.min().z() && min().z() < other.max().z();
    }
}
