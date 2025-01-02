package Rendering;

import WorldSpace.Triangle;
import WorldSpace.Vector3D;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.abs;

public class Texturizer
{
    public static void textureTriangle(Graphics2D g, int screenHeight,
                                       int x1, int y1, float u1, float v1, float w1,
                                       int x2, int y2, float u2, float v2, float w2,
                                       int x3, int y3, float u3, float v3, float w3,
                                       BufferedImage sprite)
    {
        int tempi;
        float tempf;
        if (y2 < y1)
        {
            tempi = y1; y1 = y2; y2 = tempi;
            tempi = x1; x1 = x2; x2 = tempi;
            tempf = u1; u1 = u2; u2 = tempf;
            tempf = v1; v1 = v2; v2 = tempf;
            tempf = w1; w1 = w2; w2 = tempf;
        }
        if (y3 < y1)
        {
            tempi = y1; y1 = y3; y3 = tempi;
            tempi = x1; x1 = x3; x3 = tempi;
            tempf = u1; u1 = u3; u3 = tempf;
            tempf = v1; v1 = v3; v3 = tempf;
            tempf = w1; w1 = w3; w3 = tempf;
        }
        if (y3 < y2)
        {
            tempi = y2; y2 = y3; y3 = tempi;
            tempi = x2; x2 = x3; x3 = tempi;
            tempf = u2; u2 = u3; u3 = tempf;
            tempf = v2; v2 = v3; v3 = tempf;
            tempf = w2; w2 = w3; w3 = tempf;
        }

        int dy1 = y2 - y1;
        int dx1 = x2 - x1;
        float dv1 = v2 - v1;
        float du1 = u2 - u1;
        float dw1 = w2 - w1;

        int dy2 = y3 - y1;
        int dx2 = x3 - x1;
        float dv2 = v3 - v1;
        float du2 = u3 - u1;
        float dw2 = w3 - w1;

        float tex_u, tex_v, tex_w;

        float dax_step = 0, dbx_step = 0,
                du1_step = 0, dv1_step = 0,
                du2_step = 0, dv2_step = 0,
                dw1_step=0, dw2_step=0;

        if (dy1 != 0) dax_step = dx1 / (float)abs(dy1);
        if (dy2 != 0) dbx_step = dx2 / (float)abs(dy2);

        if (dy1 != 0) du1_step = du1 / (float)abs(dy1);
        if (dy1 != 0) dv1_step = dv1 / (float)abs(dy1);
        if (dy1 != 0) dw1_step = dw1 / (float)abs(dy1);

        if (dy2 != 0) du2_step = du2 / (float)abs(dy2);
        if (dy2 != 0) dv2_step = dv2 / (float)abs(dy2);
        if (dy2 != 0) dw2_step = dw2 / (float)abs(dy2);

        if (dy1 != 0)
        {
            for (int i = y1; i <= y2; i++)
            {
                int ax = (int) (x1 + (float)(i - y1) * dax_step);
                int bx = (int) (x1 + (float)(i - y1) * dbx_step);

                float tex_su = u1 + (float)(i - y1) * du1_step;
                float tex_sv = v1 + (float)(i - y1) * dv1_step;
                float tex_sw = w1 + (float)(i - y1) * dw1_step;

                float tex_eu = u1 + (float)(i - y1) * du2_step;
                float tex_ev = v1 + (float)(i - y1) * dv2_step;
                float tex_ew = w1 + (float)(i - y1) * dw2_step;

                if (ax > bx)
                {
                    tempi = ax; ax = bx; bx = tempi;
                    tempf = tex_su; tex_su = tex_eu; tex_eu = tempf;
                    tempf = tex_sv; tex_sv = tex_ev; tex_ev = tempf;
                    tempf = tex_sw; tex_sw = tex_ew; tex_ew = tempf;
                }

                tex_u = tex_su;
                tex_v = tex_sv;
                tex_w = tex_sw;

                float tstep = 1.0f / ((float)(bx - ax));
                float t = 0.0f;

                for (int j = ax; j < bx; j++)
                {
                    tex_u = (1.0f - t) * tex_su + t * tex_eu;
                    tex_v = (1.0f - t) * tex_sv + t * tex_ev;
                    /*tex_w = (1.0f - t) * tex_sw + t * tex_ew;
                    if (tex_w > pDepthBuffer[i*ScreenWidth() + j])
                    {
                        Draw(j, i, tex->SampleGlyph(tex_u / tex_w, tex_v / tex_w), tex->SampleColour(tex_u / tex_w, tex_v / tex_w));
                        pDepthBuffer[i*ScreenWidth() + j] = tex_w;
                    }*/

                    Color c = sampleSprite(sprite, tex_u, tex_v);
                    drawPixel(g, screenHeight, c, j, i);

                    t += tstep;
                }
            }
        }

        dy1 = y3 - y2;
        dx1 = x3 - x2;
        dv1 = v3 - v2;
        du1 = u3 - u2;
        dw1 = w3 - w2;

        if (dy1 != 0) dax_step = dx1 / (float)abs(dy1);
        if (dy2 != 0) dbx_step = dx2 / (float)abs(dy2);

        du1_step = 0; dv1_step = 0;
        if (dy1 != 0) du1_step = du1 / (float)abs(dy1);
        if (dy1 != 0) dv1_step = dv1 / (float)abs(dy1);
        if (dy1 != 0) dw1_step = dw1 / (float)abs(dy1);

        if (dy1 != 0)
        {
            for (int i = y2; i <= y3; i++)
            {
                int ax = (int) (x2 + (float)(i - y2) * dax_step);
                int bx = (int) (x1 + (float)(i - y1) * dbx_step);

                float tex_su = u2 + (float)(i - y2) * du1_step;
                float tex_sv = v2 + (float)(i - y2) * dv1_step;
                float tex_sw = w2 + (float)(i - y2) * dw1_step;

                float tex_eu = u1 + (float)(i - y1) * du2_step;
                float tex_ev = v1 + (float)(i - y1) * dv2_step;
                float tex_ew = w1 + (float)(i - y1) * dw2_step;

                if (ax > bx)
                {
                    tempi = ax; ax = bx; bx = tempi;
                    tempf = tex_su; tex_su = tex_eu; tex_eu = tempf;
                    tempf = tex_sv; tex_sv = tex_ev; tex_ev = tempf;
                    tempf = tex_sw; tex_sw = tex_ew; tex_ew = tempf;
                }

                tex_u = tex_su;
                tex_v = tex_sv;
                tex_w = tex_sw;

                float tstep = 1.0f / ((float)(bx - ax));
                float t = 0.0f;

                for (int j = ax; j < bx; j++)
                {
                    tex_u = (1.0f - t) * tex_su + t * tex_eu;
                    tex_v = (1.0f - t) * tex_sv + t * tex_ev;
                    tex_w = (1.0f - t) * tex_sw + t * tex_ew;

                    /*if (tex_w > pDepthBuffer[i*ScreenWidth() + j])
                    {
                        Draw(j, i, tex->SampleGlyph(tex_u / tex_w, tex_v / tex_w), tex->SampleColour(tex_u / tex_w, tex_v / tex_w));
                        pDepthBuffer[i*ScreenWidth() + j] = tex_w;
                    }
                     */

                    Color c = sampleSprite(sprite, tex_u, tex_v);
                    drawPixel(g, screenHeight, c, j, i);

                    t += tstep;
                }
            }
        }
    }

    private static void drawPixel(Graphics2D g, int screenHeight, Color c, int x, int y)
    {
        y = screenHeight - y;
        g.setColor(c);
        g.fillRect(x, y, 1, 1);
    }

    private static Color sampleSprite(BufferedImage sprite, double u, double v)
    {
        u *= sprite.getWidth();
        v *= sprite.getHeight();

        if (u >= 0 && u < sprite.getWidth() && v >= 0 && v < sprite.getHeight()) {
            int rgb = sprite.getRGB((int)u, (int)v);
            return new Color(rgb);
        } else {
            System.err.println("Drawer: u (" + u + ") or v (" + v + ") are out of bounds of the sprite!");
            return Color.magenta;
        }
    }
}
