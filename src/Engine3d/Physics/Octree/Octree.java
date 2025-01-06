package Engine3d.Physics.Octree;

import Engine3d.Math.Vector.Vector3D;
import Engine3d.Physics.AABB;

class Octree<T> {
    private final OctreeNode<T> root;

    public Octree(AABB boundary, int capacity) {
        this.root = new OctreeNode<>(boundary, capacity);
    }

    public boolean insert(T object, Vector3D position) {
        return root.insert(object, position);
    }
}

