package Engine3d.Rendering.Filters;

import Engine3d.Rendering.Camera;
import Engine3d.Rendering.ScreenDrawing.ScreenBuffer;
import Math.Vector.Vector3D;

public class CensorFilter implements ScreenFilter {
    private final Camera camera;
    private final Vector3D facePos;      // world position of the statue's face
    private final double halfW, halfH;   // world half-size of the censored region
    private final int barColor = 0xff000000;   // solid black

    public CensorFilter(Camera camera, Vector3D facePos, double halfW, double halfH) {
        this.camera = camera;
        this.facePos = facePos;
        this.halfW = halfW;
        this.halfH = halfH;
    }

    @Override
    public void apply(ScreenBuffer buffer, Vector3D camPos, Vector3D camDir) {
        int W = buffer.width(), H = buffer.height();
        int[] c = buffer.colourArray();

        // same basis/projection as the skybox star projection — stays aligned with the render
        Vector3D right = new Vector3D(0,1,0).crossProduct(camDir).normalized();
        Vector3D up    = camDir.crossProduct(right).normalized();

        double tanHalf = Math.tan(Math.toRadians(90) / 2.0);   // your fov
        double aspect  = (double) H / W;

        Vector3D rel = facePos.translated(camPos.inverted());
        double cF = rel.dotProduct(camDir);
        if (cF <= 0.01) return;                                 // behind the camera

        double ndcX = (rel.dotProduct(right) / cF) * aspect / tanHalf;
        double ndcY = (rel.dotProduct(up)    / cF) / tanHalf;

        int cx = (int)((ndcX + 1) * 0.5 * W);
        int cy = (int)((1 - ndcY) * 0.5 * H);

        // pixel half-extents shrink with distance (attached feel)
        int hw = (int)((halfW / cF) * (aspect / tanHalf) * 0.5 * W);
        int hh = (int)((halfH / cF) * (1.0    / tanHalf) * 0.5 * H);

        int x0 = Math.max(0, cx - hw), x1 = Math.min(W, cx + hw);
        int y0 = Math.max(0, cy - hh), y1 = Math.min(H, cy + hh);
        for (int y = y0; y < y1; y++)
            for (int x = x0; x < x1; x++)
                c[y*W + x] = barColor;
    }
}