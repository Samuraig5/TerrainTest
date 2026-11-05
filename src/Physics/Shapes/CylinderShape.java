package Physics.Shapes;

import Engine3d.Model.Mesh;
import Engine3d.Objects.Object3D;
import Math.Vector.Vector3D;
import Physics.AABBCollisions.AABB;

public class CylinderShape implements ConvexShape {

    private final Object3D owner;
    private final double radius;
    private final double height;

    public CylinderShape(Object3D owner, double radius, double height) {
        this.owner = owner;
        this.radius = radius;
        this.height = height;
    }

    @Override
    public Vector3D support(Vector3D d) {
        double h = Math.hypot(d.x(), d.z());

        Vector3D support = new Vector3D(0,0,0);
        if (h > 1e-9) {
            support = support.translated(new Vector3D(d.x()/h*radius, 0, d.z()/h*radius));
        }
        if (d.y() > 0) {
            support = support.translated(new Vector3D(0,height,0));
        }

        return owner.getPosition().translated(support);
    }

    @Override
    public Vector3D center() {
        return owner.getPosition().translated(new Vector3D(0,height/2,0));
    }

    @Override
    public AABB bounds() {
        return new AABB(
                owner.getPosition().translated(new Vector3D(radius,0,radius).inverted()),
                        owner.getPosition().translated(new Vector3D(radius,height,radius))
        );
    }
}
