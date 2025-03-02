package Terrain;

import Engine3d.Model.Mesh;
import Engine3d.Rendering.DrawInstructions;
import Engine3d.Scene;
import Math.Vector.Vector3D;
import Physics.AABBCollisions.StaticAABBObject;
import Physics.Object3D;
import Math.MeshTriangle;

import java.util.List;

import static Terrain.TerrainVolumePoints.*;

public class TerrainVolume extends Mesh
{
    private final TerrainScene scene;
    private double size;

    public TerrainVolume(TerrainScene scene, Object3D object3D, double size) {
        super(object3D);
        this.scene = scene;
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

        this.setDrawInstructions(new DrawInstructions(false,true,false,true));
    }

    private Vector3D generatePoint(TerrainVolumePoints index) {
        return index.getVector().scaled(size);
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

    @Override
    public void translatePoint(Vector3D targetPoint, Vector3D delta) {
        int targetIndex = 0;
        for (int i = 0; i < 10; i++) {
            if (targetPoint == points.get(i)) { targetIndex = i; break;}
        }
        if (targetIndex > 4) {return;} //If targeting a "bottom" point, don't do anything

        super.translatePoint(targetPoint, delta);

        correctHeightAdjustment(targetPoint, targetIndex, delta);
    }

    private void correctHeightAdjustment(Vector3D targetPoint, int targetIndex, Vector3D delta) {

        if (delta.y() > 0) {
            correctHeightIncrease();
        }
        else {
            correctHeightDecrease(targetPoint, targetIndex);
        }
        correctCentreHeight();
    }

    private void correctHeightDecrease(Vector3D targetPoint, int targetIndex) {
        double MIN_THICKNESS = 0.25f;

        boolean remove = true;

        for (int i = 0; i < 5; i++) {
            double lowestAllowed = points.get(i + 5).y() + MIN_THICKNESS;
            if (points.get(i).y() > lowestAllowed) { remove = false; break; } // If at least one corner is lower than the max, don't do anything
        }

        if (remove) {
            scene.removeObject(object3D);
        }
        else {
            double lowestAllowed = points.get(targetIndex + 5).y() + MIN_THICKNESS;
            if (targetPoint.y() < lowestAllowed) {
                targetPoint.y(lowestAllowed);
            }
        }
    }

    private void correctHeightIncrease() {
        if (scene.isOccupied(getPosition().translated(new Vector3D(0,size,0)))) {
            for (int i = 0; i < 5; i++) {
                Vector3D p = points.get(i);
                p.y(Math.min(p.y(), size));
            }
            return;
        }


        double[] heights = new double[5];
        for (int i = 0; i < 5; i++) {
            double excess = points.get(i).y()-size;
            if (excess <= 0) { return; } // If at least one corner is lower than the max, don't do anything
            heights[i] = excess;
        }

        StaticAABBObject newTerrainBlock = scene.createNewTerrainVolume(getPosition().translated(new Vector3D(0,size,0)));

        List<Vector3D> newPoints = newTerrainBlock.getMesh().getPoints();

        for (int i = 0; i < 5; i++) {
            points.get(i).y(size);
            Vector3D p = newPoints.get(i);
            p.y(heights[i]);
        }
    }

    private void correctCentreHeight() {
        double centreLowestAllowed =
                Math.max(
                        (points.get(TOP_FRONT_LEFT.ordinal()).y() +
                         points.get(TOP_BACK_RIGHT.ordinal()).y()) / 2,

                        (points.get(TOP_FRONT_RIGHT.ordinal()).y() +
                         points.get(TOP_BACK_LEFT.ordinal()).y()) / 2
                );

        Vector3D topCentre = points.get(TOP_CENTRE.ordinal());
        if (topCentre.y() < centreLowestAllowed) {
            topCentre.y(centreLowestAllowed);
        }
    }
}


