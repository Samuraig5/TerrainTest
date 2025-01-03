package WorldSpace;

import Rendering.Material;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObjLoader
{
    public static boolean loadFromObjFile(String objPath, Object3D object3D) {return loadFromObjFile(objPath, null, object3D);}
    public static boolean loadFromObjFile(String objPath, String texturePath, Object3D object3D)
    {
        boolean hasTexture = true;

        if (texturePath == null || texturePath.equals("")) {hasTexture = false;}

        object3D.mesh.clear();
        List<Vector3D> vertecies = new ArrayList<>();
        List<Vector2D> texturePoints = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(objPath))) {
            boolean readingVertecies = true;
            String line;

            while ((line = reader.readLine()) != null)
            {
                String[] data = line.split(" +");

                if (Objects.equals(data[0], "v"))
                {
                    Vector3D vetrex = new Vector3D(-Double.parseDouble(data[1]), //For some reason if we don't invert this, loaded objects are mirrored across x
                                                    Double.parseDouble(data[2]),
                                                    Double.parseDouble(data[3]));
                    vertecies.add(vetrex);
                }
                if (Objects.equals(data[0], "vt"))
                {
                    Vector2D tex = new Vector2D(Double.parseDouble(data[1]),
                                                    Double.parseDouble(data[2]));
                    texturePoints.add(tex);
                }
                else if (Objects.equals(data[0], "f"))
                {
                    //Once all vertecies have been read, add them to the mesh.
                    if (!hasTexture)
                    {
                        //Obj starts counting from 0, so we need to subtract 1 from the listed indexes
                        Triangle face = new Triangle(vertecies.get(Integer.parseInt(data[3]) - 1), //Because we invert x we have to invert the way the faces are loaded
                                                        vertecies.get(Integer.parseInt(data[2]) - 1),
                                                        vertecies.get(Integer.parseInt(data[1]) - 1));
                        object3D.mesh.add(face);
                    }
                    else
                    {
                        String[] token0 = data[3].split("/+");
                        String[] token1 = data[2].split("/+");
                        String[] token2 = data[1].split("/+");

                        Triangle face = new Triangle(vertecies.get(Integer.parseInt(token0[0]) - 1), //Because we invert x we have to invert the way the faces are loaded
                                                        vertecies.get(Integer.parseInt(token1[0]) - 1),
                                                        vertecies.get(Integer.parseInt(token2[0]) - 1));
                        Material mat = new Material(texturePoints.get(Integer.parseInt(token0[1]) - 1),
                                                    texturePoints.get(Integer.parseInt(token1[1]) - 1),
                                                    texturePoints.get(Integer.parseInt(token2[1]) - 1));
                        mat.setTexturePath(texturePath);
                        face.setMaterial(mat);
                        object3D.mesh.add(face);
                    }
                }
            }
            object3D.points = vertecies.toArray(new Vector3D[0]); readingVertecies = false;
        }
        catch (IOException e)
        {
            System.err.println("Error reading file: " + e.getMessage());
            return false;
        }
        return true;
    }
}
