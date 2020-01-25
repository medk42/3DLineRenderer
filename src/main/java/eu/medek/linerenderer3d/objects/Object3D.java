package eu.medek.linerenderer3d.objects;

import eu.medek.linerenderer3d.math.Matrix3D;
import processing.core.PVector;

public abstract class Object3D {
    private float[] position;
    private float[] rotation;
    private float[] scale;

    private PVector[] precalculatedVerticies = null;

    public Object3D(float[] position, float[] rotation, float[] scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public abstract PVector[] getVertices();
    public abstract int[][] getEdges();


    public float getPosition(int id) {
        return position[id];
    }
    public float getRotation(int id) {
        return rotation[id];
    }
    public float getScale(int id) {
        return scale[id];
    }

    public void setPosition(int id, float value) {
        position[id] = value;
        invalidateCache();
    }
    public void setRotation(int id, float value) {
        rotation[id] = value;
        invalidateCache();
    }
    public void setScale(int id, float value) {
        scale[id] = value;
        invalidateCache();
    }

    public void invalidateCache() {
        precalculatedVerticies = null;
    }

    public PVector[] calculateWorldVertices() {
        if (precalculatedVerticies == null) {
            PVector[] vertices = getVertices();
            precalculatedVerticies = new PVector[vertices.length];

            float[][] toWorldMatrix = Matrix3D.multiply(Matrix3D.getTranslate(position[0], position[1], position[2]), Matrix3D.getRotateZ(rotation[2]), Matrix3D.getRotateY(rotation[1]), Matrix3D.getRotateX(rotation[0]), Matrix3D.getScale(scale[0], scale[1], scale[2]));
            for (int i = 0; i < vertices.length; i++) precalculatedVerticies[i] = Matrix3D.toPosition(Matrix3D.multiply(toWorldMatrix, Matrix3D.toVector(vertices[i])));
        }

        return precalculatedVerticies;
    }
}