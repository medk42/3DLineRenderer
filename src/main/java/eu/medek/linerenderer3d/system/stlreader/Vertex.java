package eu.medek.linerenderer3d.system.stlreader;

/**
 * Class representing a point specified in STL file as part of a triangle. Objects are immutable.
 */
public class Vertex {
    /**
     * Coordinates of the Vertex.
     */
    private float x,y,z;

    /**
     * Basic constructor.
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     */
    public Vertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Get the x coordinate.
     * @return x coordinate
     */
    public float getX() {
        return x;
    }

    /**
     * Get the y coordinate.
     * @return y coordinate
     */
    public float getY() {
        return y;
    }

    /**
     * Get the z coordinate.
     * @return z coordinate
     */
    public float getZ() {
        return z;
    }

    /**
     * Create a String containing data of this object.
     * @return String containing data of this object - "[x, y, z]"
     */
    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + ']';
    }
}
