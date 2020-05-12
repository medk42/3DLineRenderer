package eu.medek.linerenderer3d;

import eu.medek.linerenderer3d.camera.Camera;
import eu.medek.linerenderer3d.system.Color;
import eu.medek.linerenderer3d.system.Matrix3D;
import eu.medek.linerenderer3d.objects.Object3D;
import eu.medek.linerenderer3d.system.Vector;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class for representing and rendering the current 3D scene.
 */
class World {

    /**
     * Order in which to draw edges.
     */
    public enum DrawOrder {
        /**
         * Sort the edges by the distance of their midpoint from camera. Then draw the furthest first. This is slow, but
         * better looking.
         */
        SORT_EDGES,
        /**
         * Sort the objects by their distance from camera. Then draw the furthest first with edges in any order. This is
         * fast, but doesn't look as good.
         */
        SORT_OBJECTS
    }

    //const
    /**
     * "d" parameter for converting to perspective space.
     */
    private static final float d = 1;

    /**
     * Matrix for converting from camera space to perspective space.
     */
    private static final float[][] toPerspectiveMatrix = new float[][] {{1,0,0,0}, {0,1,0,0}, {0,0,1,0}, {0,0,1/d,0}};

    /**
     * Matrix for converting from camera space to perspective space. Needed to correctly (not) display objects/edges
     * behind the camera.
     */
    private static final float[][] toPerspectiveNegativeMatrix = new float[][] {{1,0,0,0}, {0,1,0,0}, {0,0,1,0}, {0,0,-1/d,0}};

    //var
    /**
     * List of 3D objects to render.
     */
    private ArrayList<Object3D> objects = new ArrayList<>();

    /**
     * Cached list of vertices used to draw in DrawOrder.SORT_EDGES.
     */
    private ArrayList<Vector> vertices = null;

    /**
     * Cached list of edges used to draw in DrawOrder.SORT_EDGES. Cache is useful, otherwise we would need to copy edges
     * and vertices from each object each frame and update edges with new positions of their vertices.
     */
    private ArrayList<int[]> edges = null;

    /**
     * Renderer used to render the scene.
     */
    private Renderer renderer;

    /**
     * Cached values for output's width/height. They are automatically updated every frame if necessary.
     */
    private int windowWidth = 0, windowHeight = 0;

    /**
     * Matrix for converting from perspective space to window space.
     */
    private float[][] toScreenMatrix;

    /**
     * Constructor for the World object.
     * @param renderer renderer to use to render the scene
     */
    public World(Renderer renderer) {
        this.renderer = renderer;
        setToScreenMatrix();
    }


    /**
     * Add {@link Object3D 3D object} to the scene.
     * @param obj the {@link Object3D 3D object} to be added
     */
    public void addObject(Object3D obj) {
        objects.add(obj);
        addObjectToCache(obj);
    }

    /**
     * Correctly set the toScreen matrix when the output and cached resolution (and by extention the resolution used for
     * the toScreen matrix)  don't match.
     */
    private void setToScreenMatrix() {
        if (renderer.getWidth() != windowWidth || renderer.getHeight() != windowHeight) {
            windowWidth = renderer.getWidth();
            windowHeight = renderer.getHeight();

            if (windowHeight < windowWidth) toScreenMatrix = Matrix3D.multiply(Matrix3D.getScale(windowHeight,windowHeight,1), Matrix3D.getTranslate(windowWidth*0.5f/windowHeight,0.5f,0));
            else toScreenMatrix = Matrix3D.multiply(Matrix3D.getScale(windowWidth,windowWidth,1), Matrix3D.getTranslate(0.5f,windowHeight*0.5f/windowWidth,0));
        }
    }

    /**
     * Cache object's vertices and edges by adding them to {@link World#vertices} and {@link World#edges}.
     * @param obj the {@link Object3D 3D object} to be cached
     */
    private void addObjectToCache(Object3D obj) {
        if (vertices == null || edges == null) updateCache();

        Vector[] objVerticesWorld = obj.calculateWorldVertices();
        int[][] objEdges = obj.getEdgesAll();

        int deltaVertices = vertices.size();

        vertices.addAll(Arrays.asList(objVerticesWorld));
        for (int i = 0; i < objEdges.length; i++) {
            int[] newEdge = new int[objEdges[i].length];
            for (int j = 2; j < newEdge.length; j++)
                newEdge[j] = objEdges[i][j];
            newEdge[0] = objEdges[i][0] + deltaVertices;
            newEdge[1] = objEdges[i][1] + deltaVertices;
            edges.add(newEdge);
        }
    }

    /**
     * Discard and rebuild {@link World#vertices} and {@link World#edges} caches.
     */
    private void updateCache() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();

