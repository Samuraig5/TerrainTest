package Engine3d.Rendering.Filters;

import Engine3d.Rendering.ScreenDrawing.ScreenBuffer;
import Math.Vector.Vector3D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlackScreenText implements ScreenFilter {
    public record Span(String text, Color color) {}

    private volatile List<List<Span>> lines = List.of();
    private volatile boolean active = false;
    private final Random rng = new Random();

    private Color defaultColor = new Color(255, 255, 255);
    private float fontSize = 16f;
    private int lineSpacing = 4;     // extra px between lines
    private ScreenFilter effect;     // optional distortion

    // simple: split on '\n', every line in the default colour
    public void show(String t) {
        List<List<Span>> ls = new ArrayList<>();
        for (String line : t.split("\n", -1))
            ls.add(List.of(new Span(line, defaultColor)));
        lines = ls;
        active = true;
    }

    // rich: pre-built coloured lines (see builder() below)
    public void show(List<List<Span>> ls) { this.lines = ls; this.active = true; }

    public void hide() { active = false; }

    // config
    public BlackScreenText fontSize(float s) { this.fontSize = s; return this; }
    public BlackScreenText defaultColor(Color c) { this.defaultColor = c; return this; }
    public BlackScreenText lineSpacing(int px) { this.lineSpacing = px; return this; }

    public BlackScreenText effect(ScreenFilter f) { this.effect = f; return this; }

    @Override
    public void apply(ScreenBuffer buffer, Vector3D camPos, Vector3D camDir) {
        if (!active) return;
        List<List<Span>> ls = lines;
        BufferedImage img = buffer.getBufferedImage();
        Graphics2D g = img.createGraphics();
        int w = buffer.width(), h = buffer.height();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);
        g.setFont(g.getFont().deriveFont(Font.BOLD, fontSize));
        FontMetrics fm = g.getFontMetrics();
        int lineH  = fm.getHeight() + lineSpacing;
        int startY = (h - ls.size() * lineH) / 2 + fm.getAscent();

        for (int li = 0; li < ls.size(); li++) {
            List<Span> line = ls.get(li);
            int lineWidth = 0;
            for (Span s : line) lineWidth += fm.stringWidth(s.text());
            int x = (w - lineWidth) / 2;                          // no jitter — clean
            int y = startY + li * lineH;
            for (Span s : line) {
                g.setColor(s.color());
                g.drawString(s.text(), x, y);
                x += fm.stringWidth(s.text());
            }
        }
        g.dispose();

        if (effect != null) effect.apply(buffer, camPos, camDir);  // distort the black+text
    }

    private static Color dim(Color c) {
        return new Color(c.getRed() / 3, c.getGreen() / 3, c.getBlue() / 3);
    }

    // fluent builder for coloured, multi-line text
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final List<List<Span>> lines = new ArrayList<>();
        private List<Span> current = new ArrayList<>();
        public Builder add(String text, Color color) { current.add(new Span(text, color)); return this; }
        public Builder newLine() { lines.add(current); current = new ArrayList<>(); return this; }
        public List<List<Span>> build() { lines.add(current); return lines; }
    }
}