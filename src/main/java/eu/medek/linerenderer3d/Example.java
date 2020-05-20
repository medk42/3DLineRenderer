package eu.medek.linerenderer3d;

import com.jogamp.newt.opengl.GLWindow;
import eu.medek.linerenderer3d.camera.Camera;
import eu.medek.linerenderer3d.camera.controllers.RotatingCameraController;
import eu.medek.linerenderer3d.objects.examples.STLObject;
import eu.medek.linerenderer3d.objects.examples.*;
import eu.medek.linerenderer3d.system.KeyController;
import eu.medek.linerenderer3d.system.Vector;
import processing.core.PApplet;
import processing.core.PVector;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Example usage of the 3DLineRenderer project - a demo scene that is explorable with mouse and keyboard. If filepath
 * to an STL file is provided as command line argument, the STL file will be rendered instead.
 */
public class Example extends PApplet {
    /**
     * Time period for {@link RotatingCameraController}.
     */
    private static final float PERIOD = 10;

    /**
     * Default movement speed - distance per frame (speed varies with framerate as it is not based on delta time).
     */
    private static final float SPEED = 0.03f;

    /**
     * World object, contains the scene.
     */
    private World world;

    /**
     * Camera position and rotation.
     */
    private Camera camera;

    /**
     * Camera controller for rotating the camera around the center of the scene.
     */
    private RotatingCameraController cameraController = new RotatingCameraController(new Vector(0,-0.5f,0), 1.5f, -.5f, PERIOD);

    /**
     * Controller managing pressed/toggled keys.
     */
    private KeyController keyController = new KeyController(false);

    /**
     * Reference to the RecurTree object in the demo scene, so that it can get reset to a new random tree while running.
     */
    private RecurTree tree;

    /**
     * Reference to the NestedPyramid object in the demo scene, so that it can be animated to show advantages of
     * recursively defined objects (transformation of parent also applies to children).
     */
    private NestedPyramid nestedPyramid;

    /**
     * Used to set mouse position as Processing library doesn't have that functionality by itself.
     */
    private GLWindow mouseMover = null;

    /**
     * true if mouse is currently controlling camera, false otherwise
     */
    private boolean mouseLock = false;

    /**
     * Mouse position in the Processing window to move the mouse back to, when mouse is controlling camera.
     */
    private PVector offset;

    /**
     * true if drawing STL file, false if drawing a demo scene.
     */
    private boolean drawing_stl;

    /**
     * Time when the program started - used in animating {@link Example#nestedPyramid} and rotating the camera using
     * {@link Example#cameraController}.
     */
    private long millisStart = System.currentTimeMillis();

    /**
     * Window setup using the Processing library.
     */
    @Override
    public void settings() {
        size(1000,660, P2D);
    }

