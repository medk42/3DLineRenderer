package eu.medek.linerenderer3d.objects.examples;

import eu.medek.linerenderer3d.math.Matrix3D;
import eu.medek.linerenderer3d.objects.Object3D;
import eu.medek.linerenderer3d.system.Color;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class RecurTree extends Object3D {
    private static final float MAX_DELTA_ANGLE_X = 1;
    private static final float MAX_DELTA_ANGLE_Y = 1;
    private static final float WEIGHT_MULTIPLIER = .65f;
    private static final float LENGTH_MULTIPLIER = .72f;
    private static final float WEIGHT_LIMIT = .5f;
    private static final float WEIGHT_ORIG = 50;
    private static final float LENGTH_ORIG = 100*0.01f/2.88f;
    private static final int TOP_COLOR = Color.fromRGB(0, 0x66, 0);
    private static final int BOTTOM_COLOR = Color.fromRGB(0x29, 0x24, 0x23);

    private PVector[] vertices = null;
    private int[][] edges = null;
    private boolean debugLogging;

    public RecurTree(float[] position, float[] rotation, float[] scale, boolean debugLogging) {
        super(position, rotation, scale);
        this.debugLogging = debugLogging;
        recalculateVertices();
    }

    public void recalculateVertices() {
        if (debugLogging) System.out.print("Calculating verticies for tree... ");
        ArrayList<PVector> verticesTemp = new ArrayList<>();
        ArrayList<int[]> edgesTemp = new ArrayList<>();

        PVector startingPoint = new PVector(0, 0, 0);
        verticesTemp.add(startingPoint);
        actIndex = 0;
        drawBranch(actIndex, 0, 0, WEIGHT_ORIG, LENGTH_ORIG, verticesTemp, edgesTemp);

        vertices = new PVector[verticesTemp.size()];
        vertices = verticesTemp.toArray(vertices);

        edges = new int[edgesTemp.size()][];
        edges = edgesTemp.toArray(edges);
        invalidateCache();
        if (debugLogging) System.out.println("done");
    }

    int actIndex;
    private void drawBranch(int fromID, float angleX, float angleY, float weight, float length, ArrayList<PVector> verticesTemp, ArrayList<int[]> edgesTemp) {
        if (weight < WEIGHT_LIMIT) return;
        int myID = ++actIndex;

        float[] upVector = new float[] {0,-length,0, 1};
        float[][] transform = Matrix3D.multiply(Matrix3D.getRotateY(angleY), Matrix3D.getRotateX(angleX));
        PVector to = Matrix3D.toPosition(Matrix3D.multiply(transform, upVector));
        to.add(verticesTemp.get(fromID));
        float percent = 1-(weight - WEIGHT_LIMIT)/(WEIGHT_ORIG - WEIGHT_LIMIT);
        percent = PApplet.pow(percent, 10);

        verticesTemp.add(to);
        edgesTemp.add(new int[]{fromID, myID, PApplet.lerpColor(BOTTOM_COLOR, TOP_COLOR, percent, PApplet.RGB), Float.floatToIntBits(weight)});

        drawBranch(myID, (float)(angleX + Math.random()*MAX_DELTA_ANGLE_X), (float)(angleY + Math.random()*2*MAX_DELTA_ANGLE_Y - MAX_DELTA_ANGLE_Y), weight * WEIGHT_MULTIPLIER, length * LENGTH_MULTIPLIER, verticesTemp, edgesTemp);
        drawBranch(myID, (float)(angleX - Math.random()*MAX_DELTA_ANGLE_X), (float)(angleY + Math.random()*2*MAX_DELTA_ANGLE_Y - MAX_DELTA_ANGLE_Y), weight * WEIGHT_MULTIPLIER, length * LENGTH_MULTIPLIER, verticesTemp, edgesTemp);
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