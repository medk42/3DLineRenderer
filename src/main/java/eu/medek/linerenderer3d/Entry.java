package eu.medek.linerenderer3d;

import com.jogamp.newt.opengl.GLWindow;
import eu.medek.linerenderer3d.camera.Camera;
import eu.medek.linerenderer3d.camera.controllers.RotatingCameraController;
import eu.medek.linerenderer3d.objects.STLObject;
import eu.medek.linerenderer3d.objects.examples.*;
import eu.medek.linerenderer3d.system.KeyController;
import eu.medek.linerenderer3d.system.Vector;
import processing.core.PApplet;
import processing.core.PVector;
import java.io.IOException;
import java.nio.file.Path;

public class Entry extends PApplet {
    private static final float PERIOD = 10;
    private static final float SPEED = 0.03f;

    private World world;
    private Camera camera;
    private KeyController keyController = new KeyController(false);
    private RecurTree tree = new RecurTree(new float[]{0, 0, -2}, new float[]{0,0,0}, new float[]{1,1,1}, true);
    private RotatingCameraController cameraController = new RotatingCameraController(new Vector(0,-0.5f,0), 1.5f, -.5f, PERIOD);
    private NestedPyramid nestedPyramid = new NestedPyramid(new float[]{0, 0, 2}, new float[]{0,0,0}, new float[]{.5f,.5f,.5f}, 5, (float)(Math.PI/6));

    private GLWindow mouseMover = null;
    private boolean mouseLock = false;
    private PVector offset;

    private long millisStart = System.currentTimeMillis();

    @Override
    public void settings() {
        size(1000,660, P2D);
    }

