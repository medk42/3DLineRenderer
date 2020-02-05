package eu.medek.linerenderer3d.system.stlreader;

public class Vertex {
    private float x,y,z;

    public Vertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + ']';
    }
}
