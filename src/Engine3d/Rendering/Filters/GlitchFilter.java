package Engine3d.Rendering.Filters;

import Engine3d.Rendering.ScreenDrawing.ScreenBuffer;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class GlitchFilter implements ScreenFilter, KeyListener {
    private volatile boolean active = false;
    private volatile double startTime;
    private final double duration = 0.45;
    private final Random rng = new Random();

    @Override
    public void apply(ScreenBuffer buffer) {
        if (!active) return;
        double p = (System.nanoTime() * 1e-9 - startTime) / duration;
        if (p >= 1) { active = false; return; }

        double intensity = 1 - p;                       // decays to nothing
        int W = buffer.width(), H = buffer.height();
        int[] c = buffer.colourArray();
        int[] src = c.clone();

        int shift = (int)(12 * intensity);
        for (int y = 0; y < H; y++) {
            int slice = (rng.nextDouble() < 0.15 * intensity)
                    ? (int)((rng.nextDouble()*2 - 1) * 20 * intensity) : 0;
            for (int x = 0; x < W; x++) {
                int r = (src[y*W + clamp(x + slice + shift, W)] >> 16) & 0xff;
                int g = (src[y*W + clamp(x + slice,         W)] >>  8) & 0xff;
                int b =  src[y*W + clamp(x + slice - shift, W)]        & 0xff;
                c[y*W + x] = 0xff000000 | (r<<16) | (g<<8) | b;
            }
        }
    }

    private static int clamp(int v, int W) { return v < 0 ? 0 : (v >= W ? W-1 : v); }

    @Override public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F) {
            startTime = System.nanoTime() * 1e-9;
            active = true;
        }
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}