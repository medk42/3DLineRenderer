package eu.medek.linerenderer3d.objects.examples;

import eu.medek.linerenderer3d.objects.Object3D;
import processing.core.PVector;

public class NestedPyramid extends Object3D {
    private static PVector[] vertices = new PVector[] {
            new PVector(.5f,0,.5f),
            new PVector(-.5f,0,.5f),
            new PVector(-.5f,0,-.5f),
            new PVector(.5f,0,-.5f),
            new PVector(0f,-1f,0f)
    };

    private static int[][] edges = new int[][] {{0,1},{1,2},{2,3},{3,0},{0,4},{1,4},{2,4},{3,4}};

    private NestedPyramid[] children;

    public NestedPyramid(float[] position, float[] rotation, float[] scale, int recursion, float recursionAngle) {
        super(position, rotation, scale);
        if (recursion > 0) {
            children = new NestedPyramid[]{
                new NestedPyramid(new float[]{.5f, -1, .5f}, new float[]{-recursionAngle, 0, recursionAngle}, new float[]{.5f, .5f, .5f}, recursion - 1, recursionAngle),
                new NestedPyramid(new float[]{-.5f, -1, .5f}, new float[]{-recursionAngle, 0, -recursionAngle}, new float[]{.5f, .5f, .5f}, recursion - 1, recursionAngle),
                new NestedPyramid(new float[]{-.5f, -1, -.5f}, new float[]{recursionAngle, 0, -recursionAngle}, new float[]{.5f, .5f, .5f}, recursion - 1, recursionAngle),
                new NestedPyramid(new float[]{.5f, -1, -.5f}, new float[]{recursionAngle, 0, recursionAngle}, new float[]{.5f, .5f, .5f}, recursion - 1, recursionAngle)
            };
        } else children = new NestedPyramid[0];
    }

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
