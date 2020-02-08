package eu.medek.linerenderer3d.objects.examples;

import eu.medek.linerenderer3d.objects.Object3D;
import eu.medek.linerenderer3d.system.Color;
import eu.medek.linerenderer3d.system.Vector;

public class Bench extends Object3D {
    private static final int LEGS_COLOR = Color.fromRGB(0x38, 0x38, 0x38);
    private static final int PLANKS_COLOR = Color.fromRGB(0x33, 0x2c, 0x2b);
    private static final float LINE_WIDTH = 5f;

    private static final Box[] planks = new Box[]{
            new Box(new float[]{0, -.24f, -.16f}, new float[]{0,0,0}, new float[]{1,.04f,.13f}, PLANKS_COLOR, LINE_WIDTH),
            new Box(new float[]{0, -.24f, 0f}, new float[]{0,0,0}, new float[]{1,.04f,.13f}, PLANKS_COLOR, LINE_WIDTH),
            new Box(new float[]{0, -.24f, .16f}, new float[]{0,0,0}, new float[]{1,.04f,.13f}, PLANKS_COLOR, LINE_WIDTH)
    };

    private static final Vector[] vertices;
    private static final int[][] edges;

    static {
        vertices = new Vector[15*4];
        Vector[] legPart = new Vector[] {new Vector(0,-.22f,.16f+.13f/4), new Vector(0,-.165f,.105f+.13f/4), new Vector(0,-.11f,.105f+.13f/4), new Vector(0,-.055f,.16f+.13f/4), new Vector(0,0,.22f), new Vector(0,0,.10f), new Vector(0,-0.055f,.045f), new Vector(0,-0.07f,0), new Vector(0,-0.055f,-.045f), new Vector(0,0,-.10f), new Vector(0,0,-.22f), new Vector(0,-.055f,-.16f-.13f/4), new Vector(0,-.11f,-.105f-.13f/4), new Vector(0,-.165f,-.105f-.13f/4), new Vector(0,-.22f,-.16f-.13f/4)};
        for (int i = 0; i < legPart.length; i++) vertices[i] = Vector.add(new Vector(-0.4f,0,0),legPart[i]);
        for (int i = 0; i < legPart.length; i++) vertices[i + legPart.length] = Vector.add(new Vector(-0.3f,0,0),legPart[i]);
        for (int i = 0; i < legPart.length; i++) vertices[i + legPart.length*2] = Vector.add(new Vector(0.3f,0,0),legPart[i]);
        for (int i = 0; i < legPart.length; i++) vertices[i + legPart.length*3] = Vector.add(new Vector(0.4f,0,0),legPart[i]);

        edges = new int[15*4+15*2][];
        edges[0] = new int[]{0, 14, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};
        for (int i = 1; i < legPart.length; i++) edges[i] = new int[]{i - 1, i, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};
        edges[15] = new int[]{15, 15 + 14, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};
        for (int i = 1; i < legPart.length; i++) edges[i + 15] = new int[]{15 + i - 1, 15 + i, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};
        edges[15*2] = new int[]{15*2, 15*2 + 14, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};
        for (int i = 1; i < legPart.length; i++) edges[i + 15*2] = new int[]{15*2 + i - 1, 15*2 + i, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};
        edges[15*3] = new int[]{15*3, 15*3 + 14, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};
        for (int i = 1; i < legPart.length; i++) edges[i + 15*3] = new int[]{15*3 + i - 1, 15*3 + i, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};

        for (int i = 0; i < legPart.length; i++) edges[15*4 + i] = new int[]{i, i + 15, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};
        for (int i = 0; i < legPart.length; i++) edges[15*4 + 15 + i] = new int[]{i + 15*2, i + 15*3, LEGS_COLOR, Float.floatToIntBits(LINE_WIDTH)};
    }

    public Bench(float[] position, float[] rotation, float[] scale) {
        super(position, rotation, scale);
    }

    @Override
    public Vector[] getVertices() {
        return vertices;
    }

    @Override
    public int[][] getEdges() {
        return edges;
    }

    @Override
    protected Object3D[] getNestedAbstract() {
        return planks;
    }
}