package Engine3d.Rendering.Skyboxes;

import Engine3d.Rendering.Camera;
import Engine3d.Rendering.ScreenDrawing.ScreenBuffer;
import Math.Vector.Vector3D;

import java.awt.image.BufferedImage;
import java.util.Random;

import static Math.Vector.Vector3D.lerp;

public class NightSkyBox implements SkyBox {
    private final Vector3D moonDir;
    private final double   moonCos;
    private final Vector3D[] stars;
    private final double fovRad = Math.toRadians(90);
    private final int[] signPixels;
    private final int signW, signH;
    private final double irisCos, sinIris, sinDiscR;
    private volatile double gaze = 0;                       // 0 = away, 1 = facing camera
    private final Vector3D gazeStart = new Vector3D(0.45, 0.55, -0.70).normalized(); // eye-local look-away
    private Vector3D eRight, eUp, gLocal, irisRight, irisUp;

    public NightSkyBox(Vector3D moonDir, double moonRadiusDeg, int starCount, long seed,
                       BufferedImage sign, double irisRadiusDeg) {
        this.moonDir = moonDir.normalized();
        this.moonCos = Math.cos(Math.toRadians(moonRadiusDeg));
        this.stars   = generateStars(starCount, seed);
        this.signW = sign.getWidth(); this.signH = sign.getHeight();
        this.signPixels = sign.getRGB(0, 0, signW, signH, null, 0, signW);
        this.irisCos = Math.cos(Math.toRadians(irisRadiusDeg));
        this.sinIris = Math.sqrt(1 - irisCos*irisCos);
        this.sinDiscR = Math.sqrt(1 - moonCos*moonCos);       // sin of the disc's angular radius
    }

    public void setGaze(double t) { this.gaze = Math.max(0, Math.min(1, t)); }

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
        eRight = new Vector3D(0,1,0).crossProduct(moonDir).normalized();
        eUp    = moonDir.crossProduct(eRight).normalized();
        gLocal = lerp(gazeStart, new Vector3D(0,0,1), gaze).normalized();   // eye-local (right, up, toward-cam)
        Vector3D refUp = new Vector3D(0,1,0);
        irisRight = refUp.crossProduct(gLocal).normalized();
        irisUp    = gLocal.crossProduct(irisRight).normalized();

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
        if (d > moonCos) {
            return eyeColor(dir);          // on the disc → eyeball
        }
        double mg = Math.pow(Math.max(0, d), 90);       // keep the outer glow
        rr += (int)(255*mg); gg += (int)(90*mg); bb += (int)(90*mg);
        return 0xff000000 | (Math.min(255,rr)<<16) | (Math.min(255,gg)<<8) | Math.min(255,bb);
    }

    private int eyeColor(Vector3D dir) {
        double x = dir.dotProduct(eRight) / sinDiscR;   // disc-local coords in [-1,1]
        double y = dir.dotProduct(eUp)    / sinDiscR;
        double r2 = x*x + y*y;
        if (r2 > 1) return 0xffEFE8EA;                   // rim safety
        double z = Math.sqrt(1 - r2);
        Vector3D p = new Vector3D(x, y, z);              // surface point, eye-local

        if (p.dotProduct(gLocal) > irisCos) {            // inside the iris cap
            double u = p.dotProduct(irisRight) / sinIris * 0.5 + 0.5;
            double v = -p.dotProduct(irisUp)    / sinIris * 0.5 + 0.5;
            int sx = clampi((int)(u*signW), signW), sy = clampi((int)(v*signH), signH);
            int texel = signPixels[sy*signW + sx];
            if ((texel >>> 24) != 0) return 0xff000000 | (texel & 0xffffff);  // Sign pixel
        }
        return 0xffEFE8EA;                                // sclera (soft off-white)
    }
    private static int clampi(int v, int n) { return v < 0 ? 0 : (v >= n ? n-1 : v); }
}