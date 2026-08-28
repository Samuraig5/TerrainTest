package Physics;

import Engine3d.DevTools.Log;
import Engine3d.DevTools.Profiler;
import Engine3d.Scene;
import Math.Geometries.MeshTriangle;
import Math.Raycast.RayTriangle;
import Math.Vector.Vector3D;
import Physics.AABBCollisions.DynamicAABBObject;
import Physics.AABBCollisions.StaticAABBObject;

import java.util.List;

public class PhysicsSystem {
    public void step(Scene scene, double deltaTime) {
        try (Profiler.Span s = Profiler.span("applyGravity")) {
            for (Gravitational grav : scene.getGravitationals()) {
                grav.applyGravity(scene.getGravity(), deltaTime);
            }
        }
        catch (Exception e) {
            Log.println("Error when applying gravity: " + e.getMessage());
        }

        try (Profiler.Span s = Profiler.span("handleCollision")) {
            handleCollision(scene);
        }
        catch (Exception e) {
            Log.println("Error when handling collisions: " + e.getMessage());
        }
    }

    private void handleCollision(Scene scene) {
        List<DynamicAABBObject> dyn = scene.getDynamicAABBObjects();
        List<StaticAABBObject> stat = scene.getStaticAABBObjects();
        for (int i = 0; i < dyn.size(); i++) {
            for (int j = 0; j < stat.size(); j++) {
                dyn.get(i).
                        getAABBCollider().handleCollision(
                                stat.get(j).getAABBCollider());
            }
            for (int j = i+1; j < dyn.size(); j++) {
                dyn.get(i).
                        getAABBCollider().handleCollision(
                                dyn.get(j).getAABBCollider());
            }
        }
    }

    public double groundDistanceBelow(Scene scene, Vector3D origin) {
        Vector3D dir = Vector3D.DOWN();     // unit, so the result is a real distance
        double best = Double.POSITIVE_INFINITY;

        for (StaticAABBObject s : scene.getStaticAABBObjects()) {
            for (MeshTriangle tri : s.getMesh().getFacesInWorld(s.getPosition(), s.getRotation())) {
                Vector3D[] p = tri.getPoints();
                double t = RayTriangle.intersect(origin, dir, p[0], p[1], p[2]);
                if (!Double.isNaN(t) && t < best) best = t;
            }
        }
        return best;
    }
}
