package eu.medek.linerenderer3d.objects;

import eu.medek.linerenderer3d.math.Matrix3D;
import eu.medek.linerenderer3d.system.Pair;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Class representing a displayable 3D object.
 */
public abstract class Object3D {
    /**
     * Reference to object containing this object as a nested object - used for invalidating parent's precalculated
     * world vertex cache in case this object's relative position/rotation/scale changes.
     */
    private Object3D parent = null;

    /**
     * Values representing position of this object (this transformation is also applied to all nested objects)
     */
    private float[] position;
    /**
     * Values representing rotation of this object (this transformation is also applied to all nested objects)
     */
    private float[] rotation;
    /**
     * Values representing scale of this object (this transformation is also applied to all nested objects)
     */
    private float[] scale;

    /**
     * Precalculated world vertices of this object. If object doesn't move between frames, there is no need to
     * recalculate world vertices.
     */
    private PVector[] precalculatedVertices = null;

    /**
     * Edges of this object and all nested objects. Will be calculated during the first call to {@link #getEdgesAll}
     * function.
     */
    private int[][] precalculatedAllEdges = null;

    /**
     * Constructor for a displayable 3D object.
     * @param position initial position of the object [x,y,z]
     * @param rotation initial rotation of the object [rx,ry,rz]
     * @param scale initial scale of the object [sx,sy,sz]
     */
    public Object3D(float[] position, float[] rotation, float[] scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    /**
     * Constructor for a displayable 3D object - position and rotation are set to zero, scale to one.
     */
    public Object3D() {
        this(new float[]{0, 0, 0}, new float[]{0, 0, 0}, new float[]{1, 1, 1});
    }

    /**
     * Getter for this object's local vertices (not including nested objects!). This method always has to return same values!
     * @return local vertices of this object
     */
    public abstract PVector[] getVertices();

    /**
     * Getter for this object's edges (not including nested objects!). This method always has to return same values!
     * @return edges of this object
     */
    public abstract int[][] getEdges();

    /**
     * Get this object's nested objects. This method always has to return same values! This method exists only to be
     * implemented by subclasses - always call {@link #getNested()} instead!
     * @return nested objects of this object
     */
    protected abstract Object3D[] getNestedAbstract();

    /**
     * Boolean value for single-time setup of nested objects.
     */
    private boolean getNestedSetupNeeded = true;
    /**
     * Getter for this object's nested objects (not recursive).
     * @return nested objects of this object
     */
    public Object3D[] getNested() {
        if (getNestedSetupNeeded) {
            Object3D[] nested = getNestedAbstract();
            for (var child : nested) child.parent = this;
            getNestedSetupNeeded = false;
            return nested;
        } else return getNestedAbstract();
    }


    /**
     * Getter for the x, y or z component of position vector.
     * @param id 0 for x, 1 for y, 2 for z (there is no range check!)
     * @return x, y or z component of position vector
     */
    public float getPosition(int id) {
        return position[id];
    }

    /**
     * Getter for the rx, ry or rz component of rotation vector.
     * @param id 0 for rx, 1 for ry, 2 for rz (there is no range check!)
     * @return rx, ry or rz component of rotation vector
     */
    public float getRotation(int id) {
        return rotation[id];
    }

    /**
     * Getter for the sx, sy or sz component of scale vector.
     * @param id 0 for sx, 1 for sy, 2 for sz (there is no range check!)
     * @return sx, sy or sz component of scale vector
     */
    public float getScale(int id) {
        return scale[id];
    }

    /**
     * Setter for the x, y or z component of position vector. World vertices will be recalculated in the next
     * call to calculateWorldVertices method (cached calculated values are no longer valid).
     * @param id 0 for x, 1 for y, 2 for z (there is no range check!)
     * @param value new x, y or z component of position vector
     */
    public void setPosition(int id, float value) {
        position[id] = value;
        invalidateVertexCache();
    }

    /**
     * Setter for the rx, ry or rz component of rotation vector. World vertices will be recalculated in the next
     * call to calculateWorldVertices method (cached calculated values are no longer valid).
     * @param id 0 for rx, 1 for ry, 2 for rz (there is no range check!)
     * @param value new rx, ry or rz component of rotation vector
     */
    public void setRotation(int id, float value) {
        rotation[id] = value;
        invalidateVertexCache();
    }

    /**
     * Setter for the sx, sy or sz component of scale vector. World vertices will be recalculated in the next
     * call to calculateWorldVertices method (cached calculated values are no longer valid).
     * @param id 0 for sx, 1 for sy, 2 for sz (there is no range check!)
     * @param value new sx, sy or sz component of scale vector
     */
    public void setScale(int id, float value) {
        scale[id] = value;
        invalidateVertexCache();
    }

    /**
     * Invalidate cached values for world vertices.
     */
    public void invalidateVertexCache() {
        precalculatedVertices = null;
        if (parent != null) parent.invalidateVertexCache();
    }

    /**
     * Calculate world vertices (including nested objects) using local-to-world transformation based on position,
     * rotation and scale. The transformation happens in the following order:
     * <ul>
     *     <li>Scale</li>
     *     <li>Rotate around global X</li>
     *     <li>Rotate around global Y</li>
     *     <li>Rotate around global Z</li>
     *     <li>Translate</li>
     * </ul>
     * @return world vertices of this object and all its nested objects
     */
    public PVector[] calculateWorldVertices() {
        if (precalculatedVertices == null) {
            ArrayList<Pair<PVector[], float[][]>> allVertices = getVerticesWorldTransform();
            int verticesCount = 0;
            for (var pair : allVertices) verticesCount += pair.getFirst().length;
            precalculatedVertices = new PVector[verticesCount];

            int id = 0;
            for (var pair : allVertices)
                for (int i = 0; i < pair.getFirst().length; i++, id++) precalculatedVertices[id] = Matrix3D.toPosition(Matrix3D.multiply(pair.getSecond(), Matrix3D.toVector(pair.getFirst()[i])));
        }

        return precalculatedVertices;
    }

    /**
     * Calculate local-to-world transformation matrix. It gets created in the following order.
     * <ul>
     *     <li>Scale</li>
     *     <li>Rotate around global X</li>
     *     <li>Rotate around global Y</li>
     *     <li>Rotate around global Z</li>
     *     <li>Translate</li>
     * </ul>
     * @return matrix for local-to-world transformation
     */
    public float[][] getTransformMatrix() {
        return Matrix3D.multiply(Matrix3D.getTranslate(position[0], position[1], position[2]), Matrix3D.getRotateZ(rotation[2]), Matrix3D.getRotateY(rotation[1]), Matrix3D.getRotateX(rotation[0]), Matrix3D.getScale(scale[0], scale[1], scale[2]));
    }

    /**
     * Calculate world-to-local transformation matrix. It gets created in the following order.
     * <ul>
     *     <li>Inverse translate</li>
     *     <li>Inverse rotate around global Z</li>
     *     <li>Inverse rotate around global Y</li>
     *     <li>Inverse rotate around global X</li>
     *     <li>Inverse scale</li>
     * </ul>
     * @return matrix for world-to-local transformation
     */
    public float[][] getTransformMatrixInv() {
        return Matrix3D.multiply(Matrix3D.getScale(1/scale[0], 1/scale[1], 1/scale[2]), Matrix3D.getRotateX(-rotation[0]), Matrix3D.getRotateY(-rotation[1]), Matrix3D.getRotateZ(-rotation[2]), Matrix3D.getTranslate(-position[0], -position[1], -position[2]));
    }

    /**
     * Get edges of this object and all nested objects. Function will calculate resulting value during the first call, after that it will always return cached value.
     * @return edges of this object and all nested objects
     */
    public int[][] getEdgesAll() {
        if (precalculatedAllEdges == null) {
            int[][] localEdges = getEdges();
            int edgesCount = localEdges.length;
            Object3D[] nested = getNested();
            int[][][] nestedEdges = new int[nested.length][][];
            for (int i = 0; i < nested.length; i++) {
                nestedEdges[i] = nested[i].getEdgesAll();
                edgesCount += nestedEdges[i].length;
            }

            precalculatedAllEdges = new int[edgesCount][];
            int id = 0, delta = 0;
            for (int i = 0; i < nested.length; i++) {
                int[][] childEdges = nestedEdges[i];
                for (int j = 0; j < childEdges.length; j++, id++) {
                    precalculatedAllEdges[id] = new int[childEdges[j].length];
                    if (childEdges[j].length >= 4) precalculatedAllEdges[id][3] = childEdges[j][3];
                    if (childEdges[j].length >= 3) precalculatedAllEdges[id][2] = childEdges[j][2];
                    precalculatedAllEdges[id][0] = childEdges[j][0] + delta;
                    precalculatedAllEdges[id][1] = childEdges[j][1] + delta;
                }
                delta += nested[i].calculateWorldVertices().length;
            }
            for (int i = 0; i < localEdges.length; i++, id++) {
                precalculatedAllEdges[id] = new int[localEdges[i].length];
                if (localEdges[i].length >= 4) precalculatedAllEdges[id][3] = localEdges[i][3];
                if (localEdges[i].length >= 3) precalculatedAllEdges[id][2] = localEdges[i][2];
                precalculatedAllEdges[id][0] = localEdges[i][0] + delta;
                precalculatedAllEdges[id][1] = localEdges[i][1] + delta;
            }
        }

        return precalculatedAllEdges;
    }

    /**
     * Get all vertices of this object and all nested objects paired with corresponding local-to-world transformation
     * matrices.
     * @return list of pairs, each containing a list of vertices and corresponding local-to-world transformation matrix
     */
    private ArrayList<Pair<PVector[], float[][]>> getVerticesWorldTransform() {
        Object3D[] nested = getNested();
        ArrayList<Pair<PVector[], float[][]>> result = new ArrayList<>();

        for (var child : nested) result.addAll(child.getVerticesWorldTransform());

        for (var pair : result) pair.setSecond(Matrix3D.multiply(getTransformMatrix(), pair.getSecond()));

        result.add(new Pair<>(getVertices(), getTransformMatrix()));

        return result;
    }
}