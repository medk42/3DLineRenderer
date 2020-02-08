package eu.medek.linerenderer3d.system;

/**
 * Class representing a 3D vector with x, y and z float components.
 */
public class Vector {
    /**
     * Component of the 3D vector.
     */
    public float x, y, z;

    /**
     * Create vector from 3 individual components.
     * @param x x component
     * @param y y component
     * @param z z component
     */
    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Create vector from 2 individual components - useful for representing a 2D vector. Third component {@link #z} is
     * set to 0.
     * @param x x component
     * @param y y component
     */
    public Vector(float x, float y) {
        this(x, y, 0);
    }

    /**
     * Create a zero vector - all components are set to 0.
     */
    public Vector() {
        this(0,0,0);
    }

    /**
     * Create a copy of a vector.
     * @param v vector to create a copy of
     */
    public Vector(Vector v) {
        this(v.x, v.y, v.z);
    }

    /**
     * Add another vector to this one (by components).
     * @param v other vector
     * @return this vector (for chain operations)
     */
    public Vector add(Vector v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;

        return this;
    }

    /**
     * Subtract another vector from this one (by components).
     * @param v other vector
     * @return this vector (for chain operations)
     */
    public Vector sub(Vector v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;

        return this;
    }

    /**
     * Multiply vector by some value (by components).
     * @param val value to multiply components by
     * @return this vector (for chain operations)
     */
    public Vector mult(double val) {
        this.x *= val;
        this.y *= val;
        this.z *= val;

        return this;
    }

    /**
     * Divide vector by some value (by components).
     * @param val value to divide components by
     * @return this vector (for chain operations)
     */
    public Vector div(double val) {
        this.x /= val;
        this.y /= val;
        this.z /= val;

        return this;
    }

    /**
     * Calculate magnitude of this vector. Uses square root.
     * @return magnitude of this vector
     */
    public float mag() {
        return (float)Math.sqrt(x*x + y*y + z*z);
    }

    /**
     * Normalize vector - set magnitude to 1. Uses {@link #mag} method.
     * @return this vector (for chain operations)
     */
    public Vector normalize() {
        this.div(this.mag());
        return this;
    }

    /**
     * Add two vectors and return the result as a new vector. Doesn't affect the value of either input vector.
     * @param v first vector
     * @param u second vector
     * @return new vector created by adding u and v
     */
    public static Vector add(Vector v, Vector u) {
        return new Vector(v).add(u);
    }

    /**
     * Subtract the vector u from the vector v and return the result as a new vector. Doesn't affect the value of either
     * input vector.
     * @param v first vector
     * @param u second vector
     * @return new vector created by subtracting u from v i.e. v-u
     */
    public static Vector sub(Vector v, Vector u) {
        return new Vector(v).sub(u);
    }

    /**
     * Create a normalized "2D" vector (z component is set to 0) specified by input angle.
     * @param angle zero points in direction of x-axis, increases away from the y-axis (counterclockwise if y-axis
     *              points down)
     * @return vector created from specified angle
     */
    public static Vector fromAngle(double angle) {
        return new Vector((float)Math.cos(angle), -(float)Math.sin(angle));
    }
}
