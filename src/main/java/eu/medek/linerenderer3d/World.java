package eu.medek.linerenderer3d;

import eu.medek.linerenderer3d.camera.Camera;
import eu.medek.linerenderer3d.system.Matrix3D;
import eu.medek.linerenderer3d.objects.Object3D;
import eu.medek.linerenderer3d.system.Vector;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Arrays;

class World {

    public enum DrawOrder {
        SORT_EDGES, SORT_OBJECTS
    }

    //const
    private static final float d = 1;
    private static final float[][] toPerspectiveMatrix = new float[][] {{1,0,0,0}, {0,1,0,0}, {0,0,1,0}, {0,0,1/d,0}};
    private static final float[][] toPerspectiveNegativeMatrix = new float[][] {{1,0,0,0}, {0,1,0,0}, {0,0,1,0}, {0,0,-1/d,0}};

    //var
    private ArrayList<Object3D> objects = new ArrayList<>();
    private ArrayList<Vector> vertices = null;
    private ArrayList<int[]> edges = null;
    private PApplet pApplet;
    private float windowWidth = 0, windowHeight = 0;

    private float[][] toScreenMatrix;

    public World(PApplet pApplet) {
        this.pApplet = pApplet;
        setToScreenMatrix();
    }


    public void addObject(Object3D obj) {
        objects.add(obj);
        addObjectToCache(obj);
    }

    private void setToScreenMatrix() {
        if (pApplet.width != windowWidth || pApplet.height != windowHeight) {
            windowWidth = pApplet.width;
            windowHeight = pApplet.height;

            if (pApplet.height < pApplet.width) toScreenMatrix = Matrix3D.multiply(Matrix3D.getScale(pApplet.height,pApplet.height,1), Matrix3D.getTranslate(pApplet.width*0.5f/pApplet.height,0.5f,0));
            else toScreenMatrix = Matrix3D.multiply(Matrix3D.getScale(pApplet.width,pApplet.width,1), Matrix3D.getTranslate(0.5f,pApplet.height*0.5f/pApplet.width,0));
        }
    }

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

    public void updateCache() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();

        for (Object3D obj : objects) {
            addObjectToCache(obj);
        }
    }

    public void invalidateCache() {
        vertices = null;
        edges = null;
    }

    public int getVertexCount() {
        return vertices.size();
    }

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
                    if (edge.length >= 3) pApplet.stroke(edge[2]);
                    else pApplet.stroke(255);
                    if (edge.length >= 4) {
                        pApplet.strokeWeight(Float.intBitsToFloat(edge[3]) / cameraVertices[edge[0]].mag());
                    } else pApplet.strokeWeight(1);
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
                if (edge.length >= 3) pApplet.stroke(edge[2]);
                else pApplet.stroke(255);
                if (edge.length >= 4) {
                    pApplet.strokeWeight(Float.intBitsToFloat(edge[3]) / cameraVertices[edge[0]].mag());
                }
                else pApplet.strokeWeight(1);
                drawLineClipped(screenVertices[edge[0]].x, screenVertices[edge[0]].y, screenVertices[edge[1]].x, screenVertices[edge[1]].y);
            }
        }
    }

    private float distSq(float[] first, float[] second) {
        float diffX = first[0]-second[0];
        float diffY = first[1]-second[1];
        float diffZ = first[2]-second[2];
        return diffX*diffX + diffY*diffY + diffZ*diffZ;
    }

    private void drawLineClipped(float x0, float y0, float x1, float y1) {
        boolean p0In = inWindow(x0, y0), p1In = inWindow(x1, y1);
        if (p0In && p1In) pApplet.line(x0, y0, x1, y1);
        else {
            Vector[] intersectionPoints = new Vector[4];
            int i = 0;
            int width = pApplet.width, height = pApplet.height;
            int[][] borders = {{0,0,0,height-1}, {0,0,width-1,0}, {width-1,0,width-1,height-1},{0,height-1,width-1,height-1}};
            for (int[] border : borders) {
                Vector intersection = getIntersection(x0,y0,x1,y1,border[0],border[1],border[2],border[3]);
                if (intersection != null) intersectionPoints[i++] = intersection;
            }

            if (!p0In && !p1In) {
                if (intersectionPoints[0] == null || intersectionPoints[1] == null) return;
                pApplet.line(intersectionPoints[0].x, intersectionPoints[0].y, intersectionPoints[1].x, intersectionPoints[1].y);
            } else if (!p0In) {
                if (intersectionPoints[0] == null) return;
                pApplet.line(intersectionPoints[0].x, intersectionPoints[0].y, x1, y1);
            } else {
                if (intersectionPoints[0] == null) return;
                pApplet.line(x0, y0, intersectionPoints[0].x, intersectionPoints[0].y);
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

    private boolean inWindow(float x, float y) {
        return (x >= 0 && y >= 0 && x < pApplet.width && y < pApplet.height);
    }
}