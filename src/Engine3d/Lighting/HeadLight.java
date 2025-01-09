package Engine3d.Lighting;

import Math.Vector.Vector3D;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.Scene;

public class HeadLight
{
    public HeadLight(Camera camera, Scene scene) {
        //Front
        CameraLight cl = new CameraLight(camera, scene, new Vector3D());
        cl.setLightRange(100);
        //Down
        CameraLight cl1 = new CameraLight(camera, scene, new Vector3D(Math.toRadians(-45),0,0));
        cl1.setLightIntensity(0.5);
        cl1.setLightRange(100);
        //Up
        CameraLight cl2 = new CameraLight(camera, scene, new Vector3D(Math.toRadians(45),0,0));
        cl2.setLightIntensity(0.5);
        cl2.setLightRange(100);
        //Left
        CameraLight cl3 = new CameraLight(camera, scene, new Vector3D(0,Math.toRadians(-45),0));
        cl3.setLightIntensity(0.5);
        cl3.setLightRange(100);
        //Right
        CameraLight cl4 = new CameraLight(camera, scene, new Vector3D(0,Math.toRadians(45),0));
        cl4.setLightIntensity(0.5);
        cl4.setLightRange(100);
    }
}
