package Engine3d.Rendering.Filters;

import Engine3d.Rendering.ScreenDrawing.ScreenBuffer;
import Math.Vector.Vector3D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class PopupFilter implements ScreenFilter {
    public record Popup(double u, double v, String text, Color textColor) {}

    private final List<Popup> popups = new CopyOnWriteArrayList<>();
    private final Random rng = new Random();

    private float fontSize   = 11f;
    private int   pad        = 4;
    private Color boxColor    = new Color(0, 0, 0, 235);    // near-opaque dark box
    private Color borderColor = new Color(200, 30, 30);
    private Color defaultText = new Color(230, 230, 230);

    public void add(String text) { add(text, defaultText); }
    public void add(String text, Color color) {
        double u, v;
        do { u = rng.nextDouble(); v = rng.nextDouble(); }   // reject the center → edge cluster
        while (u > 0.28 && u < 0.72 && v > 0.28 && v < 0.72);
        popups.add(new Popup(u, v, text, color));
    }
    public void clear() { popups.clear(); }

    @Override
    public void apply(ScreenBuffer buffer, Vector3D camPos, Vector3D camDir) {
        if (popups.isEmpty()) return;
        BufferedImage img = buffer.getBufferedImage();
        Graphics2D g = img.createGraphics();
        int W = buffer.width(), H = buffer.height();
        g.setFont(g.getFont().deriveFont(Font.BOLD, fontSize));
        FontMetrics fm = g.getFontMetrics();

        for (Popup p : popups) {
            int boxW = fm.stringWidth(p.text()) + pad * 2;
            int boxH = fm.getHeight() + pad * 2;
            int x = (int) Math.round(p.u() * (W - boxW));    // u∈[0,1] maps across the on-screen range
            int y = (int) Math.round(p.v() * (H - boxH));

            g.setColor(boxColor);    g.fillRect(x, y, boxW, boxH);
            g.setColor(borderColor); g.drawRect(x, y, boxW - 1, boxH - 1);
            g.setColor(p.textColor());
            g.drawString(p.text(), x + pad, y + pad + fm.getAscent());
        }
        g.dispose();
    }
}