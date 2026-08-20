package Engine3d.Rendering.ScreenDrawing;

import Engine3d.Model.MTL;
import Engine3d.Rendering.ScreenDrawing.Drawer;

import java.awt.*;
import java.awt.image.BufferedImage;

import static Engine3d.Rendering.ScreenDrawing.ScreenBuffer.*;
import static java.lang.Math.abs;

public class Texturizer
{
    public static void textureTriangle(ScreenBuffer screenBuffer, MTL mtl,
                                       int x1, int y1, double u1, double v1, double w1,
                                       int x2, int y2, double u2, double v2, double w2,
                                       int x3, int y3, double u3, double v3, double w3,
                                       double luminance, Rect clip, BufferedImage sprite)
    {
        int[] color = screenBuffer.colourArray();
        double[][] depth = screenBuffer.depthArray();
        int W = screenBuffer.width(), H = screenBuffer.height();
        int[] tex = spritePixels(sprite);
        int texW = sprite.getWidth(), texH = sprite.getHeight();
        int diffuse = mtl.getDiffuseColour().getRGB();     // packed once, not per pixel

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
            for (int i = Math.max(y1, clip.y0()); i <= Math.min(y2, clip.y1()-1); i++) {
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

                double tstep = 1.0 / (bx - ax);
                int sx = Math.max(ax, clip.x0());
                int ex = Math.min(bx, clip.x1());
                double t = (sx - ax) * tstep;

                for (int j = sx; j < ex; j++) {
                    tex_w = (1.0f - t) * tex_sw + t * tex_ew;

                    if (tex_w > depth[j][i]) {                          // depth test (in bounds by clamp)
                        tex_u = (1.0f - t) * tex_su + t * tex_eu;
                        tex_v = (1.0f - t) * tex_sv + t * tex_ev;

                        double uu = ((tex_u/tex_w) % 1 + 1) % 1;
                        double vv = ((tex_v/tex_w) % 1 + 1) % 1;
                        int texel = tex[(int)(vv*(texH-1)) * texW + (int)(uu*(texW-1))];
                        if ((texel >>> 24) != 0) {                      // skip fully-transparent texels
                            depth[j][i] = tex_w;
                            int argb = shade(mul(texel, diffuse), luminance);
                            color[(H - 1 - i) * W + j] = argb;
                        }
                    }

                    //drawTextureToPixel(screenBuffer,mtl,luminance,sprite,spriteWidth,spriteHeigth,
                    //        tex_u,tex_v,tex_w,clip,j,i);
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
            for (int i = Math.max(y2, clip.y0()); i <= Math.min(y3, clip.y1()-1); i++) { //Clipping loop to only cover RasterTile
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

                double tstep = 1.0f / ((double)(bx - ax));
                int sx = Math.max(ax, clip.x0());
                int ex = Math.min(bx, clip.x1());
                double t = (sx - ax) * tstep;      // ← pre-advance so texels line up across tile

                for (int j = sx; j < ex; j++)
                {
                    tex_w = (1.0f - t) * tex_sw + t * tex_ew;

                    if (tex_w > depth[j][i]) {                          // depth test (in bounds by clamp)
                        tex_u = (1.0f - t) * tex_su + t * tex_eu;
                        tex_v = (1.0f - t) * tex_sv + t * tex_ev;

                        double uu = ((tex_u/tex_w) % 1 + 1) % 1;
                        double vv = ((tex_v/tex_w) % 1 + 1) % 1;
                        int texel = tex[(int)(vv*(texH-1)) * texW + (int)(uu*(texW-1))];
                        if ((texel >>> 24) != 0) {                      // skip fully-transparent texels
                            depth[j][i] = tex_w;
                            int argb = shade(mul(texel, diffuse), luminance);
                            color[(H - 1 - i) * W + j] = argb;
                        }
                    }

                    //drawTextureToPixel(screenBuffer,mtl,luminance,sprite,spriteWidth,spriteHeigth,
                    //        tex_u,tex_v,tex_w,clip,j,i);

                    t += tstep;
                }
            }
        }
    }

    private static void drawTextureToPixel(ScreenBuffer screenBuffer, MTL mtl, double luminance,
                                           BufferedImage sprite, int spriteWidth, int spriteHeight,
                                           double tex_u, double tex_v, double tex_w,
                                           Rect clip, int j, int i)
    {
        //If the pixel isn't within the allowed clip (Raster Tile)
        if (j < clip.x0() || j >= clip.x1() || i < clip.y0() || i >= clip.y1()) {return;}
        if (screenBuffer.pixelOnTop(j,i,tex_w))
        {
            screenBuffer.updateDepth(j,i,tex_w);
            Color texture = sampleSprite(sprite, spriteWidth, spriteHeight,tex_u / tex_w, tex_v / tex_w);
            if (Drawer.colourEmpty(texture, 0.1f)) {return;}
            Color diffuse = mtl.getDiffuseColour();
            if (Drawer.colourEmpty(diffuse, 0.1f)) {return;}
            Color base = Drawer.multiplyColors(texture, diffuse);
            Color shaded =  Drawer.getColourShade(base, luminance);
            PixelDrawer.drawPixel(screenBuffer, shaded, j, i);
        }
    }

    private static Color sampleSprite(BufferedImage sprite, int spriteWidth, int spriteHeigth, double u, double v)
    {
        u = (u % 1 + 1) % 1; // Ensures u is between 0 and 1
        v = (v % 1 + 1) % 1; // Ensures v is between 0 and 1

        u *= spriteWidth;
        v *= spriteHeigth;

        int rgb = sprite.getRGB((int)u, (int)v);
        int alpha = (rgb >> 24) & 0xFF; // Extract the alpha channel (8 highest bits)
        int red = (rgb >> 16) & 0xff;
        int green = (rgb >> 8) & 0xff;
        int blue = (rgb >> 0) & 0xff;

        return new Color(red, green, blue, alpha);
    }
}
