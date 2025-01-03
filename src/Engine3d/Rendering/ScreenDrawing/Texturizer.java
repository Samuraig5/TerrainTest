package Engine3d.Rendering.ScreenDrawing;

import Engine3d.Rendering.ScreenDrawing.Drawer;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.abs;

public class Texturizer
{
    public static void textureTriangle(Graphics2D g, int screenHeight, double resolution, double[][] depthBuffer,
                                       int x1, int y1, double u1, double v1, double w1,
                                       int x2, int y2, double u2, double v2, double w2,
                                       int x3, int y3, double u3, double v3, double w3,
                                       double luminance, BufferedImage sprite)
    {
        int spriteWidth = sprite.getWidth()-1;
        int spriteHeigth = sprite.getHeight()-1;

        int tempi;
        double tempd;
        if (y2 < y1)
        {
            tempi = y1; y1 = y2; y2 = tempi;
            tempi = x1; x1 = x2; x2 = tempi;
            tempd = u1; u1 = u2; u2 = tempd;
            tempd = v1; v1 = v2; v2 = tempd;
            tempd = w1; w1 = w2; w2 = tempd;
        }
        if (y3 < y1)
        {
            tempi = y1; y1 = y3; y3 = tempi;
            tempi = x1; x1 = x3; x3 = tempi;
            tempd = u1; u1 = u3; u3 = tempd;
            tempd = v1; v1 = v3; v3 = tempd;
            tempd = w1; w1 = w3; w3 = tempd;
        }
        if (y3 < y2)
        {
            tempi = y2; y2 = y3; y3 = tempi;
            tempi = x2; x2 = x3; x3 = tempi;
            tempd = u2; u2 = u3; u3 = tempd;
            tempd = v2; v2 = v3; v3 = tempd;
            tempd = w2; w2 = w3; w3 = tempd;
        }

        int dy1 = y2 - y1;
        int dx1 = x2 - x1;
        double dv1 = v2 - v1;
        double du1 = u2 - u1;
        double dw1 = w2 - w1;

        int dy2 = y3 - y1;
        int dx2 = x3 - x1;
        double dv2 = v3 - v1;
        double du2 = u3 - u1;
        double dw2 = w3 - w1;

        double tex_u, tex_v, tex_w;

        double dax_step = 0, dbx_step = 0,
                du1_step = 0, dv1_step = 0,
                du2_step = 0, dv2_step = 0,
                dw1_step=0, dw2_step=0;

        if (dy1 != 0) dax_step = dx1 / (double)abs(dy1);
        if (dy2 != 0) dbx_step = dx2 / (double)abs(dy2);

        if (dy1 != 0) du1_step = du1 / (double)abs(dy1);
        if (dy1 != 0) dv1_step = dv1 / (double)abs(dy1);
        if (dy1 != 0) dw1_step = dw1 / (double)abs(dy1);

        if (dy2 != 0) du2_step = du2 / (double)abs(dy2);
        if (dy2 != 0) dv2_step = dv2 / (double)abs(dy2);
        if (dy2 != 0) dw2_step = dw2 / (double)abs(dy2);

        if (dy1 != 0)
        {
            for (int i = y1; i <= y2; i++)
            {
                int ax = (int) (x1 + (double)(i - y1) * dax_step);
                int bx = (int) (x1 + (double)(i - y1) * dbx_step);

                double tex_su = u1 + (double)(i - y1) * du1_step;
                double tex_sv = v1 + (double)(i - y1) * dv1_step;
                double tex_sw = w1 + (double)(i - y1) * dw1_step;

                double tex_eu = u1 + (double)(i - y1) * du2_step;
                double tex_ev = v1 + (double)(i - y1) * dv2_step;
                double tex_ew = w1 + (double)(i - y1) * dw2_step;

                if (ax > bx)
                {
                    tempi = ax; ax = bx; bx = tempi;
                    tempd = tex_su; tex_su = tex_eu; tex_eu = tempd;
                    tempd = tex_sv; tex_sv = tex_ev; tex_ev = tempd;
                    tempd = tex_sw; tex_sw = tex_ew; tex_ew = tempd;
                }

                tex_u = tex_su;
                tex_v = tex_sv;
                tex_w = tex_sw;

                double tstep = 1.0f / ((double)(bx - ax));
                double t = 0.0f;

                for (int j = ax; j < bx; j++)
                {
                    tex_u = (1.0f - t) * tex_su + t * tex_eu;
                    tex_v = (1.0f - t) * tex_sv + t * tex_ev;
                    tex_w = (1.0f - t) * tex_sw + t * tex_ew;

                    drawTextureToPixel(g,screenHeight,resolution,depthBuffer,
                            sprite,luminance,spriteWidth,spriteHeigth,
                            tex_u,tex_v,tex_w,j,i);
                    t += tstep;
                }
            }
        }

        dy1 = y3 - y2;
        dx1 = x3 - x2;
        dv1 = v3 - v2;
        du1 = u3 - u2;
        dw1 = w3 - w2;

        if (dy1 != 0) dax_step = dx1 / (double)abs(dy1);
        if (dy2 != 0) dbx_step = dx2 / (double)abs(dy2);

        du1_step = 0; dv1_step = 0;
        if (dy1 != 0) du1_step = du1 / (double)abs(dy1);
        if (dy1 != 0) dv1_step = dv1 / (double)abs(dy1);
        if (dy1 != 0) dw1_step = dw1 / (double)abs(dy1);

        if (dy1 != 0)
        {
            for (int i = y2; i <= y3; i++)
            {
                int ax = (int) (x2 + (double)(i - y2) * dax_step);
                int bx = (int) (x1 + (double)(i - y1) * dbx_step);

                double tex_su = u2 + (double)(i - y2) * du1_step;
                double tex_sv = v2 + (double)(i - y2) * dv1_step;
                double tex_sw = w2 + (double)(i - y2) * dw1_step;

                double tex_eu = u1 + (double)(i - y1) * du2_step;
                double tex_ev = v1 + (double)(i - y1) * dv2_step;
                double tex_ew = w1 + (double)(i - y1) * dw2_step;

                if (ax > bx)
                {
                    tempi = ax; ax = bx; bx = tempi;
                    tempd = tex_su; tex_su = tex_eu; tex_eu = tempd;
                    tempd = tex_sv; tex_sv = tex_ev; tex_ev = tempd;
                    tempd = tex_sw; tex_sw = tex_ew; tex_ew = tempd;
                }

                tex_u = tex_su;
                tex_v = tex_sv;
                tex_w = tex_sw;

                double tstep = 1.0f / ((double)(bx - ax));
                double t = 0.0f;

                for (int j = ax; j < bx; j++)
                {
                    tex_u = (1.0f - t) * tex_su + t * tex_eu;
                    tex_v = (1.0f - t) * tex_sv + t * tex_ev;
                    tex_w = (1.0f - t) * tex_sw + t * tex_ew;

                    drawTextureToPixel(g,screenHeight,resolution,depthBuffer,
                            sprite,luminance,spriteWidth,spriteHeigth,
                            tex_u,tex_v,tex_w,j,i);

                    t += tstep;
                }
            }
        }
    }

