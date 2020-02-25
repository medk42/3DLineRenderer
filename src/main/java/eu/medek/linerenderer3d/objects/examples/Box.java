package eu.medek.linerenderer3d.objects.examples;

import eu.medek.linerenderer3d.objects.Object3D;
import eu.medek.linerenderer3d.system.Vector;

/**
 * Implementation of Object3D class creating a simple box of size 1 centered around (0,0,0).
 */
public class Box extends Object3D {
    private static final Vector[] vertices = new Vector[8];
    private final int[][] edges;
    static {
        vertices[0] = new Vector(-.5f,-.5f,-.5f);
        vertices[1] = new Vector(-.5f,-.5f,.5f);
        vertices[2] = new Vector(-.5f,.5f,.5f);
        vertices[3] = new Vector(-.5f,.5f,-.5f);
        vertices[4] = new Vector(.5f,.5f,-.5f);
        vertices[5] = new Vector(.5f,.5f,.5f);
        vertices[6] = new Vector(.5f,-.5f,.5f);
        vertices[7] = new Vector(.5f,-.5f,-.5f);
    }

    /**
     * Constructor for also setting color and width of the edges.
     * @param color color of the edges
     * @param lineWidth width of the edges
     * @see Object3D#Object3D(float[], float[], float[])
     */
    public Box(float[] position, float[] rotation, float[] scale, int color, float lineWidth) {
        super(position, rotation, scale);
        int wBits = Float.floatToIntBits(lineWidth);
        edges = new int[][] {{0,1,color,wBits},{1,2,color,wBits},{2,3,color,wBits},{3,0,color,wBits},{3,4,color,wBits},{2,5,color,wBits},{1,6,color,wBits},{0,7,color,wBits},{4,5,color,wBits},{5,6,color,wBits},{6,7,color,wBits},{7,4,color,wBits}};
    }

    /**
     * @see Object3D#Object3D(float[], float[], float[])
     */
    public Box(float[] position, float[] rotation, float[] scale) {
        super(position, rotation, scale);
        edges = new int[][] {{0,1},{1,2},{2,3},{3,0},{3,4},{2,5},{1,6},{0,7},{4,5},{5,6},{6,7},{7,4}};
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
        return new Object3D[0];
    }
}