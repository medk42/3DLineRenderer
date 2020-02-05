package eu.medek.linerenderer3d.system.stlreader;

public class STLTriangle {
    private Vertex p1, p2, p3;
    private float normalX, normalY, normalZ;

    public STLTriangle(Vertex p1, Vertex p2, Vertex p3, float normalX, float normalY, float normalZ) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;

        this.normalX = normalX;
        this.normalY = normalY;
        this.normalZ = normalZ;
    }

    public Vertex getP1() {
        return p1;
    }

    public Vertex getP2() {
        return p2;
    }

    public Vertex getP3() {
        return p3;
    }

    public float getNormalX() {
        return normalX;
    }

    public float getNormalY() {
        return normalY;
    }

    public float getNormalZ() {
        return normalZ;
    }

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
