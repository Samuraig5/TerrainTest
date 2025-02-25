package Terrain;

import Engine3d.Rendering.DrawInstructions;
import Engine3d.Rendering.Material;
import Math.Vector.Vector3D;
import Math.MeshTriangle;
import Engine3d.Model.Mesh;
import Math.Vector.Vector5D;
import Physics.Object3D;

import java.awt.*;

public class TerrainTileMesh extends Mesh {
    public static double GRID_SIZE = 10;
    private Vector3D gridPos;

    public TerrainTileMesh(Object3D object3D, Vector3D gridPos, Vector5D gridHeight, TerrainTileMesh[][] neighbours) {
        super(object3D);

        this.gridPos = gridPos;

        generateMesh(gridPos, gridHeight, neighbours);
        this.setDrawInstructions(new DrawInstructions(false,true,false,true));
    }

    private void generateMesh(Vector3D gridPos, Vector5D gridHeight, TerrainTileMesh[][] neighbours) {
        getLevel(gridPos,
                gridHeight,
                neighbours);

        for (int i = 0; i < faces.size(); i++) {
            Material mat = faces.get(i).getMaterial();
            mat.setBaseColour(Color.GREEN);
        }
    }

    private void getLevel(Vector3D gridPos, Vector5D gridHeight, TerrainTileMesh[][] neighbours)
    {
        int x = (int) gridPos.x();
        int z = (int) gridPos.z();

        Vector3D min = new Vector3D();
        Vector3D max = new Vector3D(GRID_SIZE,GRID_SIZE,GRID_SIZE);

        double minX = min.x();
        double maxX = max.x();
        double diffX = (maxX - minX)/2;

        double minZ = min.z();
        double maxZ = max.z();
        double diffZ = (maxZ - minZ)/2;

        //Looking top down:
        double v0Y = gridHeight.a();
        SharedPoint v0sharedPoint = new SharedPoint();
        if (x > 0 && neighbours[x-1][z] != null) {
            TerrainVertex v0_other = (TerrainVertex) neighbours[x-1][z].getPoint(3);
            v0sharedPoint = v0_other.getSharedPoint();
            v0Y = v0_other.y();
        }
        else if (z > 0 && neighbours[x][z-1] != null) {
            TerrainVertex v0_other = (TerrainVertex) neighbours[x][z-1].getPoint(1);
            v0sharedPoint = v0_other.getSharedPoint();
            v0Y = v0_other.y();
        }
        TerrainVertex v0 = new TerrainVertex(minX, v0Y, minZ); //Bottom Left
        v0.assignToSharedPoint(v0sharedPoint);

        double v1Y = gridHeight.b();
        SharedPoint v1sharedPoint = new SharedPoint();
        if (x > 0 && neighbours[x-1][z] != null) {
            TerrainVertex v1_other = (TerrainVertex) neighbours[x-1][z].getPoint(2);
            v1sharedPoint = v1_other.getSharedPoint();
            v1Y = v1_other.y();
        }
        TerrainVertex v1 = new TerrainVertex(minX, v1Y, maxZ); //Bottom Left
        v1.assignToSharedPoint(v1sharedPoint);

        //Since terrain is generated from min to max, the top right never has existing neighbours
        TerrainVertex v2 = new TerrainVertex(maxX,  gridHeight.d(), maxZ); //Top Right
        v1.assignToSharedPoint(new SharedPoint());

        double v3Y = gridHeight.e();
        SharedPoint v3sharedPoint = new SharedPoint();
        if (z > 0 && neighbours[x][z-1] != null) {
            TerrainVertex v3_other = (TerrainVertex) neighbours[x][z-1].getPoint(2);
            v3sharedPoint = v3_other.getSharedPoint();
            v3Y = v3_other.y();
        }
        TerrainVertex v3 = new TerrainVertex(maxX, v3Y, minZ); //Bottom Right
        v0.assignToSharedPoint(v3sharedPoint);


        //The centre point never exists
        TerrainVertex v4 = new TerrainVertex(minX+diffX, gridHeight.c(), minZ+diffZ); //Centre
        v4.assignToSharedPoint(new SharedPoint());


        points.add(v0);
        points.add(v1);
        points.add(v2);
        points.add(v3);
        points.add(v4);

        MeshTriangle t0 = new MeshTriangle(v0,v1,v4);
        MeshTriangle t1 = new MeshTriangle(v1,v2,v4);
        MeshTriangle t2 = new MeshTriangle(v2,v3,v4);
        MeshTriangle t3 = new MeshTriangle(v3,v0,v4);
        faces.add(t0);
        faces.add(t1);
        faces.add(t2);
        faces.add(t3);
    }

    private Vector3D getPoint(int i) {
        return points.get(i);
    }
}
