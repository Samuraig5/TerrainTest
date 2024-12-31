import java.awt.*;
import java.util.Vector;

public class Object3D
{
    Point3D[] points;
    int[][] edges;

    public Object3D(Point3D[] points, int[][] edges)
    {
        this.points = points;
        this.edges = edges;
    }

    public void translate(Vector3D delta)
    {
        for (Point3D point:points) {
            point.translate(delta);
        }
    }

    public void drawObject(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;

        for (int[] edge : edges) {
            Point3D p1 = points[edge[0]];
            Point3D p2 = points[edge[1]];
            g2d.drawLine((int) p1.projectX(500), (int) p1.projectY(500),
                            (int) p2.projectX(500), (int) p2.projectY(500));
        }
    }
}
