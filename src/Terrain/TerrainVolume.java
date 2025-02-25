package Terrain;

import Engine3d.Model.Mesh;
import Engine3d.Rendering.DrawInstructions;
import Math.Vector.Vector3D;
import Physics.Object3D;
import Math.MeshTriangle;

import static Terrain.TerrainVolumePoints.*;

public class TerrainVolume extends Mesh
{
    private double size;

    public TerrainVolume(Object3D object3D, double size) {
        super(object3D);
        this.size = size;
        initialize();
    }

    private void initialize() {
        points.add(generatePoint(TOP_FRONT_LEFT));
        points.add(generatePoint(TOP_FRONT_RIGHT));
        points.add(generatePoint(TOP_BACK_LEFT));
        points.add(generatePoint(TOP_BACK_RIGHT));
        points.add(generatePoint(TOP_CENTRE));
        points.add(generatePoint(BOTTOM_FRONT_LEFT));
        points.add(generatePoint(BOTTOM_FRONT_RIGHT));
        points.add(generatePoint(BOTTOM_BACK_LEFT));
        points.add(generatePoint(BOTTOM_BACK_RIGHT));
        points.add(generatePoint(BOTTOM_CENTRE));

        //Top Faces
        for (int i = 0; i < 4; i++) {
            faces.add(generateHorizontalFace(i));
        }
        //Bottom Faces
        for (int i = 5; i < 9; i++) {
            faces.add(generateHorizontalFace(i));
        }
        generateVerticalFaces();

        this.setDrawInstructions(new DrawInstructions(true,true,false,true));
    }


    private Vector3D generatePoint(TerrainVolumePoints index) {
        return index.getVector().scaled(size/2);
    }

    private MeshTriangle generateHorizontalFace(int index) {

        boolean isTopFace = index < 4;

        int centreIndex;
        if (isTopFace) {centreIndex = 4;} else {centreIndex = 9;}
        int otherIndex = index + 1;
        if (otherIndex == centreIndex) {otherIndex -= 4;}

        if (isTopFace) {
            return new MeshTriangle(
                    points.get(index),
                    points.get(centreIndex),
                    points.get(otherIndex)
            );
        }
        else {
            return new MeshTriangle(
                    points.get(index),
                    points.get(otherIndex),
                    points.get(centreIndex)
                    );
        }
    }

    private void generateVerticalFaces() {
        MeshTriangle[] frontFace =
                generateVerticalFace(
                        TOP_FRONT_LEFT.ordinal(),
                        TOP_FRONT_RIGHT.ordinal(),
                        BOTTOM_FRONT_LEFT.ordinal(),
                        BOTTOM_FRONT_RIGHT.ordinal()
                );
        faces.add(frontFace[0]);
        faces.add(frontFace[1]);

        MeshTriangle[] leftFace =
                generateVerticalFace(
                        TOP_BACK_LEFT.ordinal(),
                        TOP_FRONT_LEFT.ordinal(),
                        BOTTOM_BACK_LEFT.ordinal(),
                        BOTTOM_FRONT_LEFT.ordinal()
                );
        faces.add(leftFace[0]);
        faces.add(leftFace[1]);

        MeshTriangle[] rightFace =
                generateVerticalFace(
                        TOP_FRONT_RIGHT.ordinal(),
                        TOP_BACK_RIGHT.ordinal(),
                        BOTTOM_FRONT_RIGHT.ordinal(),
                        BOTTOM_BACK_RIGHT.ordinal()
                );
        faces.add(rightFace[0]);
        faces.add(rightFace[1]);

        MeshTriangle[] backFace =
                generateVerticalFace(
                        TOP_BACK_RIGHT.ordinal(),
                        TOP_BACK_LEFT.ordinal(),
                        BOTTOM_BACK_RIGHT.ordinal(),
                        BOTTOM_BACK_LEFT.ordinal()
                );
        faces.add(backFace[0]);
        faces.add(backFace[1]);
    }

    private MeshTriangle[] generateVerticalFace(int i, int j, int k, int l) {
        MeshTriangle[] faces = new MeshTriangle[2];

        faces[0] = new MeshTriangle(points.get(i), points.get(j), points.get(k));
        faces[1] = new MeshTriangle(points.get(j), points.get(l), points.get(k));

        return faces;
    }
}