    /**
     * Scene setup.
     */
    @Override
    public void setup() {
        // get reference to the OpenGL window, so we can set mouse position
        mouseMover = (GLWindow) surface.getNative();

        // create the world object by supplying an implementation of the Renderer interface using the Processing library
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
                Example.this.line(x0, y0, x1, y1);
            }
        });



        // find out if there is a command line argument
        drawing_stl = args != null && args.length == 1;

        if (drawing_stl) { // if there is, add the STL object to the scene (it is moved half a unit up, so that it sits at y=0)
            // create a camera offset from the center, so the object is visible
            camera = new Camera(new float[]{0,-0.5f,-2}, new float[]{0,0,0});

            try {
                world.addObject(new STLObject(new float[]{0,-0.5f,0}, new float[]{HALF_PI,0,0}, new float[]{1,1,1}, Path.of(args[0]), true));
            } catch (IOException e) {
                System.err.println("Wrong path or file format.");
                exit();
            }
        } else {  // otherwise create a demo scene
            // create a camera at the center of the scene
            camera = new Camera(new float[]{0,0,0}, new float[]{0,0,0});

            tree = new RecurTree(new float[]{0, 0, -2}, new float[]{0,0,0}, new float[]{1,1,1}, true);
            nestedPyramid = new NestedPyramid(new float[]{0, 0, 2}, new float[]{0,0,0}, new float[]{.5f,.5f,.5f}, 5, (float)(Math.PI/6));

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
    }

    /**
     * Drawing the scene every frame.
     */
    @Override
    public void draw() {
        if (!drawing_stl) { // if we are drawing the demo scene, animate the nestedPyramid
            nestedPyramid.updateRecursionAngle((System.currentTimeMillis() - millisStart) / 1000f * TWO_PI / 10);
            world.invalidateCache(); // don't forget to invalidate the world vertex/edge caches, since the vertex positions changed
        }

        // setup camera
        handleCameraMovement();

        // clear the frame to black and set stroke (line) color to white
        background(0);
        stroke(255);

        // get various toggles
        int edgeLimit = 2000;
        boolean edgeLimitActive = keyController.isToggled('j', true);
        boolean debug = keyController.isToggled('k', true);
        boolean crosshair = keyController.isToggled('u', true);
        World.DrawOrder drawOrder = keyController.isToggled('p', true) ? World.DrawOrder.SORT_OBJECTS : World.DrawOrder.SORT_EDGES;

        // draw the scene
        world.draw(camera, edgeLimitActive?edgeLimit:-1, drawOrder);

        // display extra debug information when toggled
        if (debug) {
            boolean rotating = keyController.isToggled('i', true);
            float localSpeed = (keyController.isToggled('l', true)) ? SPEED*3 : SPEED;

            fill(255);
            textSize(15);
            text("Total Vertices: " + world.getVertexCount(), 10, 20);
            text("Total edges: " + world.getEdgeCount(), 10, 40);
            text("Edge limit: " + (edgeLimitActive ? edgeLimit : "not active"), 10, 60);
            text("Draw order (sort by): " + drawOrder, 10, 80);
            text("Rotating around center: " + rotating, 10, 100);
            text("Crosshair shown: " + crosshair, 10, 120);
            text("Speed: " + localSpeed, 10, 140);
            text("FPS: " + frameRate, 10, 160);
        }

        // display crosshair in the middle of frame when toggled
        if (crosshair) {
            stroke(255);
            strokeWeight(1);
            line(width/2f-10, height/2f-10, width/2f+10, height/2f+10);
            line(width/2f+10, height/2f-10, width/2f-10, height/2f+10);
        }
    }

    /**
     * Set camera position and rotation using cameraController or mouse and keyboard.
     */
    void handleCameraMovement() {
        if (keyController.isToggled('i', true)) { // if rotating around the center of the scene is toggled, do so
            // set camera position and rotation at the current time using cameraController
            cameraController.setupCamera(camera, (System.currentTimeMillis() - millisStart)/1000f);
            println("Showing time " + (System.currentTimeMillis() - millisStart)/1000f);
        } else {
            // get speed based on a "fast speed" toggle
            float localSpeed = (keyController.isToggled('l', true)) ? SPEED*3 : SPEED;

            // create a "forward", "left" and "up" vectors from the camera rotation and localSpeed
            Vector forward = new Vector(0,0,1);
            forward = camera.getTransformedVector(forward);
            forward.mult(localSpeed);

            Vector left = new Vector(-1,0,0);
            left = camera.getTransformedVector(left);
            left.mult(localSpeed);

            Vector up = new Vector(0,-1,0);
            up = camera.getTransformedVector(up);
            up.mult(localSpeed);

            // get camera position and put it into a vector
            float[] cameraPositionFloat = camera.getPosition();
            Vector cameraPosition = new Vector(cameraPositionFloat[0], cameraPositionFloat[1], cameraPositionFloat[2]);

            // control the camera using WSADEQ to move in the direction of the axes
            if (keyController.isPressed('W', false)) cameraPosition.z+=localSpeed;
            if (keyController.isPressed('S', false)) cameraPosition.z-=localSpeed;
            if (keyController.isPressed('E', false)) cameraPosition.y-=localSpeed;
            if (keyController.isPressed('Q', false)) cameraPosition.y+=localSpeed;
            if (keyController.isPressed('A', false)) cameraPosition.x-=localSpeed;
            if (keyController.isPressed('D', false)) cameraPosition.x+=localSpeed;

            // control the camera using wsadeq to move in the direction of the camera
            if (keyController.isPressed('w', false)) cameraPosition.add(forward);
            if (keyController.isPressed('s', false)) cameraPosition.sub(forward);
            if (keyController.isPressed('e', false)) cameraPosition.add(up);
            if (keyController.isPressed('q', false)) cameraPosition.sub(up);
            if (keyController.isPressed('a', false)) cameraPosition.add(left);
            if (keyController.isPressed('d', false)) cameraPosition.sub(left);

            // set new camera position
            cameraPositionFloat[0] = cameraPosition.x;
            cameraPositionFloat[1] = cameraPosition.y;
            cameraPositionFloat[2] = cameraPosition.z;


            if (mouseLock && !mouseMover.hasFocus()) {
                mouseMover.setPointerVisible(true);
                mouseLock = false;
            }
            if (mouseLock) { // if mouse is currently controlling the camera, rotate the camera based on mouse movement
                // get the distance from offset position
                PVector mouseDelta = PVector.sub(new PVector(mouseX, mouseY), offset);

                // rotate the camera
                camera.getRotation()[1] += mouseDelta.x * TWO_PI / width;
                camera.getRotation()[0] += -mouseDelta.y * TWO_PI / height;
                camera.getRotation()[0] = constrain(camera.getRotation()[0], -HALF_PI, HALF_PI);

                // move mouse back to the offset position
                mouseMover.warpPointer((int)offset.x, (int)offset.y);
            }
        }
    }

    /**
     * Handle key events from Processing library.
     */
    @Override
    public void keyPressed() {
        // notify the key controller
        keyController.keyPressed(key);

        // create a new tree when space is pressed (and we are drawing the demo scene)
        if (key == ' ' && !drawing_stl) {
            tree.recalculateVertices();
            world.invalidateCache();
        }
    }

    /**
     * Handle key events from Processing library.
     */
    @Override
    public void keyReleased() {
        // notify the key controller
        keyController.keyReleased(key);
    }

    /**
     * Handle mouse events from Processing library.
     */
    @Override
    public void mousePressed() {
        // lock/unlock mouse when pressed
        mouseLock = !mouseLock;
        if (mouseLock) { // set mouse position and make mouse pointer invisible
            offset = new PVector(mouseX, mouseY);
            mouseMover.setPointerVisible(false);
        } else { // make mouse pointer visible
            mouseMover.setPointerVisible(true);
        }
    }

    /**
     * Start the Processing sketch
     */
    public static void main(String[] args) {
        PApplet.main(Example.class.getName(), args);
    }
}
