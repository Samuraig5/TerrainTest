package Engine3d.Rendering.ScreenDrawing;

import static java.lang.Math.abs;

public class FlatShader {
    public static void drawTriangle(ScreenBuffer screenBuffer, int colour,
                                    int x1, int y1, double w1,
                                    int x2, int y2, double w2,
                                    int x3, int y3, double w3)
    {
        int tempi;
        double tempd;

        // Sort vertices by y-coordinate (y1 <= y2 <= y3)
        if (y2 < y1) {
            tempi = y1; y1 = y2; y2 = tempi;
            tempi = x1; x1 = x2; x2 = tempi;
            tempd = w1; w1 = w2; w2 = tempd;
        }
        if (y3 < y1) {
            tempi = y1; y1 = y3; y3 = tempi;
            tempi = x1; x1 = x3; x3 = tempi;
            tempd = w1; w1 = w3; w3 = tempd;
        }
        if (y3 < y2) {
            tempi = y2; y2 = y3; y3 = tempi;
            tempi = x2; x2 = x3; x3 = tempi;
            tempd = w2; w2 = w3; w3 = tempd;
        }

        int dy1 = y2 - y1;
        int dx1 = x2 - x1;
        double dw1 = w2 - w1;

        int dy2 = y3 - y1;
        int dx2 = x3 - x1;
        double dw2 = w3 - w1;

        double dax_step = 0, dbx_step = 0;
        double dw1_step = 0, dw2_step = 0;

        if (dy1 != 0) dax_step = dx1 / (double) abs(dy1);
        if (dy2 != 0) dbx_step = dx2 / (double) abs(dy2);

        if (dy1 != 0) dw1_step = dw1 / (double) abs(dy1);
        if (dy2 != 0) dw2_step = dw2 / (double) abs(dy2);

        // Draw upper half of triangle (y1 to y2)
        if (dy1 != 0) {
            for (int i = y1; i <= y2; i++) {
                int ax = (int) (x1 + (double) (i - y1) * dax_step);
                int bx = (int) (x1 + (double) (i - y1) * dbx_step);

                double w_start = w1 + (double) (i - y1) * dw1_step;
                double w_end = w1 + (double) (i - y1) * dw2_step;

                if (ax > bx) {
                    tempi = ax; ax = bx; bx = tempi;
                    tempd = w_start; w_start = w_end; w_end = tempd;
                }

                double tstep = 1.0 / (double) (bx - ax);
                double t = 0.0;

                for (int j = ax; j < bx; j++) {
                    double tex_w = (1.0 - t) * w_start + t * w_end;
                    screenBuffer.writePixelIfOnTop(j, i, tex_w, colour);
                    t += tstep;
                }
            }
        }

        // Recalculate for lower half of triangle (y2 to y3)
        dy1 = y3 - y2;
        dx1 = x3 - x2;
        dw1 = w3 - w2;

        if (dy1 != 0) dax_step = dx1 / (double) abs(dy1);
        if (dy1 != 0) dw1_step = dw1 / (double) abs(dy1);

        // Draw lower half of triangle (y2 to y3)
        if (dy1 != 0) {
            for (int i = y2; i <= y3; i++) {
                int ax = (int) (x2 + (double) (i - y2) * dax_step);
                int bx = (int) (x1 + (double) (i - y1) * dbx_step);

                double w_start = w2 + (double) (i - y2) * dw1_step;
                double w_end = w1 + (double) (i - y1) * dw2_step;

                if (ax > bx) {
                    tempi = ax; ax = bx; bx = tempi;
                    tempd = w_start; w_start = w_end; w_end = tempd;
                }

                double tstep = 1.0 / (double) (bx - ax);
                double t = 0.0;

                for (int j = ax; j < bx; j++) {
                    double tex_w = (1.0 - t) * w_start + t * w_end;
                    screenBuffer.writePixelIfOnTop(j, i, tex_w, colour);
                    t += tstep;
                }
            }
        }
    }
}
