package Engine3d.Model;

import Math.MeshTriangle;
import Math.Vector.Vector2D;
import Math.Vector.Vector3D;
import Physics.Object3D;
import Engine3d.Rendering.Material;
import Engine3d.Rendering.ResourceManager.SpriteManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ObjParser
{

    SpriteManager spriteManager = new SpriteManager();

    public Mesh loadFromObjFile(Object3D object3D, String folderPath, String objFile)
    {
        Mesh mesh = new Mesh(object3D);
        object3D.setMesh(mesh);

        HashMap<String, MTL> mtlLib = new HashMap<>();

        List<Vector2D> textureVertices = new ArrayList<>();
        MTL mtl = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(folderPath + "/" + objFile))) {
            String line;

            while ((line = reader.readLine()) != null)
            {
                String[] data = line.split(" +");

                if (Objects.equals(data[0], "v")) {
                    mesh.points.add(generateVertex(data));
                }
                else if (Objects.equals(data[0], "vt")) {
                    textureVertices.add(generateTextureVertex(data));
                }
                else if (Objects.equals(data[0], "f")) {
                    List<String[]> triangles = new ArrayList<>();
                    if (data.length > 4) {
                        triangles = triangulateFace(data);
                        //System.err.println("Model contains quads");
                    }
                    else{
                        triangles.add(data);
                    }
                    for (String[] face: triangles) {
                        mesh.faces.add(generateFace(face, mtl, mesh.points, textureVertices));
                    }
                }
                else if (Objects.equals(data[0],"usemtl")) {
                    mtl = mtlLib.get(data[1]);
                }
                else if (Objects.equals(data[0],"mtllib")) {
                    mtlLib = readMTLlib(folderPath, recombineString(data));
                }
            }
        }
        catch (IOException e)
        {
            System.err.println("Error reading obj file: " + e.getMessage());
            return null;
        }
        return mesh;
    }

    private Vector3D generateVertex(String[] data) {
        return new Vector3D(-Double.parseDouble(data[1]), //For some reason if we don't invert this, loaded objects are mirrored across x
                Double.parseDouble(data[2]),
                Double.parseDouble(data[3]));

    }
    private Vector2D generateTextureVertex(String[] data) {
        return new Vector2D(Double.parseDouble(data[1]),
                1-Double.parseDouble(data[2]));
    }
    private MeshTriangle generateFace(String[] data, MTL mtl, List<Vector3D> vertices, List<Vector2D> texturePoints) {
        String[] token0 = data[3].split("/+");
        String[] token1 = data[2].split("/+");
        String[] token2 = data[1].split("/+");

        MeshTriangle face = new MeshTriangle(
                vertices.get(Integer.parseInt(token0[0]) - 1), //Because we invert x we have to invert the way the faces are loaded
                vertices.get(Integer.parseInt(token1[0]) - 1),
                vertices.get(Integer.parseInt(token2[0]) - 1));
        Material mat = new Material(
                texturePoints.get(Integer.parseInt(token0[1]) - 1),
                texturePoints.get(Integer.parseInt(token1[1]) - 1),
                texturePoints.get(Integer.parseInt(token2[1]) - 1));
        mat.setMTL(mtl);
        BufferedImage sprite = spriteManager.getResource(mtl.getColourTexture());
        mat.setTexture(sprite);
        face.setMaterial(mat);
        return face;
    }
    private static HashMap<String, MTL> readMTLlib(String folderPath, String mtlFile) {
        HashMap<String, MTL> mtlLib = new HashMap<>();
        MTL mtl = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(folderPath + "/" + mtlFile))) {
            String line;

            while ((line = reader.readLine()) != null)
            {
                String[] data = line.split(" +");
                switch (data[0]) {
                    case "newmtl" -> {
                        mtl = new MTL(recombineString(data));
                        mtlLib.put(data[1], mtl);
                    }
                    case "Kd" -> {
                        assert mtl != null;
                        mtl.setDiffuseColour(new Color((int) (Double.parseDouble(data[1])*255),
                                (int) (Double.parseDouble(data[2])*255),
                                (int) (Double.parseDouble(data[3])*255)));
                    }
                    case "map_Kd" -> {
                        assert mtl != null;
                        mtl.setColourTexture(folderPath + "/" + recombineString(data));
                    }
                }
            }
        }
        catch (IOException e)
        {
            System.err.println("Error reading mtl file: " + e.getMessage());
        }
        return mtlLib;
    }

    public static List<String[]> triangulateFace(String[] data) {
        List<String[]> triangles = new ArrayList<>();

        // Ensure the input face has at least 4 vertices (1 "f" token + 3 vertices).
        if (data.length < 4) {
            throw new IllegalArgumentException("Face must have at least 4 vertices to be triangulated.");
        }

        // Use the first vertex as the base for triangulation.
        String v0 = data[1];

        // Generate triangles for each consecutive pair of vertices.
        for (int i = 2; i < data.length - 1; i++) {
            String v1 = data[i];
            String v2 = data[i + 1];

            // Add the triangle as an array of strings.
            triangles.add(new String[]{"f", v0, v1, v2});
        }
        return triangles;
    }

    private static String recombineString(String[] strings) {
        if (strings == null || strings.length <= 1) {
            // Return an empty string if array is null or has 1 or fewer elements
            return "";
        }

        // Use String.join() to combine strings starting from the second element
        return String.join(" ", java.util.Arrays.copyOfRange(strings, 1, strings.length));
    }
}
