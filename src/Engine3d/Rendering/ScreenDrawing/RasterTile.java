package Engine3d.Rendering.ScreenDrawing;

import Engine3d.Rendering.DrawInstructions;
import Math.Geometries.MeshTriangle;

import java.util.ArrayList;
import java.util.List;

record Rect(int x0, int y0, int x1, int y1) { }
record TriRef(MeshTriangle tri, DrawInstructions di) { }
public class RasterTile {
    final Rect rect;
    final List<TriRef> bin = new ArrayList<>();
    RasterTile(Rect r) { this.rect = r; }
}
