package eu.medek.linerenderer3d.camera;

import eu.medek.linerenderer3d.math.Matrix3D;
import processing.core.PVector;

public class Camera {
    private float[] position;
    private float[] rotation;

    public Camera(float[] position, float[] rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public float[] getPosition() {
        return position;
    }
    public float[] getRotation() {
        return rotation;
    }

    public float[][] calculateToCameraMatrix() {
        return Matrix3D.multiply(Matrix3D.getRotateX(-rotation[0]), Matrix3D.getRotateY(-rotation[1]), Matrix3D.getRotateZ(-rotation[2]), Matrix3D.getTranslate(-position[0], -position[1], -position[2]));
    }

    public PVector getTransformedVector(PVector forward) {
        float[][] rotationMatrix = Matrix3D.multiply(Matrix3D.getRotateZ(rotation[2]), Matrix3D.getRotateY(rotation[1]), Matrix3D.getRotateX(rotation[0]));
        forward = Matrix3D.toPosition(Matrix3D.multiply(rotationMatrix, Matrix3D.toVector(forward)));

        return forward;
    }

}
