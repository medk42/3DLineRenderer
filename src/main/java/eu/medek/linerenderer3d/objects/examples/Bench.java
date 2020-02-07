package eu.medek.linerenderer3d.objects.examples;

import eu.medek.linerenderer3d.objects.Object3D;
import eu.medek.linerenderer3d.system.Color;
import processing.core.PVector;

public class Bench extends Object3D {
    private static final int LEGS_COLOR = Color.fromRGB(0x38, 0x38, 0x38);
    private static final int PLANKS_COLOR = Color.fromRGB(0x33, 0x2c, 0x2b);
    private static final float LINE_WIDTH = 5f;

    private static final Box[] planks = new Box[]{
            new Box(new float[]{0, -.24f, -.16f}, new float[]{0,0,0}, new float[]{1,.04f,.13f}),
            new Box(new float[]{0, -.24f, 0f}, new float[]{0,0,0}, new float[]{1,.04f,.13f}),
            new Box(new float[]{0, -.24f, .16f}, new float[]{0,0,0}, new float[]{1,.04f,.13f})
    };

    private static final PVector[] vertices;
    private static final int[][] edges;

    static {
        int boxVert = planks[0].getVertices().length;
        int vertOrigSize = boxVert*3;
        vertices = new PVector[vertOrigSize + 15*4];
        for (int i = 0; i < vertOrigSize; i++) vertices[i] = planks[i/boxVert].calculateWorldVertices()[i%boxVert];
        PVector[] legPart = new PVector[] {new PVector(0,-.22f,.16f+.13f/4), new PVector(0,-.165f,.105f+.13f/4), new PVector(0,-.11f,.105f+.13f/4), new PVector(0,-.055f,.16f+.13f/4), new PVector(0,0,.22f), new PVector(0,0,.10f), new PVector(0,-0.055f,.045f), new PVector(0,-0.07f,0), new PVector(0,-0.055f,-.045f), new PVector(0,0,-.10f), new PVector(0,0,-.22f), new PVector(0,-.055f,-.16f-.13f/4), new PVector(0,-.11f,-.105f-.13f/4), new PVector(0,-.165f,-.105f-.13f/4), new PVector(0,-.22f,-.16f-.13f/4)};
        for (int i = 0; i < legPart.length; i++) vertices[vertOrigSize + i] = PVector.add(new PVector(-0.4f,0,0),legPart[i]);
        for (int i = 0; i < legPart.length; i++) vertices[vertOrigSize + i + legPart.length] = PVector.add(new PVector(-0.3f,0,0),legPart[i]);
        for (int i = 0; i < legPart.length; i++) vertices[vertOrigSize + i + legPart.length*2] = PVector.add(new PVector(0.3f,0,0),legPart[i]);
        for (int i = 0; i < legPart.length; i++) vertices[vertOrigSize + i + legPart.length*3] = PVector.add(new PVector(0.4f,0,0),legPart[i]);

        int boxEdges = planks[0].getEdges().length;
        int edgeOrigSize = boxEdges*3;
        edges = new int[edgeOrigSize+15*4+15*2][]; //<>//
        for (int i = 0; i < edgeOrigSize; i++) {
            edges[i] = new int[4];
            edges[i][0] = planks[i/boxEdges].getEdges()[i%boxEdges][0] + (i/boxEdges)*boxVert;
            edges[i][1] = planks[i/boxEdges].getEdges()[i%boxEdges][1] + (i/boxEdges)*boxVert;
            edges[i][2] = PLANKS_COLOR;
            edges[i][3] = Float.floatToIntBits(LINE_WIDTH);
        }
        edges[edgeOrigSize] = new int[]{vertOrigSize, vertOrigSize + 14, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};
        for (int i = 1; i < legPart.length; i++) edges[edgeOrigSize + i] = new int[]{vertOrigSize + i - 1, vertOrigSize + i, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};
        edges[edgeOrigSize+15] = new int[]{vertOrigSize+15, vertOrigSize + 15 + 14, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};
        for (int i = 1; i < legPart.length; i++) edges[edgeOrigSize + i + 15] = new int[]{vertOrigSize + 15 + i - 1, vertOrigSize + 15 + i, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};
        edges[edgeOrigSize+15*2] = new int[]{vertOrigSize+15*2, vertOrigSize + 15*2 + 14, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};
        for (int i = 1; i < legPart.length; i++) edges[edgeOrigSize + i + 15*2] = new int[]{vertOrigSize + 15*2 + i - 1, vertOrigSize + 15*2 + i, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};
        edges[edgeOrigSize+15*3] = new int[]{vertOrigSize+15*3, vertOrigSize + 15*3 + 14, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};
        for (int i = 1; i < legPart.length; i++) edges[edgeOrigSize + i + 15*3] = new int[]{vertOrigSize + 15*3 + i - 1, vertOrigSize + 15*3 + i, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};

        for (int i = 0; i < legPart.length; i++) edges[edgeOrigSize + 15*4 + i] = new int[]{vertOrigSize + i, vertOrigSize + i + 15, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};
        for (int i = 0; i < legPart.length; i++) edges[edgeOrigSize + 15*4 + 15 + i] = new int[]{vertOrigSize + i + 15*2, vertOrigSize + i + 15*3, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};
    }

    public Bench(float[] position, float[] rotation, float[] scale) {
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

    @Override
    protected Object3D[] getNestedAbstract() {
        return new Object3D[0];
    }
}