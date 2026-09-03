package Engine3d.Rendering;

import Engine3d.DevTools.Log;
import Engine3d.DevTools.Profiler;
import Engine3d.Lighting.LightSource;
import Engine3d.Model.Mesh;
import Engine3d.Model.SimpleMeshes.BoxMesh;
import Engine3d.Objects.Object3D;
import Engine3d.Rendering.Filters.ScreenFilter;
import Engine3d.Rendering.ScreenDrawing.TileRasterizer;
import Engine3d.Scene;
import Math.Geometries.Box;
import Math.Vector.Vector3D;
import Math.Matrix4x4;
import Physics.AABBCollisions.AABB;
import Physics.AABBCollisions.AABBObject;
import Physics.PlayerObject;
import Physics.Triggers.TriggerZone;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RenderPipeline {
    private final TileRasterizer tileRasterizer = new TileRasterizer();

    public void build(Camera camera, Scene scene, List<RenderItem> frame) {
        camera.getScreenBuffer().clear(scene.getBackgroundColour());
        List<LightSource> lightSources = scene.getLightSources();

        //These values are purely based off the camera.
        //If they change between two objects on the same frame then the objects can "jitter"
        //This is also slightly more efficient.
        Vector3D constCamPos = new Vector3D(camera.getPosition());
        Vector3D constCamDir = new Vector3D(camera.getDirection());
        Vector3D up = new Vector3D(0,1,0);
        Vector3D target = camera.getDirection().translated(constCamPos);
        Matrix4x4 cameraMatrix = Matrix4x4.getPointAtMatrix(constCamPos, target, up);
        Matrix4x4 viewMatrix = cameraMatrix.quickMatrixInverse();

        Matrix4x4 viewProj = Matrix4x4.matrixMatrixMultiplication(viewMatrix, camera.getProjectionMatrix());
        Frustum frustum = new Frustum(viewProj);

        frame.sort((o1, o2) -> {
            // Calculate distances to the camera
            double distance1 = o1.position().distanceTo(constCamPos);
            double distance2 = o2.position().distanceTo(constCamPos);
            // Sort objects by distance (closer first)
            return Double.compare(distance1, distance2);
        });

        // Compute Geometry
        List<Mesh.ProjectedTriangles> geometry;
        try (Profiler.Span s = Profiler.span("geometry")) {
            geometry = new ArrayList<>(frame.parallelStream()
                    .map(o -> o.mesh().computeGeometry(
                            o.scale(), o.position(), o.rotation(),
                            camera, constCamPos, frustum, viewMatrix,
                            lightSources))
                    .toList());   // wrap in ArrayList so we can add to it
        }
        catch (Exception e) {
            Log.println("Error when computing geometry: " + e.getMessage());
            return;
        }

        // Draw box around selected object
        Object3D sel = scene.getSelected();
        if (sel != null && sel.getMesh() != null) {
            AABB hb = sel.getMesh().getWorldAABB(sel.getPosition(), sel.getRotation(), sel.getScale());
            if (hb != null) geometry.add(debugBox(hb, Color.YELLOW, camera, constCamPos, viewMatrix, frustum));
        }

        if (camera.debugging) {
            for (AABBObject o : scene.getAABBObjects()) {
                if (o instanceof PlayerObject) {
                    continue;
                }
                geometry.add(debugBox(o.getAABBCollider().getAABB(), Color.WHITE, camera, constCamPos, viewMatrix, frustum));
            }
            for (TriggerZone t : scene.getTriggers()) {
                geometry.add(debugBox(t.getRegion(), Color.ORANGE, camera, constCamPos, viewMatrix, frustum));
            }
        }

        try (Profiler.Span s = Profiler.span("raster")) {
            tileRasterizer.render(camera, geometry, scene.getBackgroundColour(), scene.getSkybox());
        }
        catch (Exception e) {
            Log.println("Error when rasterizing objects: " + e.getMessage());
        }

        try (Profiler.Span s = Profiler.span("filters")) {
            for (ScreenFilter f : scene.getFilters()) {
                f.apply(camera.getScreenBuffer(), constCamPos, constCamDir);
            }
            scene.getFilters().removeIf(ScreenFilter::isDone);
        }
        catch (Exception e) {
            Log.println("Error when applying filters: " + e.getMessage());
        }
    }

    private Mesh.ProjectedTriangles debugBox(AABB box, Color color, Camera camera,
                                             Vector3D camPos, Matrix4x4 viewMatrix, Frustum frustum) {
        BoxMesh mesh = new BoxMesh(new Box(box.min(), box.max()));   // built at world min/max
        DrawInstructions di = new DrawInstructions(true, false, false, false); // wireframe only
        di.wireFrameColour = color;
        di.ignorePixelDepth = true;                                  // draw on top, see through walls
        mesh.setDrawInstructions(di);
        return mesh.computeGeometry(
                new Vector3D(1,1,1), new Vector3D(0,0,0), new Vector3D(0,0,0),
                camera, camPos, frustum, viewMatrix, new ArrayList<>());
    }
}
