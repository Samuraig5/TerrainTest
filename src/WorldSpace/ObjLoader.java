package WorldSpace;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObjLoader
{
    public static boolean loadFromObjFile(String filePath, Object3D object3D)
    {
        object3D.mesh.clear();
        List<Vector3D> vertecies = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            boolean readingVertecies = true;
            String line;

            while ((line = reader.readLine()) != null)
            {
                String[] data = line.split(" ");
                if (Objects.equals(data[0], "v"))
                {
                    Vector3D vetrex = new Vector3D(Double.parseDouble(data[1]),
                                                    Double.parseDouble(data[2]),
                                                    Double.parseDouble(data[3]));
                    vertecies.add(vetrex);
                }
                else if (Objects.equals(data[0], "f"))
                {
                    //Once all vertecies have been read, add them to the mesh.
                    if (readingVertecies) {object3D.points = vertecies.toArray(new Vector3D[0]); readingVertecies = false;}

                    //Obj starts counting from 0, so we need to subtract 1 from the listed indexes
                    Triangle face = new Triangle(object3D.points[Integer.parseInt(data[1])-1],
                                                    object3D.points[Integer.parseInt(data[2])-1],
                                                    object3D.points[Integer.parseInt(data[3])-1]);
                    object3D.mesh.add(face);
                }
            }

        }
        catch (IOException e)
        {
            System.err.println("Error reading file: " + e.getMessage());
            return false;
        }
        return true;
    }
}
