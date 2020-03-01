package eu.medek.linerenderer3d.objects.examples;

import eu.medek.linerenderer3d.system.Matrix3D;
import eu.medek.linerenderer3d.objects.Object3D;
import eu.medek.linerenderer3d.system.Color;
import eu.medek.linerenderer3d.system.Vector;

import java.util.ArrayList;

/**
 * Complex implementation of Object3D class creating a recursive 3D tree.
 */
public class RecurTree extends Object3D {
    /**
     * Maximum angle in radians by which a branch angle changes in X coordinate during one {@link RecurTree#drawBranch} call.
     */
    private static final float MAX_DELTA_ANGLE_X = 1;
    /**
     * Maximum angle in radians by which a branch angle changes in Y coordinate during one {@link RecurTree#drawBranch} call.
     */
    private static final float MAX_DELTA_ANGLE_Y = 1;
    /**
     * Amount to which the weight of a branch should be decreased during one {@link RecurTree#drawBranch} call.
     */
    private static final float WEIGHT_MULTIPLIER = .65f;
    /**
     * Amount to which the length of a branch should be decreased during one {@link RecurTree#drawBranch} call.
     */
    private static final float LENGTH_MULTIPLIER = .72f;
    /**
     * Minimum weight to draw a branch, if the branch's weight is lower, the recursion stops.
     */
    private static final float WEIGHT_LIMIT = .5f;
    /**
     * Original branch weight for the start of the recursive function.
     */
    private static final float WEIGHT_ORIG = 50;
    /**
     * Original branch length for the start of the recursive function.
     */
    private static final float LENGTH_ORIG = 1/2.88f;
    /**
     * Color of the top part of the tree, the leaves.
     */
    private static final int TOP_COLOR = Color.fromRGB(0, 0x66, 0);
    /**
     * Color of the bottom part of the tree, the trunk and branches.
     */
    private static final int BOTTOM_COLOR = Color.fromRGB(0x29, 0x24, 0x23);

    private Vector[] vertices = null;
    private int[][] edges = null;
    private boolean debugLogging;

    /**
     * Constructor with an option for debug logging.
     * @param debugLogging true to print debug information to the standard output.
     * @see Object3D#Object3D(float[], float[], float[])
     */
    public RecurTree(float[] position, float[] rotation, float[] scale, boolean debugLogging) {
        super(position, rotation, scale);
        this.debugLogging = debugLogging;
        recalculateVertices();
    }

    /**
     * Create a new random tree and calculate its vertices and edges.
     */
    public void recalculateVertices() {
        if (debugLogging) System.out.print("Calculating new vertices for tree... ");
        ArrayList<Vector> verticesTemp = new ArrayList<>();
        ArrayList<int[]> edgesTemp = new ArrayList<>();

        Vector startingPoint = new Vector();
        verticesTemp.add(startingPoint);
        actIndex = 0;
        drawBranch(actIndex, 0, 0, WEIGHT_ORIG, LENGTH_ORIG, verticesTemp, edgesTemp);

        vertices = new Vector[verticesTemp.size()];
        vertices = verticesTemp.toArray(vertices);

        edges = new int[edgesTemp.size()][];
        edges = edgesTemp.toArray(edges);
        invalidateVertexCache();
        if (debugLogging) System.out.println("done");
    }

    private int actIndex;

    /**
     * Function to recursively create a random tree with initial angle, branch weight, branch length and position
     * (passed in verticesTemp Vector list). This function creates a branch and then recursively calls itself to create
     * two child branches originating at its end.
     * @param fromID index of parent vertex in verticesTemp list
     * @param angleX x coordinate of branch angle (radians)
     * @param angleY y coordinate of branch angle (radians)
     * @param weight branch weight
     * @param length branch length
     * @param verticesTemp list of vertices for output (first call should supply the originating vertex)
     * @param edgesTemp list of edges for output (first call should supply empty list)
     */
    private void drawBranch(int fromID, float angleX, float angleY, float weight, float length, ArrayList<Vector> verticesTemp, ArrayList<int[]> edgesTemp) {
        if (weight < WEIGHT_LIMIT) return;
        int myID = ++actIndex;

        float[] upVector = new float[] {0,-length,0, 1};
        float[][] transform = Matrix3D.multiply(Matrix3D.getRotateY(angleY), Matrix3D.getRotateX(angleX));
        Vector to = Matrix3D.toPosition(Matrix3D.multiply(transform, upVector));
        to.add(verticesTemp.get(fromID));
        float percent = 1-(weight - WEIGHT_LIMIT)/(WEIGHT_ORIG - WEIGHT_LIMIT);
        percent = (float)Math.pow(percent, 10);

        verticesTemp.add(to);
        edgesTemp.add(new int[]{fromID, myID, lerpColor(BOTTOM_COLOR, TOP_COLOR, percent), Float.floatToIntBits(weight)});

        drawBranch(myID, (float)(angleX + Math.random()*MAX_DELTA_ANGLE_X), (float)(angleY + Math.random()*2*MAX_DELTA_ANGLE_Y - MAX_DELTA_ANGLE_Y), weight * WEIGHT_MULTIPLIER, length * LENGTH_MULTIPLIER, verticesTemp, edgesTemp);
        drawBranch(myID, (float)(angleX - Math.random()*MAX_DELTA_ANGLE_X), (float)(angleY + Math.random()*2*MAX_DELTA_ANGLE_Y - MAX_DELTA_ANGLE_Y), weight * WEIGHT_MULTIPLIER, length * LENGTH_MULTIPLIER, verticesTemp, edgesTemp);
    }

    /**
     * Calculate linear interpolation between two colors (c1 and c2) using percent.
     * @param c1 color to interpolate from
     * @param c2 color to interpolate to
     * @param percent amount to interpolate, between 0 (c1) and 1 (c2), no range check
     * @return color created by linear interpolation between two colors
     */
    public int lerpColor(int c1, int c2, float percent) {
        int r1 = Color.getR(c1), g1 = Color.getG(c1), b1 = Color.getB(c1);
        int r2 = Color.getR(c2), g2 = Color.getG(c2), b2 = Color.getB(c2);
        int r = (int)(r2*percent + r1*(1-percent));
        int g = (int)(g2*percent + g1*(1-percent));
        int b = (int)(b2*percent + b1*(1-percent));
        return Color.fromRGB(r, g, b);
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