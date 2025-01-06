package Engine3d.Physics;

import Engine3d.Math.Box;
import Engine3d.Math.Vector.Vector3D;

public class AABB extends Box
{
    public AABB(Vector3D min, Vector3D max) {
        super(min, max);
    }

    public Vector3D collision(AABB other) {

        double totalX = (max().x() - min().x()) + (other.max().x() - other.min().x());
        double projXLen = Math.max(
                max().x() - other.min().x(),
                other.max().x() - min().x());
        if (totalX < projXLen) { return new Vector3D(); }

        double totalY = (max().y() - min().y()) + (other.max().y() - other.min().y());
        double projYLen = Math.max(
                max().y() - other.min().y(),
                other.max().y() - min().y());
        if (totalY < projYLen) { return new Vector3D(); }

        double totalZ = (max().z() - min().z()) + (other.max().z() - other.min().z());
        double projZLen = Math.max(
                max().z() - other.min().z(),
                other.max().z() - min().z());
        if (totalZ < projZLen) { return new Vector3D(); }

        double overlapX = totalX - projXLen;
        double overlapY = totalY - projYLen;
        double overlapZ = totalZ - projZLen;

        Vector3D separatingVector;

        if (overlapX <= overlapY && overlapX <= projZLen) {
            // X-axis has the smallest overlap
            separatingVector = new Vector3D(overlapX < 0 ? -overlapX : overlapX, 0, 0);
        } else if (overlapY <= overlapX && overlapY <= projZLen) {
            // Y-axis has the smallest overlap
            separatingVector = new Vector3D(0, overlapY < 0 ? -overlapY : overlapY, 0);
        } else {
            // Z-axis has the smallest overlap
            separatingVector = new Vector3D(0, 0, overlapZ < 0 ? -projZLen : projZLen);
        }

        return separatingVector;
    }
}
