package eu.medek.linerenderer3d.objects.examples;

import eu.medek.linerenderer3d.objects.Object3D;
import processing.core.PVector;

public class Box extends Object3D {
    private static final PVector[] vertices = new PVector[8];
    private final int[][] edges;
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

    public Box(float[] position, float[] rotation, float[] scale, int color, float lineWidth) {
        super(position, rotation, scale);
        int wBits = Float.floatToIntBits(lineWidth);
        edges = new int[][] {{0,1,color,wBits},{1,2,color,wBits},{2,3,color,wBits},{3,0,color,wBits},{3,4,color,wBits},{2,5,color,wBits},{1,6,color,wBits},{0,7,color,wBits},{4,5,color,wBits},{5,6,color,wBits},{6,7,color,wBits},{7,4,color,wBits}};
    }

    public Box(float[] position, float[] rotation, float[] scale) {
        super(position, rotation, scale);
        edges = new int[][] {{0,1},{1,2},{2,3},{3,0},{3,4},{2,5},{1,6},{0,7},{4,5},{5,6},{6,7},{7,4}};
    }

    @Override
    public PVector[] getVertices() {
        return vertices;
    }

    @Override
    public int[][] getEdges() {
        return edges;
    }

    @Override
    protected Object3D[] getNestedAbstract() {
        return new Object3D[0];
    }
}