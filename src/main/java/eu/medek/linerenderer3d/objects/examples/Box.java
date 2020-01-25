package eu.medek.linerenderer3d.objects.examples;

import eu.medek.linerenderer3d.objects.Object3D;
import processing.core.PVector;

public class Box extends Object3D {
    private static final PVector[] vertices = new PVector[8];
    private static final int[][] edges = new int[][] {{0,1},{1,2},{2,3},{3,0},{3,4},{2,5},{1,6},{0,7},{4,5},{5,6},{6,7},{7,4}};
    static {
        vertices[0] = new PVector(-.5f,-.5f,-.5f);
        vertices[1] = new PVector(-.5f,-.5f,.5f);
        vertices[2] = new PVector(-.5f,.5f,.5f);
        vertices[3] = new PVector(-.5f,.5f,-.5f);
        vertices[4] = new PVector(.5f,.5f,-.5f);
        vertices[5] = new PVector(.5f,.5f,.5f);
        vertices[6] = new PVector(.5f,-.5f,.5f);
        vertices[7] = new PVector(.5f,-.5f,-.5f);
    }

    public Box(float[] position, float[] rotation, float[] scale) {
        super(position, rotation, scale);
    }

    @Override
    public PVector[] getVertices() {
        return vertices;
    }

    @Override
    public int[][] getEdges() {
        return edges;
    }
}