package eu.medek.linerenderer3d.objects.examples;

import eu.medek.linerenderer3d.objects.Object3D;
import eu.medek.linerenderer3d.system.Vector;

/**
 * Simple example implementation of Object3D class with recursive nested objects.
 */
public class NestedLines extends Object3D {

    private static Vector[] vertices = new Vector[] {
            new Vector(.5f, 0, 0),
            new Vector(-.5f, 0, 0)
    };

    private static int[][] edges = new int[][]{{0,1}};

    private NestedLines[] children;

    /**
     * Creates 2 children if recursionDepth > 0.
     * @param recursionDepth number of levels of lines
     * @see Object3D#Object3D(float[], float[], float[]) 
     */
    public NestedLines(float[] position, float[] rotation, float[] scale, int recursionDepth) {
        super(position, rotation, scale);
        if (recursionDepth > 0) {
            children = new NestedLines[]{
                new NestedLines(new float[]{.5f, -1, 0}, new float[]{0, 0, 0}, new float[]{.5f, .5f, .5f}, recursionDepth - 1),
                new NestedLines(new float[]{-.5f, -1, 0}, new float[]{0, 0, 0}, new float[]{.5f, .5f, .5f}, recursionDepth - 1)
            };
        } else children = new NestedLines[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector[] getVertices() {
        return vertices;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[][] getEdges() {
        return edges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object3D[] getNestedAbstract() {
        return children;
    }
}
