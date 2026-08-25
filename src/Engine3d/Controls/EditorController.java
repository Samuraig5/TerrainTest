package Engine3d.Controls;

import Engine3d.Rendering.Camera;
import Engine3d.Rendering.SceneRenderer;
import Engine3d.Scene;
import Engine3d.Time.Updatable;
import Math.Raycast.Ray;
import Math.Raycast.RayTriangle;
import Math.Vector.Vector3D;
import Engine3d.Objects.Object3D;
import Physics.AABBCollisions.AABB;
import Physics.PlayerObject;

import javax.swing.SwingUtilities;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class EditorController extends Controller implements Updatable {
    private final Scene scene;
    private final SceneRenderer renderer;
    private final Camera camera;      // rendering cam, for rays
    private final CreativeCamera cam; // mover

    private final float stepSize = 8f, boost = 4f;
    private final float sensitivity = 0.3f;
    private final double moveStep = 0.5f;

    private boolean w, a, s, d, up, down, ctrl, shift;
    private java.awt.Robot robot;
    private boolean recentering = false;


    public EditorController(SceneRenderer r, CreativeCamera cam, Scene scene, Camera camera) {
        super(r);
        this.cam = cam; this.scene = scene; this.camera = camera;
        this.renderer = r;
        try { robot = new java.awt.Robot(); } catch (java.awt.AWTException ex) { ex.printStackTrace(); }
        attachTranslatable(cam);
        attachRotatable(cam);
    }

    @Override public void update(double dt) {
        if (!isEnabled()) return;
        double step = stepSize * dt * (shift ? boost : 1);
        Vector3D m = new Vector3D(0,0,0);
        if (w)    m = m.translated(new Vector3D(0,0, step));
        if (s)    m = m.translated(new Vector3D(0,0,-step));
        if (a)    m = m.translated(new Vector3D( step,0,0));
        if (d)    m = m.translated(new Vector3D(-step,0,0));
        if (up)   m = m.translated(new Vector3D(0, step,0));
        if (down) m = m.translated(new Vector3D(0,-step,0));
        cam.localTranslate(m);
    }

    @Override public void keyPressed(KeyEvent e) {
        if (!isEnabled()) return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> w = true;
            case KeyEvent.VK_A -> a = true;
            case KeyEvent.VK_S -> s = true;
            case KeyEvent.VK_D -> d = true;
            case KeyEvent.VK_SPACE   -> up = true;
            case KeyEvent.VK_C   -> down = true;
            case KeyEvent.VK_SHIFT -> shift = true;
            case KeyEvent.VK_CONTROL -> ctrl = true;
        }
    }

    @Override public void keyReleased(KeyEvent e) {   // NOT gated by isEnabled, so keys can't stick
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> w = false;
            case KeyEvent.VK_A -> a = false;
            case KeyEvent.VK_S -> s = false;
            case KeyEvent.VK_D -> d = false;
            case KeyEvent.VK_SPACE   -> up = false;
            case KeyEvent.VK_C   -> down = false;
            case KeyEvent.VK_SHIFT -> shift = false;
            case KeyEvent.VK_CONTROL -> ctrl = false;
        }
    }

    @Override public void mousePressed(MouseEvent e) {
        if (!isEnabled()) return;
        if (SwingUtilities.isLeftMouseButton(e)) {
            var buf = camera.getScreenBuffer().getBufferedImage();
            Ray ray = camera.rayFromBuffer(buf.getWidth()/2.0, buf.getHeight()/2.0);
            scene.enqueueEdit(() -> scene.setSelected(pick(ray)));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e)   {
        look(e);
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        look(e);
    }

    private void look(MouseEvent e) {
        if (!isEnabled() || recentering) return;              // ignore the Robot's own move
        java.awt.Point center = new java.awt.Point(renderer.getWidth()/2, renderer.getHeight()/2);
        SwingUtilities.convertPointToScreen(center, renderer);
        int dx = e.getXOnScreen() - center.x;
        int dy = e.getYOnScreen() - center.y;
        cam.rotate(new Vector3D(Math.toRadians(dy) * sensitivity,
                Math.toRadians(-dx) * sensitivity, 0));   // same mapping as player
        recentering = true;
        robot.mouseMove(center.x, center.y);
        recentering = false;
    }

    private Object3D pick(Ray ray) {
        Object3D best = null; double bestT = Double.POSITIVE_INFINITY;
        for (Object3D o : scene.getObjects()) {
            if (o instanceof PlayerObject) continue;   // skip camera/player bodies
            if (o.getMesh() == null) continue;
            AABB box = o.getMesh().getWorldAABB(o.getPosition(), o.getRotation(), o.getScale());
            if (box == null) continue;
            double tHit = RayTriangle.intersectAABB(ray.getOrigin(), ray.getDirection(), box.min(), box.max());
            if (!Double.isNaN(tHit) && tHit < bestT) { bestT = tHit; best = o; }
        }
        return best;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (!isEnabled()) {
            return;
        }
        Object3D sel = scene.getSelected();
        if (sel == null || sel instanceof PlayerObject) {
            return;
        }

        // Axis of the world that best lines up with where the editor camera is looking
        Vector3D axis = camera.getDirection().dominantAxis();

        // Wheel up = negative rotation => push away (+axis); wheel down => pull closer
        double amount = -e.getPreciseWheelRotation() * moveStep;
        Vector3D delta = axis.scaled(amount);

        scene.enqueueEdit(() -> sel.translate(delta));
    }
}