        for (Object3D obj : objects) {
            addObjectToCache(obj);
        }
    }

    /**
     * Invalidate (discard) {@link World#vertices} and {@link World#edges} caches. Function doesn't rebuild the cache,
     * because cache might be invalidated multiple times per frame.
     */
    public void invalidateCache() {
        vertices = null;
        edges = null;
    }

    /**
     * @return the number of vertices in the scene
     */
    public int getVertexCount() {
        return vertices.size();
    }

    /**
     * @return the number of edges in the scene
     */
    public int getEdgeCount() {
        return edges.size();
    }

    public void draw(final Camera camera, int edgeLimit, DrawOrder drawOrder) {
        if (vertices == null || edges == null) updateCache();

        switch (drawOrder) {
            case SORT_OBJECTS: drawObjects(camera, edgeLimit); break;
            case SORT_EDGES: drawEdges(camera, edgeLimit); break;
        }
    }

    private void drawObjects(final Camera camera, int edgeLimit) {
        setToScreenMatrix();
        float[][] toCameraMatrix = camera.calculateToCameraMatrix();

        objects.sort((left, right) -> {
            float distRight = distSq(camera.getPosition(), new float[]{right.getPosition(0), right.getPosition(1), right.getPosition(2)});
            float distLeft = distSq(camera.getPosition(), new float[]{left.getPosition(0), left.getPosition(1), left.getPosition(2)});
            return Float.compare(distRight, distLeft);
        });

        for (Object3D obj : objects) {
            Vector[] worldVertices = obj.calculateWorldVertices();
            Vector[] cameraVertices = new Vector[worldVertices.length];
            for (int i = 0; i < worldVertices.length; i++)
                cameraVertices[i] = Matrix3D.toPosition(Matrix3D.multiply(toCameraMatrix, Matrix3D.toVector(worldVertices[i])));
            Vector[] perspectiveVertices = new Vector[cameraVertices.length];
            for (int i = 0; i < cameraVertices.length; i++) {
                if (cameraVertices[i].z >= 0)
                    perspectiveVertices[i] = Matrix3D.toPosition(Matrix3D.multiply(toPerspectiveMatrix, Matrix3D.toVector(cameraVertices[i])));
                else
                    perspectiveVertices[i] = Matrix3D.toPosition(Matrix3D.multiply(toPerspectiveNegativeMatrix, Matrix3D.toVector(cameraVertices[i])));
            }
            Vector[] screenVertices = new Vector[perspectiveVertices.length];
            for (int i = 0; i < perspectiveVertices.length; i++)
                screenVertices[i] = Matrix3D.toPosition(Matrix3D.multiply(toScreenMatrix, Matrix3D.toVector(perspectiveVertices[i])));

            int[][] edges = obj.getEdgesAll();
            for (int[] edge : edges) {
                if (edgeLimit-- == 0) return;
                if (screenVertices[edge[0]].z >= 0 && screenVertices[edge[1]].z >= 0) {
                    if (edge.length >= 3) renderer.setStrokeColor(Color.getR(edge[2]), Color.getG(edge[2]), Color.getB(edge[2]));
                    else renderer.setStrokeColor(255, 255, 255);
                    if (edge.length >= 4) {
                        renderer.setStrokeWeight(Float.intBitsToFloat(edge[3]) / cameraVertices[edge[0]].mag());
                    } else renderer.setStrokeWeight(1);
                    drawLineClipped(screenVertices[edge[0]].x, screenVertices[edge[0]].y, screenVertices[edge[1]].x, screenVertices[edge[1]].y);
                }
            }
        }
    }

    private void drawEdges (final Camera camera, int edgeLimit) {
        setToScreenMatrix();
        float[][] toCameraMatrix = camera.calculateToCameraMatrix();

        Vector[] cameraVertices = new Vector[vertices.size()];
        for (int i = 0; i < vertices.size(); i++)
            cameraVertices[i] = Matrix3D.toPosition(Matrix3D.multiply(toCameraMatrix, Matrix3D.toVector(vertices.get(i))));

        Vector[] perspectiveVertices = new Vector[cameraVertices.length];
        for (int i = 0; i < cameraVertices.length; i++) {
            if (cameraVertices[i].z >= 0)
                perspectiveVertices[i] = Matrix3D.toPosition(Matrix3D.multiply(toPerspectiveMatrix, Matrix3D.toVector(cameraVertices[i])));
            else
                perspectiveVertices[i] = Matrix3D.toPosition(Matrix3D.multiply(toPerspectiveNegativeMatrix, Matrix3D.toVector(cameraVertices[i])));
        }

        Vector[] screenVertices = new Vector[perspectiveVertices.length];
        for (int i = 0; i < perspectiveVertices.length; i++)
            screenVertices[i] = Matrix3D.toPosition(Matrix3D.multiply(toScreenMatrix, Matrix3D.toVector(perspectiveVertices[i])));

        edges.sort((left, right) -> {
            float[] firstEdge = new float[] {
                    (vertices.get(left[0]).x + vertices.get(left[1]).x)/2,
                    (vertices.get(left[0]).y + vertices.get(left[1]).y)/2,
                    (vertices.get(left[0]).z + vertices.get(left[1]).z)/2,
            };
            float[] secondEdge = new float[] {
                    (vertices.get(right[0]).x + vertices.get(right[1]).x)/2,
                    (vertices.get(right[0]).y + vertices.get(right[1]).y)/2,
                    (vertices.get(right[0]).z + vertices.get(right[1]).z)/2,
            };
            return Float.compare(distSq(camera.getPosition(), secondEdge),distSq(camera.getPosition(), firstEdge));
        });

        for (int[] edge : edges) {
            if (edgeLimit-- == 0) return;
            if (screenVertices[edge[0]].z >= 0 && screenVertices[edge[1]].z >= 0) {
                if (edge.length >= 3) renderer.setStrokeColor(Color.getR(edge[2]), Color.getG(edge[2]), Color.getB(edge[2]));
                else renderer.setStrokeColor(255, 255, 255);
                if (edge.length >= 4) {
                    renderer.setStrokeWeight(Float.intBitsToFloat(edge[3]) / cameraVertices[edge[0]].mag());
                }
                else renderer.setStrokeWeight(1);
                drawLineClipped(screenVertices[edge[0]].x, screenVertices[edge[0]].y, screenVertices[edge[1]].x, screenVertices[edge[1]].y);
            }
        }
    }

    /**
     * Returns distance between two points squared, for performance reasons - can be used in cases when there is no
     * reason to take the square root of the result. For example when sorting points by distances.
     * @param first point in 3D space (float array with length 3)
     * @param second point in 3D space (float array with length 3)
     * @return distance between the two points squared
     */
    private float distSq(float[] first, float[] second) {
        float diffX = first[0]-second[0];
        float diffY = first[1]-second[1];
        float diffZ = first[2]-second[2];
        return diffX*diffX + diffY*diffY + diffZ*diffZ;
    }

    private void drawLineClipped(float x0, float y0, float x1, float y1) {
        boolean p0In = inWindow(x0, y0), p1In = inWindow(x1, y1);
        if (p0In && p1In) renderer.line(x0, y0, x1, y1);
        else {
            int[][] borders = {{0,0,0,windowHeight-1}, {0,0,windowWidth-1,0}, {windowWidth-1,0,windowWidth-1,windowHeight-1},
                    {0,windowHeight-1,windowWidth-1,windowHeight-1}};
            Vector[] intersectionPoints = new Vector[4];
            int i = 0;
            for (int[] border : borders) {
                Vector intersection = getIntersection(x0,y0,x1,y1,border[0],border[1],border[2],border[3]);
                if (intersection != null) intersectionPoints[i++] = intersection;
            }

            if (!p0In && !p1In) {
                if (intersectionPoints[0] == null || intersectionPoints[1] == null) return;
                renderer.line(intersectionPoints[0].x, intersectionPoints[0].y, intersectionPoints[1].x, intersectionPoints[1].y);
            } else if (!p0In) {
                if (intersectionPoints[0] == null) return;
                renderer.line(intersectionPoints[0].x, intersectionPoints[0].y, x1, y1);
            } else {
                if (intersectionPoints[0] == null) return;
                renderer.line(x0, y0, intersectionPoints[0].x, intersectionPoints[0].y);
            }
        }
    }

    /**
     * Get the point of intersection between two lines specified by their endpoints using line-line intersection
     * (https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection). x1,y1,x2,y2 specify endpoints of the first line,
     * x3,y3,x4,y4 specify endpoints of the second line
     * @return point of intersection or null if there isn't one
     */
    private Vector getIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        float denominator = (x1-x2)*(y3-y4)-(y1-y2)*(x3-x4);

        if (Math.abs(denominator) < 0.000001) return null;

        float t = ((x1-x3)*(y3-y4)-(y1-y3)*(x3-x4))/denominator;
        float u = ((y1-y2)*(x1-x3)-(x1-x2)*(y1-y3))/denominator;

        if (t < 0 || t > 1 || u < 0 || u > 1) return null;

        return new Vector(x1+t*(x2-x1), y1+t*(y2-y1));
    }

    /**
     * @param x x coordinate
     * @param y y coordinate
     * @return true if point (x,y) is in the window with size specified by the {@link World#renderer}, false otherwise
     */
    private boolean inWindow(float x, float y) {
        return (x >= 0 && y >= 0 && x < windowWidth && y < windowHeight);
    }
}