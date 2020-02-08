package eu.medek.linerenderer3d.objects;

import eu.medek.linerenderer3d.system.Vector;
import eu.medek.linerenderer3d.system.stlreader.STLReader;
import eu.medek.linerenderer3d.system.stlreader.STLTriangle;
import eu.medek.linerenderer3d.system.stlreader.Vertex;

import java.io.IOException;
import java.nio.file.Path;

public class STLObject extends Object3D {
    private final Vector[] vertices;
    private final int[][] edges;

    public STLObject(float[] position, float[] rotation, float[] scale, Path path) throws IOException {
        super(position, rotation, scale);

        STLReader reader = new STLReader(path);
        STLTriangle[] tris = reader.tryRead();

        vertices = new Vector[tris.length*3];
        edges = new int[tris.length*3][2];

        for (int i = 0; i < tris.length; i++) {
            vertices[3*i] = vertexToVector(tris[i].getP1());
            vertices[3*i+1] = vertexToVector(tris[i].getP2());
            vertices[3*i+2] = vertexToVector(tris[i].getP3());

            edges[3*i] = new int[]{3*i, 3*i+1};
            edges[3*i+1] = new int[]{3*i+1, 3*i+2};
            edges[3*i+2] = new int[]{3*i, 3*i+2};
        }

        Vector center = new Vector();
        for (Vector vertex : vertices) center.add(vertex);
        center.div(vertices.length);
        for (Vector vertex : vertices) vertex.sub(center);

        Vector min = new Vector(), max = new Vector();
        for (Vector vertex : vertices) {
            if (vertex.x < min.x) min.x = vertex.x;
            if (vertex.y < min.y) min.y = vertex.y;
            if (vertex.z < min.z) min.z = vertex.z;

            if (vertex.x > max.x) max.x = vertex.x;
            if (vertex.y > max.y) max.y = vertex.y;
            if (vertex.z > max.z) max.z = vertex.z;
        }
        Vector delta = Vector.sub(max,min);
        float dist = Math.max(delta.x, Math.max(delta.y, delta.z));
        for (Vector vertex : vertices) vertex.div(dist);

    }

    private Vector vertexToVector(Vertex v) {
        return new Vector(v.getX(), v.getY(), v.getZ());
    }

    @Override
    public Vector[] getVertices() {
        return vertices;
    }

    @Override
    public int[][] getEdges() {
        return edges;
    }

    @Override
    protected Object3D[] getNestedAbstract() {
        return new Object3D[0];
    }
}
