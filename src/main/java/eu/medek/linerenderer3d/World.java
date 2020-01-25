package eu.medek.linerenderer3d;

import eu.medek.linerenderer3d.camera.Camera;
import eu.medek.linerenderer3d.math.Matrix3D;
import eu.medek.linerenderer3d.objects.Object3D;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

class World {
    //const
    private static final float d = 1;
    private static final float[][] toPerspectiveMatrix = new float[][] {{1,0,0,0}, {0,1,0,0}, {0,0,1,0}, {0,0,1/d,0}};
    private static final float[][] toPerspectiveNegativeMatrix = new float[][] {{1,0,0,0}, {0,1,0,0}, {0,0,1,0}, {0,0,-1/d,0}};

    private final float[][] toScreenMatrix;

    //var
    private ArrayList<Object3D> objects = new ArrayList<>();
    private ArrayList<PVector> vertices = new ArrayList<>();
    private ArrayList<int[]> edges = new ArrayList<>();
    private PApplet pApplet;

    public World(PApplet pApplet) {
        this.pApplet = pApplet;
        if (pApplet.height < pApplet.width) toScreenMatrix = Matrix3D.multiply(Matrix3D.getScale(pApplet.height,pApplet.height,1), Matrix3D.getTranslate(pApplet.width*0.5f/pApplet.height,0.5f,0));
        else toScreenMatrix = Matrix3D.multiply(Matrix3D.getScale(pApplet.width,pApplet.width,1), Matrix3D.getTranslate(0.5f,pApplet.height*0.5f/pApplet.width,0));
    }


    public void addObject(Object3D obj) {
        objects.add(obj);
        addObjectToCache(obj);
    }

    private void addObjectToCache(Object3D obj) {
        PVector[] objVerticesWorld = obj.calculateWorldVertices();
        int[][] objEdges = obj.getEdges();

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

    public void invalidateCache() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();

        for (Object3D obj : objects) {
            addObjectToCache(obj);
        }
    }

    public void draw(Camera camera) {
        draw(camera, false, -1);
    }

    public void draw(final Camera camera, boolean debug, int edgeLimit) {
        float[][] toCameraMatrix = camera.calculateToCameraMatrix();

        int totalVertices = 0, totalEdges = 0;

        objects.sort((left, right) -> {
            float distRight = distSq(camera.getPosition(), new float[]{right.getPosition(0), right.getPosition(1), right.getPosition(2)});
            float distLeft = distSq(camera.getPosition(), new float[]{left.getPosition(0), left.getPosition(1), left.getPosition(2)});
            return Float.compare(distLeft, distRight);
        });

        for (Object3D obj : objects) {
            PVector[] worldVertices = obj.calculateWorldVertices();
            PVector[] cameraVertices = new PVector[worldVertices.length];
            for (int i = 0; i < worldVertices.length; i++)
                cameraVertices[i] = Matrix3D.toPosition(Matrix3D.multiply(toCameraMatrix, Matrix3D.toVector(worldVertices[i])));
            PVector[] perspectiveVertices = new PVector[cameraVertices.length];
            for (int i = 0; i < cameraVertices.length; i++) {
                if (cameraVertices[i].z >= 0)
                    perspectiveVertices[i] = Matrix3D.toPosition(Matrix3D.multiply(toPerspectiveMatrix, Matrix3D.toVector(cameraVertices[i])));
                else
                    perspectiveVertices[i] = Matrix3D.toPosition(Matrix3D.multiply(toPerspectiveNegativeMatrix, Matrix3D.toVector(cameraVertices[i])));
            }
            PVector[] screenVertices = new PVector[perspectiveVertices.length];
            for (int i = 0; i < perspectiveVertices.length; i++)
                screenVertices[i] = Matrix3D.toPosition(Matrix3D.multiply(toScreenMatrix, Matrix3D.toVector(perspectiveVertices[i])));

            int localEdgeLimit = edgeLimit;
            int[][] edges = obj.getEdges();
            for (int[] edge : edges) {
                if (localEdgeLimit-- == 0) break;
                if (screenVertices[edge[0]].z >= 0 && screenVertices[edge[1]].z >= 0) {
                    if (edge.length >= 3) pApplet.stroke(edge[2]);
                    else pApplet.stroke(255);
                    if (edge.length >= 4) {
                        pApplet.strokeWeight(Float.intBitsToFloat(edge[3]) / cameraVertices[edge[0]].z);
                    } else pApplet.strokeWeight(1);
                    pApplet.line(screenVertices[edge[0]].x, screenVertices[edge[0]].y, screenVertices[edge[1]].x, screenVertices[edge[1]].y);
                }
            }

            totalVertices += worldVertices.length;
            totalEdges += edges.length;
        }

        if (debug) {
            pApplet.fill(255);
            pApplet.textSize(15);
            pApplet.text("Total Vertices: " + totalVertices, 10, 20);
            pApplet.text("Total edges: " + totalEdges, 10, 40);
            pApplet.text("Edge limit: " + ((edgeLimit >= 0) ? edgeLimit : "not active"), 10, 60);
        }
    }

    public void drawBackToFront (final Camera camera) {
        float[][] toCameraMatrix = camera.calculateToCameraMatrix();

        PVector[] cameraVertices = new PVector[vertices.size()];
        for (int i = 0; i < vertices.size(); i++)
            cameraVertices[i] = Matrix3D.toPosition(Matrix3D.multiply(toCameraMatrix, Matrix3D.toVector(vertices.get(i))));

        PVector[] perspectiveVertices = new PVector[cameraVertices.length];
        for (int i = 0; i < cameraVertices.length; i++) {
            if (cameraVertices[i].z >= 0)
                perspectiveVertices[i] = Matrix3D.toPosition(Matrix3D.multiply(toPerspectiveMatrix, Matrix3D.toVector(cameraVertices[i])));
            else
                perspectiveVertices[i] = Matrix3D.toPosition(Matrix3D.multiply(toPerspectiveNegativeMatrix, Matrix3D.toVector(cameraVertices[i])));
        }

        PVector[] screenVertices = new PVector[perspectiveVertices.length];
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
            if (screenVertices[edge[0]].z >= 0 && screenVertices[edge[1]].z >= 0) {
                if (edge.length >= 3) pApplet.stroke(edge[2]);
                else pApplet.stroke(255);
                if (edge.length >= 4) {
                    pApplet.strokeWeight(Float.intBitsToFloat(edge[3]) / cameraVertices[edge[0]].z);
                }
                else pApplet.strokeWeight(1);
                pApplet.line(screenVertices[edge[0]].x, screenVertices[edge[0]].y, screenVertices[edge[1]].x, screenVertices[edge[1]].y);
            }
        }
    }

    private float distSq(float[] first, float[] second) {
        float diffX = first[0]-second[0];
        float diffY = first[1]-second[1];
        float diffZ = first[2]-second[2];
        return diffX*diffX + diffY*diffY + diffZ*diffZ;
    }
}