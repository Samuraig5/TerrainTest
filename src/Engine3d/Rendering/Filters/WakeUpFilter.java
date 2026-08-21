package Engine3d.Rendering.Filters;

import Engine3d.Rendering.ScreenDrawing.ScreenBuffer;
import Math.Vector.Vector3D;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class WakeUpFilter implements ScreenFilter, KeyListener {
    private enum State { ASLEEP, OPENING, DONE }
    private volatile State state = State.ASLEEP;
    private double openStart;
    private final Runnable onWake;
    private final double openDuration = 6;   // seconds

    public WakeUpFilter(Runnable onWake) {
        this.onWake = onWake;
    }

    @Override
    public void apply(ScreenBuffer buffer, Vector3D camPos, Vector3D camDir) {
        int W = buffer.width(), H = buffer.height();
        int[] color = buffer.colourArray();

        if (state == State.ASLEEP) {
            for (int i = 0; i < color.length; i++) color[i] = 0xff000000;
            drawPrompt(buffer);
        } else if (state == State.OPENING) {
            double p = (System.nanoTime() * 1e-9 - openStart) / openDuration;
            if (p >= 1) { state = State.DONE; return; }
            int bar = (int)((1 - p) * (H / 2.0));                 // shrinking black bars
            for (int y = 0; y < H; y++)
                if (y < bar || y >= H - bar)
                    for (int x = 0; x < W; x++) color[y*W + x] = 0xff000000;
        }
    }

    private void drawPrompt(ScreenBuffer buffer) {
        Graphics2D g = buffer.getBufferedImage().createGraphics();
        g.setFont(new Font("Monospaced", Font.BOLD, Math.max(10, buffer.height()/12)));
        FontMetrics fm = g.getFontMetrics();
        String a = "Press 'F' to ", b = "wake up";
        int x = (buffer.width() - fm.stringWidth(a) - fm.stringWidth(b)) / 2;
        int y = buffer.height() / 2;
        g.setColor(Color.WHITE);  g.drawString(a, x, y);
        g.setColor(Color.YELLOW); g.drawString(b, x + fm.stringWidth(a), y);
        g.dispose();
    }

    @Override public boolean isDone() { return state == State.DONE; }

    @Override public void keyPressed(KeyEvent e) {
        if (state == State.ASLEEP && e.getKeyCode() == KeyEvent.VK_F) {
            openStart = System.nanoTime() * 1e-9;
            state = State.OPENING;
            if (onWake != null) {
                onWake.run();
            }
        }
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}