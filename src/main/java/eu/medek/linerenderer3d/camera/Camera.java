package eu.medek.linerenderer3d.camera;

import eu.medek.linerenderer3d.system.Matrix3D;
import eu.medek.linerenderer3d.system.Vector;

/**
 * Class representing the position and rotation of camera.
 */
public class Camera {
    /**
     * Position of the camera.
     */
    private float[] position;

    /**
     * Rotation of the camera.
     */
    private float[] rotation;

    /**
     * Create a camera at a specific position with a specified rotation.
     * @param position position to put the camera
     * @param rotation rotation of the camera - around global X, then global Y, then global Z
     */
    public Camera(float[] position, float[] rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    /**
     * Getter for the position of the camera.
     * @return position of the camera
     */
    public float[] getPosition() {
        return position;
    }

    /**
     * Getter for the rotation of the camera.
     * @return rotation of the camera
     */
    public float[] getRotation() {
        return rotation;
    }

    /**
     * Calculate transformation matrix from world coordinates into camera coordinates.
     * @return transformation matrix from world coordinates into camera coordinates
     */
    public float[][] calculateToCameraMatrix() {
        return Matrix3D.multiply(Matrix3D.getRotateX(-rotation[0]), Matrix3D.getRotateY(-rotation[1]), Matrix3D.getRotateZ(-rotation[2]), Matrix3D.getTranslate(-position[0], -position[1], -position[2]));
    }

    /**
     * Calculate where does a certain vector rotate. Useful for finding forward, up and right vectors of the Camera.
     * @param forward input Vector in world coordinates
     * @return a new Vector created by rotating input vector with the camera rotation
     */
    public Vector getTransformedVector(Vector forward) {
        float[][] rotationMatrix = Matrix3D.multiply(Matrix3D.getRotateZ(rotation[2]), Matrix3D.getRotateY(rotation[1]), Matrix3D.getRotateX(rotation[0]));
        forward = Matrix3D.toPosition(Matrix3D.multiply(rotationMatrix, Matrix3D.toVector(forward)));

        return forward;
    }

}
