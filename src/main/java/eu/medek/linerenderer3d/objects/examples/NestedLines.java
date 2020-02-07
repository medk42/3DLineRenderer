package eu.medek.linerenderer3d.objects.examples;

import eu.medek.linerenderer3d.objects.Object3D;
import processing.core.PVector;

public class NestedLines extends Object3D {

    private static PVector[] vertices = new PVector[] {
            new PVector(.5f, 0, 0),
            new PVector(-.5f, 0, 0)
    };

    private static int[][] edges = new int[][]{{0,1}};

    private NestedLines[] children;

    public NestedLines(float[] position, float[] rotation, float[] scale, int recursion) {
        super(position, rotation, scale);
        if (recursion > 0) {
            children = new NestedLines[]{
                new NestedLines(new float[]{.5f, -1, 0}, new float[]{0, 0, 0}, new float[]{.5f, .5f, .5f}, recursion - 1),
                new NestedLines(new float[]{-.5f, -1, 0}, new float[]{0, 0, 0}, new float[]{.5f, .5f, .5f}, recursion - 1)
            };
        } else children = new NestedLines[0];
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
        return children;
    }
}
