package Engine3d.Objects;

import Math.Geometries.Box;
import Math.Matrix4x4;
import Math.Vector.Vector3D;
import Engine3d.Rendering.DrawInstructions;
import Engine3d.Model.Mesh;
import Engine3d.Model.UnrotatableBox;
import Engine3d.Scene;

import java.awt.*;
import java.util.List;

public class Object3D implements Translatable, Rotatable
{
    private final Scene scene;
    private ObjectSource objectSource;

    private Mesh mesh;
    protected Vector3D rotation = new Vector3D(0,0,0);
    protected Vector3D position = new Vector3D(0,0,0);
    protected Vector3D scale = new Vector3D(1,1,1);

    public record ObjectRecord(
            ObjectSource source,
            double[] position,
            double[] rotation,
            double[] scale
    ) { }

    public Object3D(Scene scene) {
        this.scene = scene;
        scene.addObject(this);
        setUpDebugging();
    }

    public Object3D(Object3D source) {
        this.scene = source.getScene();

        this.mesh = new Mesh();
        mesh.copy(source.getMesh());
        this.position = new Vector3D(source.getPosition());
        this.rotation = new Vector3D(source.getRotation());

        scene.addObject(this);
        setUpDebugging();
    }

    public void setObjectSource(ObjectSource source) {
        this.objectSource = source;
    }
    public ObjectSource getObjectSource() {
        return objectSource;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
    public Mesh getMesh() {
        return mesh;
    }

    public List<Vector3D> getMeshPointsInWorld() {
        return mesh.getPointsInWorld(getPosition(), getRotation());
    }

    @Override
    public void rotate(Vector3D delta) {
        rotation = rotation.translated(delta);
    }

    @Override
    public Vector3D getRotation() {
        return rotation;
    }

    @Override
    public Vector3D getDirection() {
        return Matrix4x4.get3dRotationMatrix(rotation).matrixVectorMultiplication(Vector3D.FORWARD());
    }

    @Override
    public Vector3D getDirection(Vector3D base) {
        return Matrix4x4.get3dRotationMatrix(getRotation()).matrixVectorMultiplication(base);
    }

    @Override
    public void translate(Vector3D delta) {
        position = position.translated(delta);
    }

    @Override
    public Vector3D getPosition() {
        return position;
    }

    public Vector3D getScale() { return scale; }
    public void setScale(Vector3D scale) {
        this.scale = scale;
    }
    public void scaleBy(Vector3D delta) {
        this.scale = new Vector3D(
                scale.x()*delta.x(),
                scale.y()*delta.y(),
                scale.z()*delta.z());
    }
    public Scene getScene() {return scene;}

    // === DEBUGGING ===

    double sourceBoxSize = 0.1d;
    UnrotatableBox source = new UnrotatableBox(new Box(
            new Vector3D(-sourceBoxSize,-sourceBoxSize,-sourceBoxSize),
            new Vector3D(sourceBoxSize,sourceBoxSize,sourceBoxSize))
    );
    private void setUpDebugging() {
        //Centers the box on the source of the object
        source.translate(new Vector3D(-sourceBoxSize/2,-sourceBoxSize/2,-sourceBoxSize/2));
        source.centreToMiddleBottom();
        DrawInstructions di = new DrawInstructions(true,false,false,false);
        di.wireFrameColour = new Color(84, 178, 255);
        di.ignorePixelDepth = true;
        source.setDrawInstructions(di);
    }
    public UnrotatableBox getSource() {
        source.translate(source.getPosition().inverted());
        source.translate(getPosition());
        return source;
    }
}