    @Override
    public void setup() {
        mouseMover = (GLWindow) surface.getNative();

        world = new World(new Renderer() {
            @Override
            public int getWidth() {
                return width;
            }

            @Override
            public int getHeight() {
                return height;
            }

            @Override
            public void setStrokeColor(int r, int g, int b) {
                stroke(r,g,b);
            }

            @Override
            public void setStrokeWeight(float strokeWeight) {
                strokeWeight(strokeWeight);
            }

            @Override
            public void line(float x0, float y0, float x1, float y1) {
                Entry.this.line(x0, y0, x1, y1);
            }
        });

        camera = new Camera(new float[]{0,0,0}, new float[]{0,0,0});
        world.addObject(new Box(new float[]{0, -0.5f, 0}, new float[]{0,0,0}, new float[]{1,1,1}));
        world.addObject(tree);
        world.addObject(new Bench(new float[]{0, 0, -1}, new float[]{0,0,0}, new float[]{1f,1f,1f}));
        world.addObject(new NestedLines(new float[]{0, 0, 1}, new float[]{0,0,0}, new float[]{1f,1f,1f}, 5));
        world.addObject(nestedPyramid);
        try {
            world.addObject(new STLObject(new float[]{-2,-0.5f,0}, new float[]{HALF_PI,0,0}, new float[]{1,1,1}, Path.of("STLExamples", "Globe.stl"), true));
            world.addObject(new STLObject(new float[]{0,-0.5f,0}, new float[]{HALF_PI,0,0}, new float[]{1,1,1}, Path.of("STLExamples", "Tower.stl"), true));
            world.addObject(new STLObject(new float[]{2,0,-0.25f}, new float[]{HALF_PI,0,0}, new float[]{1,1,1}, Path.of("STLExamples", "NameTagIn.stl"), true));
            world.addObject(new STLObject(new float[]{2,0,0.25f}, new float[]{HALF_PI,0,0}, new float[]{1,1,1}, Path.of("STLExamples", "NameTagOut.stl"), true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw() {
        nestedPyramid.updateRecursionAngle((System.currentTimeMillis() - millisStart)/1000f*TWO_PI/10);
        world.invalidateCache();
        handleCameraMovement();

        background(0);
        stroke(255);

        int edgeLimit = 2000;
        boolean edgeLimitActive = keyController.isToggled('j', true);
        boolean debug = keyController.isToggled('k', true);
        boolean crosshair = keyController.isToggled('u', true);
        World.DrawOrder drawOrder = keyController.isToggled('p', true) ? World.DrawOrder.SORT_OBJECTS : World.DrawOrder.SORT_EDGES;

        world.draw(camera, edgeLimitActive?edgeLimit:-1, drawOrder);

        if (debug) {
            fill(255);
            textSize(15);
            text("Total Vertices: " + world.getVertexCount(), 10, 20);
            text("Total edges: " + world.getEdgeCount(), 10, 40);
            text("Edge limit: " + (edgeLimitActive ? edgeLimit : "not active"), 10, 60);
            text("Draw order (sort by): " + drawOrder, 10, 80);
            text("FPS: " + frameRate, 10, 100);
        }

        if (crosshair) {
            stroke(255);
            strokeWeight(1);
            line(width/2f-10, height/2f-10, width/2f+10, height/2f+10);
            line(width/2f+10, height/2f-10, width/2f-10, height/2f+10);
        }
    }

    void handleCameraMovement() {
        if (!keyController.isToggled('i', true)) {
            float localSpeed = (keyController.isToggled('l', true)) ? SPEED*3 : SPEED;
            Vector forward = new Vector(0,0,1);
            forward = camera.getTransformedVector(forward);
            forward.mult(localSpeed);

            Vector left = new Vector(-1,0,0);
            left = camera.getTransformedVector(left);
            left.mult(localSpeed);

            Vector up = new Vector(0,-1,0);
            up = camera.getTransformedVector(up);
            up.mult(localSpeed);

            float[] cameraPositionFloat = camera.getPosition();

            Vector cameraPosition = new Vector(cameraPositionFloat[0], cameraPositionFloat[1], cameraPositionFloat[2]);

            if (keyController.isPressed('W', false)) cameraPosition.z+=localSpeed;
            if (keyController.isPressed('S', false)) cameraPosition.z-=localSpeed;
            if (keyController.isPressed('E', false)) cameraPosition.y-=localSpeed;
            if (keyController.isPressed('Q', false)) cameraPosition.y+=localSpeed;
            if (keyController.isPressed('A', false)) cameraPosition.x-=localSpeed;
            if (keyController.isPressed('D', false)) cameraPosition.x+=localSpeed;

            if (keyController.isPressed('w', false)) cameraPosition.add(forward);
            if (keyController.isPressed('s', false)) cameraPosition.sub(forward);
            if (keyController.isPressed('e', false)) cameraPosition.add(up);
            if (keyController.isPressed('q', false)) cameraPosition.sub(up);
            if (keyController.isPressed('a', false)) cameraPosition.add(left);
            if (keyController.isPressed('d', false)) cameraPosition.sub(left);

            cameraPositionFloat[0] = cameraPosition.x;
            cameraPositionFloat[1] = cameraPosition.y;
            cameraPositionFloat[2] = cameraPosition.z;


            if (mouseLock) {
                PVector mouseDelta = PVector.sub(new PVector(mouseX, mouseY), offset);
                camera.getRotation()[1] += mouseDelta.x * TWO_PI / width;
                camera.getRotation()[0] += -mouseDelta.y * TWO_PI / height;
                camera.getRotation()[0] = constrain(camera.getRotation()[0], -HALF_PI, HALF_PI);
                mouseMover.warpPointer((int)offset.x, (int)offset.y);
            }
        } else {
            cameraController.setupCamera(camera, (System.currentTimeMillis() - millisStart)/1000f);
            println("Showing time " + (System.currentTimeMillis() - millisStart)/1000f);
        }
    }

    @Override
    public void keyPressed() {
        keyController.keyPressed(key);
        if (key == ' ') {
            tree.recalculateVertices();
            world.invalidateCache();
        }
    }

    @Override
    public void keyReleased() {
        keyController.keyReleased(key);
    }

    @Override
    public void mousePressed() {
        mouseLock = !mouseLock;
        if (mouseLock) {
            offset = new PVector(mouseX, mouseY);
            mouseMover.setPointerVisible(false);
        } else {
            mouseMover.setPointerVisible(true);
        }
    }

    public static void main(String[] args) {
        PApplet.main(Entry.class.getName());
    }
}
