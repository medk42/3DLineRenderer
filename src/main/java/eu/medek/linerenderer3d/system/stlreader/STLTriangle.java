package eu.medek.linerenderer3d.system.stlreader;

/**
 * Class representing a triangle specified in STL file. Objects are immutable.
 */
public class STLTriangle {
    /**
     * Vertices of the triangle as specified in the STL file.
     */
    private Vertex p1, p2, p3;

    /**
     * Normal vector of the triangle as specified in the STL file - [normalX, normalY, normalZ].
     */
    private float normalX, normalY, normalZ;

    /**
     * Basic constructor of a triangle specified in STL file.
     * @param p1 first vertex
     * @param p2 second vertex
     * @param p3 third vertex
     * @param normalX x-coordinate of normal vector
     * @param normalY y-coordinate of normal vector
     * @param normalZ z-coordinate of normal vector
     */
    public STLTriangle(Vertex p1, Vertex p2, Vertex p3, float normalX, float normalY, float normalZ) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;

        this.normalX = normalX;
        this.normalY = normalY;
        this.normalZ = normalZ;
    }

    /**
     * Getter for the first vertex.
     * @return first vertex
     */
    public Vertex getP1() {
        return p1;
    }

    /**
     * Getter for the second vertex.
     * @return second vertex
     */
    public Vertex getP2() {
        return p2;
    }

    /**
     * Getter for the third vertex.
     * @return third vertex
     */
    public Vertex getP3() {
        return p3;
    }

    /**
     * Getter for the x-coordinate of the normal vector of this triangle.
     * @return x-coordinate of the normal vector of this triangle.
     */
    public float getNormalX() {
        return normalX;
    }

    /**
     * Getter for the y-coordinate of the normal vector of this triangle.
     * @return y-coordinate of the normal vector of this triangle.
     */
    public float getNormalY() {
        return normalY;
    }

    /**
     * Getter for the z-coordinate of the normal vector of this triangle.
     * @return z-coordinate of the normal vector of this triangle.
     */
    public float getNormalZ() {
        return normalZ;
    }

    /**
     * Create a String containing data of this object.
     * @return String containing data of this object - "STLTriangle{p1=[x1, y1, z1], p2=[x2, y2, z2], p3=[x3, y3, z3],
     * normal=[nX, nY, nZ]}"
     */
    @Override
    public String toString() {
        return "STLTriangle{" +
                "p1=" + p1 +
                ", p2=" + p2 +
                ", p3=" + p3 +
                ", normal=[" + normalX + ", " + normalY + ", " + normalZ + "]" +
                '}';
    }
}
