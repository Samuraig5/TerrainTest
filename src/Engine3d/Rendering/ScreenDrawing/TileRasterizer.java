package Engine3d.Rendering.ScreenDrawing;

import Engine3d.Model.Mesh;
import Engine3d.Rendering.DrawInstructions;
import Math.MeshTriangle;
import Engine3d.Rendering.Camera;
import Math.Vector.Vector2D;
import Math.Vector.Vector3D;
import Math.MathHelper;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TileRasterizer {
    static final int TILE = 32;
    private final ExecutorService pool =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private RasterTile[] tiles;
    private int gridW = -1, gridH = -1;

    public void render(Camera camera, List<Mesh.ProjectedTriangles> geometry, Color background) {
        ScreenBuffer buffer = camera.getScreenBuffer();

        ensureGrid(buffer.getSize());
        clearBins();
        bin(geometry);
        rasterize(buffer, camera, background);
    }

    private void ensureGrid(Vector2D size) {
        if (gridW != (int) Math.ceil(size.u()/TILE) ||
                gridH != (int) Math.ceil(size.v()/TILE)) {

            gridW = (int) Math.ceil(size.u()/TILE);
            gridH = (int) Math.ceil(size.v()/TILE);

            tiles = new RasterTile[gridW*gridH];

            int W = (int) size.u(), H = (int) size.v();

            for (int x = 0; x < gridW; x++) {
                for (int y = 0; y < gridH; y++) {
                    tiles[x + (y * gridW)] = new RasterTile(
                            new Rect(
                            x*TILE, y*TILE,
                            Math.min((x+1)*TILE, W), Math.min((y+1)*TILE, H))
                    );
                }
            }
        }
    }

    private void clearBins() {
        for (RasterTile tile : tiles) {
            tile.bin.clear();
        }
    }

    private void bin(List<Mesh.ProjectedTriangles> geometry) {
        for (Mesh.ProjectedTriangles pTri : geometry) {
            for (MeshTriangle tri : pTri.meshTriangles()) {
                Vector3D[] points = tri.getPoints();

                //Find screen-space box that "contains" the triangle
                int minX = (int)Math.floor(MathHelper.min3(points[0].x(), points[1].x(), points[2].x()));
                int maxX = (int)Math.ceil(MathHelper.max3(points[0].x(), points[1].x(), points[2].x()));
                int minY = (int)Math.floor(MathHelper.min3(points[0].y(), points[1].y(), points[2].y()));
                int maxY = (int)Math.ceil(MathHelper.max3(points[0].y(), points[1].y(), points[2].y()));

                TriRef ref = new TriRef(tri, pTri.drawInstructions());

                for (int x = minX/TILE; x <= maxX/TILE; x++) {
                    for (int y = minY/TILE; y <= maxY/TILE; y++) {
                        tiles[x + (y * gridW)].bin.add(ref);
                    }
                }
            }
        }
    }

    private void rasterize(ScreenBuffer buffer, Camera camera, Color background) {
        buffer.clear(background);

        Arrays.stream(tiles).parallel().forEach(tile -> {
            for (TriRef ref : tile.bin) {
                DrawInstructions drawInstructions = ref.di();
                if (drawInstructions.drawWireFrame) {
                    camera.drawer.drawTriangle(drawInstructions.wireFrameColour, ref.tri(), drawInstructions.ignorePixelDepth);
                }
                if (drawInstructions.drawFlatColour) {
                    camera.drawer.fillTriangle(ref.tri(), tile.rect);
                }
                else if (drawInstructions.drawTexture) {
                    if (ref.tri().getMaterial().getTexture() != null) {
                        camera.drawer.textureTriangle(ref.tri(), tile.rect);
                    }
                }
            }
        });
    }
}
