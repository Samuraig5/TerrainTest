package WorldSpace;

public class Point3D extends Vector3D {


    public Point3D(double x, double y, double z) {
        super(x, y, z);
    }

    public void rotateX(double angle) {
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double newY = cos * y - sin * z;
        double newZ = sin * y + cos * z;
        y = newY;
        z = newZ;
    }

    public void rotateY(double angle) {
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double newX = cos * x + sin * z;
        double newZ = -sin * x + cos * z;
        x = newX;
        z = newZ;
    }

    public double projectX(double distance) {
        return x / (z / distance + 1);
    }

    public double projectY(double distance) {
        return y / (z / distance + 1);
    }
}