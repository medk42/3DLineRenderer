package eu.medek.linerenderer3d.camera.controllers;

import eu.medek.linerenderer3d.camera.Camera;
import eu.medek.linerenderer3d.system.Vector;

/**
 * Class for moving a Camera in a circle around certain point.
 */
public class RotatingCameraController {
    /**
     * Point to rotate the camera around.
     */
    private Vector center;

    /**
     * Horizontal distance from the center - radius of the circle.
     */
    private float radius;

    /**
     * Vertical distance from the center.
     */
    private float deltaHeight;

    /**
     * Time to complete one circle.
     */
    private float period;

    /**
     * Create the rotating camera controller. Rotates camera around the center looking at the center.
     * @param center point to rotate the camera around
     * @param radius horizontal distance from the center - radius of the circle
     * @param deltaHeight vertical distance from the center
     * @param period time to complete one circle
     */
    public RotatingCameraController(Vector center, float radius, float deltaHeight, float period) {
        this.center = new Vector(center);
        this.radius = radius;
        this.deltaHeight = deltaHeight;
        this.period = period;
    }

    /**
     * Set camera position and rotation to reflect the requested time.
     * @param camera camera to set up
     * @param time time for which to set the camera up
     */
    public void setupCamera(Camera camera, float time) {
        float angle = (time/period % 1) * (float) Math.PI * 2;
        Vector newPositionDelta2D = Vector.fromAngle(angle).normalize().mult(radius);

        float[] cameraPosition = camera.getPosition();
        float[] cameraRotation = camera.getRotation();

        cameraPosition[0] = center.x + newPositionDelta2D.x;
        cameraPosition[2] = center.z + newPositionDelta2D.y;
        cameraPosition[1] = center.y + deltaHeight;
        cameraRotation[0] = (float)Math.atan(deltaHeight/radius);
        cameraRotation[2] = 0;
        cameraRotation[1] = angle - (float)Math.PI/2;
    }
}
