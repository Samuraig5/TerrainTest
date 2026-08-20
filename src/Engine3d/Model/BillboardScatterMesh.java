package Engine3d.Model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import Engine3d.Lighting.LightSource;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.DrawInstructions;
import Engine3d.Rendering.Material;
import Math.Vector.Vector2D;
import Math.Vector.Vector3D;
import Math.Matrix4x4;
import Math.MeshTriangle;

public class BillboardScatterMesh extends Mesh{
    public record Instance(Vector3D position, double width, double height) { }
    public record Wind(Vector3D direction, double amplitude, double frequency, double waveLength) { }

    private final List<Instance> instances = new ArrayList<>();
    private final BufferedImage sprite;
    private Wind wind = new Wind(new Vector3D(0,0,0),0,0,0);

    public BillboardScatterMesh(BufferedImage sprite) {
        super();
        this.sprite = sprite;
        setDrawInstructions(
                new DrawInstructions(false,false,true,false));
    }

    public void add(Instance i) {
        instances.add(i);
    }
    public void add(Wind wind) { this.wind = wind; }

    @Override
    public ProjectedTriangles computeGeometry(Vector3D position, Vector3D rotation,
                                              Camera camera, Vector3D cameraPos,
                                              Matrix4x4 vieMatrix, List<LightSource> lightSources) {
        rebuildQuads(camera);
        return super.computeGeometry(position,rotation,camera,cameraPos,vieMatrix,lightSources);
    }

    private void rebuildQuads(Camera camera) {
        double time = System.nanoTime() * 1e-9;

        Vector3D f = camera.getDirection();
        Vector3D right = new Vector3D(-f.z(), 0, f.x());
        if (right.magnitude() < 1e-6) right = Vector3D.RIGHT();   // camera looking straight up/down
        right = right.normalized();

        List<Vector3D> newPoints = new ArrayList<>(instances.size() * 4);
        List<MeshTriangle> newFaces = new ArrayList<>(instances.size() * 2);

        for (Instance in : instances) {
            double hw = in.width() * 0.5;
            double h  = in.height();
            double rx = right.x() * hw, rz = right.z() * hw;   // right.y() is 0
            Vector3D p = in.position();

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
            t1.setMaterial(makeMaterial(new Vector2D(0, 1), new Vector2D(1, 1), new Vector2D(0, 0)));
            newFaces.add(t1);

            MeshTriangle t2 = new MeshTriangle(tl, br, tr);
            t2.setMaterial(makeMaterial(new Vector2D(0, 0), new Vector2D(1, 1), new Vector2D(1, 0)));
            newFaces.add(t2);
        }

        this.points = newPoints;   // protected in Mesh
        this.faces  = newFaces;    // assigning an ArrayList is fine — field is declared as List
    }

    private Material makeMaterial(Vector2D t0, Vector2D t1, Vector2D t2) {
        Material m = new Material(t0, t1, t2);
        m.setTexture(sprite);           // TileRasterizer skips the texture branch if this is null
        m.setBaseColour(Color.WHITE);   // diffuse must be white, or mul(texel, diffuse) darkens the sprite
        return m;
    }
}
