package Engine3d.Rendering;

import Math.Matrix4x4;
import Math.MeshTriangle;
import Math.Vector.Vector3D;
import Engine3d.Rendering.ScreenDrawing.Drawer;
import Engine3d.Rendering.ScreenDrawing.ScreenBuffer;
import Engine3d.Rotatable;
import Engine3d.Translatable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public abstract class Camera implements Rotatable, Translatable {
    private Vector3D resolution = new Vector3D(200, 200, 0);
    double fov = 90;
    double zNear = 0.25d;
    double zFar = 1000;
    public boolean debugging = false;

    JFrame window;
    public Drawer drawer;
    Matrix4x4 projectionMatrix;

    private ScreenBuffer displayBuffer;
    private ScreenBuffer buildBuffer;

    public Camera(JFrame window)
    {
        this.window = window;
        this.drawer = new Drawer(this);
        this.displayBuffer = new ScreenBuffer(getResolution());
        this.buildBuffer = new ScreenBuffer(getResolution());

        this.projectionMatrix = Matrix4x4.getProjectionMatrix(fov, getAspectRatio(), zNear, zFar);

        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                onFrameSizeChange();
            }
        });
    }
    public void drawScreenBuffer(Graphics g){
        synchronized (this) {
            drawer.drawBuffer(g, displayBuffer); // Ensure thread-safe rendering
        }
    }
    public synchronized void swapBuffers() {
        ScreenBuffer temp = displayBuffer;
        displayBuffer = buildBuffer;
        buildBuffer = temp;
    }
    public void onFrameSizeChange() {
        //this.projectionMatrix = Matrix4x4.getProjectionMatrix(fov, getAspectRatio(), zNear, zFar);
        //this.buildBuffer.recompute(getResolution());
        //this.displayBuffer.recompute(getResolution());
    }
    public MeshTriangle projectTriangle(MeshTriangle in) {
        return projectionMatrix.multiplyWithTriangle(in);
    }
    public Matrix4x4 getProjectionMatrix() {
        return projectionMatrix;
    }
    public Vector3D getScreenDimensions() {
        return new Vector3D(window.getWidth(), window.getHeight(), 0);
    }
    public JFrame getFrame() {return window;}
    public Vector3D getResolution() { return new Vector3D(resolution); }
    private double getAspectRatio() { return getResolution().y() / getResolution().x(); }
    public ScreenBuffer getScreenBuffer(){ return buildBuffer; }
    public Vector3D getNearPlane() {return new Vector3D(0,0,zNear);}
    public Vector3D getFarPlane() {return new Vector3D(0,0,zFar);}

    public void setResolution(int width, int height) {
        this.resolution = new Vector3D(width, height, 0);
        this.projectionMatrix = Matrix4x4.getProjectionMatrix(fov, getAspectRatio(), zNear, zFar);
        this.buildBuffer.recompute(getResolution());
        this.displayBuffer.recompute(getResolution());
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
