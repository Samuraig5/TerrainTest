package Engine3d.Lighting;

import Engine3d.Math.Matrix4x4;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Rendering.Scene;
import Engine3d.Rotatable;
import Engine3d.Translatable;

import java.awt.*;

public class LightSource implements Translatable, Rotatable
{
    private double lightIntensity = 1;
    private double lightRange = Double.MAX_VALUE;
    private Color lightColour = new Color(255, 255, 255);
    private Vector3D position = new Vector3D();
    private Vector3D rotation = new Vector3D();

    public LightSource(Scene scene) {
        scene.addLight(this);
    }
    public void setLightIntensity(double lightIntensity) {
        this.lightIntensity = lightIntensity;
    }
    public double getLightIntensity() {
        return Math.min(1, Math.max(0, lightIntensity));
    }
    public double getLightIntensity(double distance){
        distance = Math.min(lightRange, Math.max(0, distance));
        return lightIntensity * (1 - (distance / lightRange));
    }
    public void setLightRange(double lightRange) {
        this.lightRange = lightRange;
    }
    public double getLightRange() {
        return lightRange;
    }
    public Color getLightColour() {
        return lightColour;
    }
    public Vector3D getPosition() {
        return position;
    }

    public void setRotation(Vector3D rotation) {
        this.rotation = rotation;
    }
    @Override
    public void translate(Vector3D delta) {
        rotation.translate(delta);
    }

    @Override
    public void rotate(Vector3D delta) {
        position.translate(delta);
    }
    @Override
    public Vector3D getRotation() {
        return rotation;
    }
    @Override
    public Vector3D getDirection() {
        return Matrix4x4.get3dRotationMatrix(getRotation()).matrixVectorMultiplication(Vector3D.FORWARD());
    }
    @Override
    public Vector3D getDirection(Vector3D base) {
        return Matrix4x4.get3dRotationMatrix(getRotation()).matrixVectorMultiplication(base);
    }
}
