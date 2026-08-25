package Engine3d.Model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import Engine3d.Lighting.LightSource;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.DrawInstructions;
import Engine3d.Rendering.Frustum;
import Engine3d.Rendering.Material;
import Math.Vector.Vector2D;
import Math.Vector.Vector3D;
import Math.Matrix4x4;
import Math.Geometries.MeshTriangle;

public class BillboardScatterMesh extends Mesh{
    public record Instance(Vector3D position, double width, double height) { }
    public record Wind(Vector3D direction, double amplitude, double frequency, double waveLength) { }

    private volatile List<Instance> instances = new ArrayList<>();
    private final BufferedImage sprite;
    private final Material matA;
    private final Material matB;
    private Wind wind = new Wind(new Vector3D(0,0,0),0,0,0);
    private double maxDrawDist = 50;

    public BillboardScatterMesh(BufferedImage sprite) {
        super();
        this.sprite = sprite;
        setDrawInstructions(
                new DrawInstructions(false,false,true,false));

        matA = makeMaterial(new Vector2D(0, 1), new Vector2D(1, 1), new Vector2D(0, 0));
        matB = makeMaterial(new Vector2D(0, 0), new Vector2D(1, 1), new Vector2D(1, 0));
    }

    private Material makeMaterial(Vector2D t0, Vector2D t1, Vector2D t2) {
        Material m = new Material(t0, t1, t2);
        m.setTexture(sprite);           // TileRasterizer skips the texture branch if this is null
        m.setBaseColour(Color.WHITE);   // diffuse must be white, or mul(texel, diffuse) darkens the sprite
        return m;
    }

    public void add(Instance i) {
        instances.add(i);
    }
    public void setInstances(List<Instance> newInstances) {
        instances = newInstances;
    }
    public void add(Wind wind) { this.wind = wind; }

    @Override
    public ProjectedTriangles computeGeometry(Vector3D scale, Vector3D position, Vector3D rotation, Camera camera, Vector3D cameraPos,
                                              Frustum frustum, Matrix4x4 viewMatrix, List<LightSource> lightSources) {
        rebuildQuads(camera, cameraPos, frustum);
        return super.computeGeometry(scale, position,rotation,camera,cameraPos, frustum, viewMatrix, lightSources);
    }

    private void rebuildQuads(Camera camera, Vector3D camPos, Frustum frustum) {
        double time = System.nanoTime() * 1e-9;

        List<Instance> snap = this.instances;

        Vector3D f = camera.getDirection();
        Vector3D right = new Vector3D(-f.z(), 0, f.x());
        if (right.magnitude() < 1e-6) right = Vector3D.RIGHT();   // camera looking straight up/down
        right = right.normalized();

        List<Vector3D>      newPoints = new ArrayList<>(snap.size() * 4);
        List<MeshTriangle>  newFaces  = new ArrayList<>(snap.size() * 2);

        for (Instance in : snap) {
            Vector3D p = in.position();

            // (a) distance cull — grass doesn't need to draw 500 units away
            double dx = p.x() - camPos.x(), dz = p.z() - camPos.z();
            if (dx*dx + dz*dz > maxDrawDist * maxDrawDist) continue;

            // (b) frustum cull — treat the quad as a sphere at its centre
            if (!frustum.isSphereVisible(p, Math.max(in.width(), in.height()))) continue;

            double hw = in.width() * 0.5;
            double h  = in.height();
            double rx = right.x() * hw, rz = right.z() * hw;   // right.y() is 0

            // Per-flower wind sway (top of the quad only)
            double phase = (p.x() + p.z()) * wind.waveLength;
            double s  = wind.amplitude * Math.sin(time * wind.frequency + phase);
            double sx = wind.direction.x() * s, sz = wind.direction.z() * s;

            // Base stays planted; keep component-wise so w stays exactly 1
            Vector3D bl = new Vector3D(p.x() - rx,      p.y(),     p.z() - rz);
            Vector3D br = new Vector3D(p.x() + rx,      p.y(),     p.z() + rz);
            Vector3D tl = new Vector3D(p.x() - rx + sx, p.y() + h, p.z() - rz + sz);
            Vector3D tr = new Vector3D(p.x() + rx + sx, p.y() + h, p.z() + rz + sz);

            newPoints.add(bl); newPoints.add(br); newPoints.add(tl); newPoints.add(tr);

            MeshTriangle t1 = new MeshTriangle(bl, br, tl);
            t1.setMaterial(matA);
            newFaces.add(t1);

            MeshTriangle t2 = new MeshTriangle(tl, br, tr);
            t2.setMaterial(matB);
            newFaces.add(t2);
        }

        this.points = newPoints;   // protected in Mesh
        this.faces  = newFaces;    // assigning an ArrayList is fine — field is declared as List
    }
}