    private static void drawTextureToPixel(Graphics2D g, int screenHeight, double resolution, double[][] depthBuffer,
                                           BufferedImage sprite, double luminance, int spriteWidth, int spriteHeigth,
                                           double tex_u, double tex_v, double tex_w,
                                           int j, int i)
    {
        if (tex_w > depthBuffer[j][i])
        {
            Color c = sampleSprite(sprite, luminance, spriteWidth, spriteHeigth,tex_u / tex_w, tex_v / tex_w);
            drawPixel(g, screenHeight, (int)(1/resolution), c, j, i);
            depthBuffer[j][i] = tex_w;
        }
    }

    private static void drawPixel(Graphics2D g, int screenHeight, int pixelSize, Color c, int x, int y)
    {
        y = screenHeight - y;
        g.setColor(c);
        g.fillRect(x*pixelSize, y*pixelSize, pixelSize, pixelSize);
    }

    private static Color sampleSprite(BufferedImage sprite, double luminance, int spriteWidth, int spriteHeigth, double u, double v)
    {
        u = Math.max(0, Math.min(1, u));
        v = Math.max(0, Math.min(1, v));

        u *= spriteWidth;
        v *= spriteHeigth;

        int rgb = sprite.getRGB((int)u, (int)v);
        Color result =  Drawer.getColourShade(new Color(rgb), luminance);
        return result;

        /*if (u >= 0 && u < sprite.getWidth() && v >= 0 && v < sprite.getHeight()) {
            int rgb = sprite.getRGB((int)u, (int)v);
            return new Color(rgb);
        } else {
            System.err.println("Texturizer: u (" + u + ") or v (" + v + ") are out of bounds of the sprite!");
            return Color.magenta;
        }*/
    }
}
