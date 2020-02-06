package eu.medek.linerenderer3d.objects;

import eu.medek.linerenderer3d.system.stlreader.STLReader;
import eu.medek.linerenderer3d.system.stlreader.STLTriangle;
import eu.medek.linerenderer3d.system.stlreader.Vertex;
import processing.core.PVector;

import java.io.IOException;
import java.nio.file.Path;

public class STLObject extends Object3D {
    private final PVector[] verticies;
    private final int[][] edges;

    public STLObject(float[] position, float[] rotation, float[] scale, Path path) throws IOException {
        super(position, rotation, scale);

        STLReader reader = new STLReader(path);
        STLTriangle[] tris = reader.tryRead();

        verticies = new PVector[tris.length*3];
        edges = new int[tris.length*3][2];

        for (int i = 0; i < tris.length; i++) {
            verticies[3*i] = vertexToPVector(tris[i].getP1());
            verticies[3*i+1] = vertexToPVector(tris[i].getP2());
            verticies[3*i+2] = vertexToPVector(tris[i].getP3());

            edges[3*i] = new int[]{3*i, 3*i+1};
            edges[3*i+1] = new int[]{3*i+1, 3*i+2};
            edges[3*i+2] = new int[]{3*i, 3*i+2};
        }

        PVector center = new PVector();
        for (PVector vertex : verticies) center.add(vertex);
        center.div(verticies.length);
        for (PVector vertex : verticies) vertex.sub(center);

        PVector min = new PVector(), max = new PVector();
        for (PVector vertex : verticies) {
            if (vertex.x < min.x) min.x = vertex.x;
            if (vertex.y < min.y) min.y = vertex.y;
            if (vertex.z < min.z) min.z = vertex.z;

            if (vertex.x > max.x) max.x = vertex.x;
            if (vertex.y > max.y) max.y = vertex.y;
            if (vertex.z > max.z) max.z = vertex.z;
        }
        PVector delta = PVector.sub(max,min);
        float dist = Math.max(delta.x, Math.max(delta.y, delta.z));
        for (PVector vertex : verticies) vertex.div(dist);

    }

    private PVector vertexToPVector(Vertex v) {
        return new PVector(v.getX(), v.getY(), v.getZ());
    }

    @Override
    public PVector[] getVertices() {
        return verticies;
    }

    @Override
    public int[][] getEdges() {
        return edges;
    }
}
