package Math.Raycast;

import Engine3d.Model.SimpleMeshes.BoxMesh;
import Math.Vector.Vector3D;
import Physics.AABBCollisions.StaticAABBObject;
import Engine3d.Object3D;
import Math.Line;

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

    public Object3D getPointObject() {
        double BOX_SIZE = 0.001;
        Object3D pointObject = new StaticAABBObject(source.getScene());
        pointObject.setMesh(new BoxMesh(pointObject, new Vector3D(BOX_SIZE,BOX_SIZE,BOX_SIZE)));
        pointObject.translate(getDirection());
        return pointObject;
    }
}
