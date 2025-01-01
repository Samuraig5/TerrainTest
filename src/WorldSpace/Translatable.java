package WorldSpace;

public interface Translatable
{
    Vector3D position = new Vector3D();
    void translate(Vector3D delta);
}
