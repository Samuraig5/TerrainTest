package Engine3d.Math;

import Engine3d.Math.Vector.Vector3D;
import Engine3d.Physics.Object3D;

public class Ray
{
    private Object3D source;
    private Vector3D origin;
    private Vector3D direction;

    public Ray(Object3D source, Vector3D origin, Vector3D direction) {
        this.source = source;
        this.origin = new Vector3D(origin);
        this.direction = new Vector3D(direction);
    }

    public Vector3D getOrigin() {
        return origin;
    }
    public Vector3D getDirection() {
        return direction;
    }
    public Object3D getSource() {
        return source;
    }

    public void advance() {
        origin.translate(direction);
    }
}
