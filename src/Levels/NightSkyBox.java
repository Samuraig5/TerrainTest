package Levels;

import Engine3d.Rendering.Camera;
import Engine3d.Rendering.ScreenDrawing.ScreenBuffer;
import Engine3d.Rendering.SkyBox;
import Math.Vector.Vector3D;
import java.util.Random;

public class NightSkyBox implements SkyBox {
    private final Vector3D moonDir;
    private final double   moonCos;
    private final Vector3D[] stars;
    private final double fovRad = Math.toRadians(90);

    public NightSkyBox(Vector3D moonDir, double moonRadiusDeg, int starCount, long seed) {
        this.moonDir = moonDir.normalized();
        this.moonCos = Math.cos(Math.toRadians(moonRadiusDeg));
        this.stars   = generateStars(starCount, seed);
    }

    private static Vector3D[] generateStars(int n, long seed) {
        Random rng = new Random(seed);
        Vector3D[] out = new Vector3D[n];
        int i = 0;
        while (i < n) {                                   // rejection-sample a uniform sphere
            double x = rng.nextDouble()*2-1, y = rng.nextDouble()*2-1, z = rng.nextDouble()*2-1;
            double m2 = x*x + y*y + z*z;
            if (m2 < 1e-4 || m2 > 1) continue;
            double m = Math.sqrt(m2);
            double sy = y/m;
            if (sy < -0.05) continue;                     // keep them mostly above the horizon
            out[i++] = new Vector3D(x/m, sy, z/m);
        }
        return out;
    }

    @Override
    public void render(ScreenBuffer buffer, Camera camera) {
        int W = buffer.width(), H = buffer.height();
        int[] color = buffer.colourArray();

        // Right-handed basis (worldUp × F) — this is the "source fix" for the mirror issue,
        // so DON'T also negate ndcX like the temporary fix did.
        Vector3D F     = camera.getDirection().normalized();
        Vector3D right = new Vector3D(0,1,0).crossProduct(F).normalized();
        Vector3D up    = F.crossProduct(right).normalized();

        double tanHalf = Math.tan(fovRad/2);
        double aspect  = (double)H / W;

        // --- gradient + moon, per pixel ---
        for (int r = 0; r < H; r++) {
            double ndcY = 1 - 2.0*(r+0.5)/H;
            for (int c = 0; c < W; c++) {
                double ndcX = 2.0*(c+0.5)/W - 1;
                Vector3D dir = F
                        .translated(right.scaled(ndcX * tanHalf / aspect))
                        .translated(up.scaled(ndcY * tanHalf))
                        .normalized();
                color[r*W + c] = shadeSky(dir);
            }
        }

        // --- stars: project fixed world directions (stable, no flicker) ---
        for (Vector3D s : stars) {
            double cF = s.dotProduct(F);
            if (cF <= 1e-4) continue;                     // behind the camera
            if (s.dotProduct(moonDir) > 0.9) continue;    // not over the moon
            double ndcX = (s.dotProduct(right) / cF) * aspect / tanHalf;
            double ndcY = (s.dotProduct(up)    / cF) / tanHalf;
            if (ndcX < -1 || ndcX > 1 || ndcY < -1 || ndcY > 1) continue;
            int col = (int)((ndcX + 1) * 0.5 * W);
            int row = (int)((1 - ndcY) * 0.5 * H);
            if (col < 0 || col >= W || row < 0 || row >= H) continue;
            color[row*W + col] = 0xffffffff;
        }
    }

    private int shadeSky(Vector3D dir) {
        double h = Math.max(0, dir.y());
        double g = Math.pow(1 - h, 2.4);
        int rr = (int)(150*g)+15, gg = (int)(8*g), bb = (int)(10*g);
        double glow = Math.exp(-h*16);
        rr += (int)(130*glow); gg += (int)(15*glow); bb += (int)(18*glow);
        double d = dir.dotProduct(moonDir);
        if (d > moonCos) { rr = gg = bb = 255; }
        else {
            double mg = Math.pow(Math.max(0, d), 90);
            rr += (int)(255*mg); gg += (int)(90*mg); bb += (int)(90*mg);
        }
        return 0xff000000 | (Math.min(255,rr)<<16) | (Math.min(255,gg)<<8) | Math.min(255,bb);
    }
}