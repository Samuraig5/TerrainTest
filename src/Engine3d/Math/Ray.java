package Engine3d.Math;

import Engine3d.Math.Vector.Vector3D;
import Engine3d.Physics.Object3D;

public class Ray extends Line
{
    private Object3D source;

    public Ray(Object3D source, Vector3D origin, Vector3D direction) {
        super(origin, direction);
        this.source = source;
    }

    public Vector3D getOrigin() {
        return p1();
    }
    public Vector3D getDirection() {
        return p2();
    }
    public Object3D getSource() {
        return source;
    }

    public void advance() {
        getOrigin().translate(getDirection());
    }

    public Vector3D next() {
        return getOrigin().translated(getDirection());
    }
}
