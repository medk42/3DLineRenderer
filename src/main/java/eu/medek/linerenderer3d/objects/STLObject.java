package eu.medek.linerenderer3d.objects;

import eu.medek.linerenderer3d.system.Vector;
import eu.medek.linerenderer3d.system.stlreader.STLReader;
import eu.medek.linerenderer3d.system.stlreader.STLTriangle;
import eu.medek.linerenderer3d.system.stlreader.Vertex;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Implementation of Object3D class able to display an STL file (both binary and text).
 */
public class STLObject extends Object3D {
    private final Vector[] vertices;
    private final int[][] edges;

    /**
     * Constructor for also setting path to the STL file and with an option to normalize vertices.
     * @param path path to the STL file; there are no checks, has to be valid, otherwise IOException gets thrown
     * @param normalize option to normalize the vertices of the loaded object to be in a cube of size 1 with its center
     *                  at 0
     * @throws IOException if any error arises while reading the file
     * @see Object3D#Object3D(float[], float[], float[])
     */
    public STLObject(float[] position, float[] rotation, float[] scale, Path path, boolean normalize) throws IOException {
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

        if (normalize) {
            Vector min = new Vector(), max = new Vector();
            for (Vector vertex : vertices) {
                if (vertex.x < min.x) min.x = vertex.x;
                if (vertex.y < min.y) min.y = vertex.y;
                if (vertex.z < min.z) min.z = vertex.z;

                if (vertex.x > max.x) max.x = vertex.x;
                if (vertex.y > max.y) max.y = vertex.y;
                if (vertex.z > max.z) max.z = vertex.z;
            }

            Vector delta = Vector.sub(max, min);
            Vector center = new Vector(delta).mult(0.5f).add(min);
            float dist = Math.max(delta.x, Math.max(delta.y, delta.z));
            for (Vector vertex : vertices) {
                vertex.sub(center);
                vertex.div(dist);
            }
        }
    }

    /**
     * Transform STL object Vertex representing vertex into Vector class.
     * @param v Vertex object to copy from
     * @return new Vector object containing the same data as the Vertex object
     */
    private Vector vertexToVector(Vertex v) {
        return new Vector(v.getX(), v.getY(), v.getZ());
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
