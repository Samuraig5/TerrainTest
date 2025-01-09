package Physics.Octree;

import Math.Vector.Vector3D;

class Octree<T> {
    private final OctreeNode<T> root;

    public Octree(OctreeSpace boundary, int capacity) {
        this.root = new OctreeNode<>(boundary, capacity);
    }

    public boolean insert(T object, Vector3D position) {
        return root.insert(object, position);
    }
}

