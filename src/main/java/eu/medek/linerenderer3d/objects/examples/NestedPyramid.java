package eu.medek.linerenderer3d.objects.examples;

import eu.medek.linerenderer3d.objects.Object3D;
import eu.medek.linerenderer3d.system.Vector;

/**
 * More advanced example implementation of Object3D class with recursive nested objects.
 */
public class NestedPyramid extends Object3D {
    /**
     * Vertices of the NestedPyramid object.
     */
    private static Vector[] vertices = new Vector[] {
            new Vector(.5f,0,.5f),
            new Vector(-.5f,0,.5f),
            new Vector(-.5f,0,-.5f),
            new Vector(.5f,0,-.5f),
            new Vector(0f,-1f,0f)
    };

    /**
     * Edges of the NestedPyramid object.
     */
    private static int[][] edges = new int[][] {{0,1},{1,2},{2,3},{3,0},{0,4},{1,4},{2,4},{3,4}};

    /**
     * "Nested" objects of the NestedPyramid object - other NestedPyramid objects (NestedPyramid is a recursive
     *  3D object).
     */
    private NestedPyramid[] children;

    /**
     * Creates 4 children if recursionDepth &gt; 0.
     * @param recursionDepth number of levels of lines
     * @param recursionAngle angle at which the children pyramids are rotated away from center (0 means same rotation as
     *                       parent due to them being nested objects)
     * @see Object3D#Object3D(float[], float[], float[])
     */
    public NestedPyramid(float[] position, float[] rotation, float[] scale, int recursionDepth, float recursionAngle) {
        super(position, rotation, scale);
        if (recursionDepth > 0) {
            children = new NestedPyramid[]{
                new NestedPyramid(new float[]{.5f, -1, .5f}, new float[]{-recursionAngle, 0, recursionAngle}, new float[]{.5f, .5f, .5f}, recursionDepth - 1, recursionAngle),
                new NestedPyramid(new float[]{-.5f, -1, .5f}, new float[]{-recursionAngle, 0, -recursionAngle}, new float[]{.5f, .5f, .5f}, recursionDepth - 1, recursionAngle),
                new NestedPyramid(new float[]{-.5f, -1, -.5f}, new float[]{recursionAngle, 0, -recursionAngle}, new float[]{.5f, .5f, .5f}, recursionDepth - 1, recursionAngle),
                new NestedPyramid(new float[]{.5f, -1, -.5f}, new float[]{recursionAngle, 0, recursionAngle}, new float[]{.5f, .5f, .5f}, recursionDepth - 1, recursionAngle)
            };
        } else children = new NestedPyramid[0];
    }

    /**
     * Update the recursion angle.
     * @param newRecursionAngle angle at which the children pyramids are rotated away from center (0 means same rotation
     *                          as parent due to them being nested objects)
     */
    public void updateRecursionAngle(float newRecursionAngle) {
        if (children.length == 0) return;

        children[0].setRotation(0, -newRecursionAngle);
        children[0].setRotation(2, newRecursionAngle);

        children[1].setRotation(0, -newRecursionAngle);
        children[1].setRotation(2, -newRecursionAngle);

        children[2].setRotation(0, newRecursionAngle);
        children[2].setRotation(2, -newRecursionAngle);

        children[3].setRotation(0, newRecursionAngle);
        children[3].setRotation(2, newRecursionAngle);

        children[0].updateRecursionAngle(newRecursionAngle);
        children[1].updateRecursionAngle(newRecursionAngle);
        children[2].updateRecursionAngle(newRecursionAngle);
        children[3].updateRecursionAngle(newRecursionAngle);
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
