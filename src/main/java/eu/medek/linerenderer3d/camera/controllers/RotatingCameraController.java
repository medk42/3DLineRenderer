package eu.medek.linerenderer3d.camera.controllers;

import eu.medek.linerenderer3d.camera.Camera;
import processing.core.PApplet;
import processing.core.PVector;

public class RotatingCameraController {
    private PVector center;
    private float radius, deltaHeight;
    private float period;

    public RotatingCameraController(PVector center, float radius, float deltaHeight, float period) {
        this.center = center.copy();
        this.radius = radius;
        this.deltaHeight = deltaHeight;
        this.period = period;
    }

    public void setupCamera(Camera camera, float time) {
        float angle = (time/period % 1) * PApplet.TWO_PI;
        PVector newPositionDelta2D = PVector.fromAngle(angle).normalize().mult(radius);

        float[] cameraPosition = camera.getPosition();
        float[] cameraRotation = camera.getRotation();

        cameraPosition[0] = center.x + newPositionDelta2D.x;
        cameraPosition[2] = center.z + newPositionDelta2D.y;
        cameraPosition[1] = center.y + deltaHeight;
        cameraRotation[0] = PApplet.atan(deltaHeight/radius);
        cameraRotation[2] = 0;
        cameraRotation[1] = -angle - PApplet.HALF_PI;
    }
}
