package eu.medek.linerenderer3d.camera.controllers;

import eu.medek.linerenderer3d.camera.Camera;
import eu.medek.linerenderer3d.system.Vector;

public class RotatingCameraController {
    private Vector center;
    private float radius, deltaHeight;
    private float period;

    public RotatingCameraController(Vector center, float radius, float deltaHeight, float period) {
        this.center = new Vector(center);
        this.radius = radius;
        this.deltaHeight = deltaHeight;
        this.period = period;
    }

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
