package eu.medek.linerenderer3d.system;

public class Vector {
    // Variables
    public float x, y, z;

    // Constructors
    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(float x, float y) {
        this(x, y, 0);
    }

    public Vector() {
        this(0,0,0);
    }

    public Vector(Vector v) {
        this(v.x, v.y, v.z);
    }

    // Methods
    public Vector add(Vector v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;

        return this;
    }

    public Vector sub(Vector v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;

        return this;
    }

    public Vector mult(double val) {
        this.x *= val;
        this.y *= val;
        this.z *= val;

        return this;
    }

    public Vector div(double val) {
        this.x /= val;
        this.y /= val;
        this.z /= val;

        return this;
    }

    public float mag() {
        return (float)Math.sqrt(x*x + y*y + z*z);
    }

    public Vector normalize() {
        this.div(this.mag());
        return this;
    }

    // Static methods
    public static Vector add(Vector v, Vector u) {
        return new Vector(v).add(u);
    }

    public static Vector sub(Vector v, Vector u) {
        return new Vector(v).sub(u);
    }

    public static Vector fromAngle(double angle) {
        return new Vector((float)Math.cos(angle), -(float)Math.sin(angle));
    }
}
