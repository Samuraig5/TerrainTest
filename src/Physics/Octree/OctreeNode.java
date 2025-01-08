package Physics.Octree;

import Engine3d.Math.Vector.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class OctreeNode<T>
{
    private final OctreeSpace boundary;  // Axis-aligned bounding box defining this node's region
    private final List objects; // Objects contained in this node
    private final int capacity;   // Maximum objects before subdividing
    private boolean isDivided;    // Whether this node has been subdivided
    private OctreeNode<T>[] children; // The 8 child nodes

    public OctreeNode(OctreeSpace boundary, int capacity) {
        this.boundary = boundary;
        this.capacity = capacity;
        this.objects = new ArrayList<>();
        this.isDivided = false;
    }

    public void subdivide() {
        double x = boundary.getCenterPoint().x();
        double y = boundary.getCenterPoint().y();
        double z = boundary.getCenterPoint().z();
        double halfSize = boundary.getSize() / 2;

        children = new OctreeNode[8];
        children[0] = new OctreeNode<>(new OctreeSpace(new Vector3D(x - halfSize, y - halfSize, z - halfSize), halfSize), capacity);
        children[1] = new OctreeNode<>(new OctreeSpace(new Vector3D(x + halfSize, y - halfSize, z - halfSize), halfSize), capacity);
        children[2] = new OctreeNode<>(new OctreeSpace(new Vector3D(x - halfSize, y + halfSize, z - halfSize), halfSize), capacity);
        children[3] = new OctreeNode<>(new OctreeSpace(new Vector3D(x + halfSize, y + halfSize, z - halfSize), halfSize), capacity);
        children[4] = new OctreeNode<>(new OctreeSpace(new Vector3D(x - halfSize, y - halfSize, z + halfSize), halfSize), capacity);
        children[5] = new OctreeNode<>(new OctreeSpace(new Vector3D(x + halfSize, y - halfSize, z + halfSize), halfSize), capacity);
        children[6] = new OctreeNode<>(new OctreeSpace(new Vector3D(x - halfSize, y + halfSize, z + halfSize), halfSize), capacity);
        children[7] = new OctreeNode<>(new OctreeSpace(new Vector3D(x + halfSize, y + halfSize, z + halfSize), halfSize), capacity);

        isDivided = true;
    }

    public boolean insert(T object, Vector3D position) {
        if (!boundary.contains(position)) {
            return false; // Object is outside the boundary
        }

        if (objects.size() < capacity || boundary.getSize() <= 1.0) {
            objects.add(object); // Add directly if capacity isn't exceeded
            return true;
        }

        if (!isDivided) {
            subdivide(); // Subdivide if not already divided
        }

        // Try inserting into children
        for (OctreeNode<T> child : children) {
            if (child.insert(object, position)) {
                return true;
            }
        }

        return false; // Shouldn't reach here if logic is correct
    }
}
