package eu.medek.linerenderer3d;

import eu.medek.linerenderer3d.camera.Camera;
import eu.medek.linerenderer3d.camera.controllers.RotatingCameraController;
import eu.medek.linerenderer3d.objects.examples.Bench;
import eu.medek.linerenderer3d.objects.examples.Box;
import eu.medek.linerenderer3d.objects.examples.RecurTree;
import eu.medek.linerenderer3d.system.KeyController;
import processing.core.PApplet;
import processing.core.PVector;

public class Entry extends PApplet {
    private static final float PERIOD = 10;
    private static final float SPEED = 0.03f;

    private World world;
    private Camera camera;
    private KeyController keyController = new KeyController(false);
    private RecurTree tree = new RecurTree(new float[]{0, 0, 0}, new float[]{0,0,0}, new float[]{1,1,1}, true);
    private RotatingCameraController cameraController = new RotatingCameraController(new PVector(0,-0.5f,0), 1.5f, -.5f, PERIOD);

    private long millisStart = System.currentTimeMillis();

    @Override
    public void settings() {
        size(1000,660);
    }

    @Override
    public void setup() {
        world = new World(this);
        camera = new Camera(new float[]{0,0,0}, new float[]{0,0,0});
        world.addObject(new Box(new float[]{0, -0.5f, 0}, new float[]{/*PI/3,PI/5*/0,0,0}, new float[]{1,1,1}));
        world.addObject(tree);
        world.addObject(new Bench(new float[]{0, 0, -.125f}, new float[]{0,0,0}, new float[]{.3f,.3f,.3f}));
    }

    @Override
    public void draw() {
        handleCameraMovement();

        background(0);
        stroke(255);
        if (keyController.isToggled('p') ^ keyController.isToggled('P')) world.draw(camera, keyController.isToggled('k')^keyController.isToggled('K'), (keyController.isToggled('j')^keyController.isToggled('J'))?2000:-1);
        else world.drawBackToFront(camera);

        if (keyController.isToggled('u') ^ keyController.isToggled('U')) {
            stroke(255);
            strokeWeight(1);
            line(width/2f-10, height/2f-10, width/2f+10, height/2f+10);
            line(width/2f+10, height/2f-10, width/2f-10, height/2f+10);
        }
    }

    void handleCameraMovement() {
        if (!(keyController.isToggled('i') ^ keyController.isToggled('I'))) {
            float localSpeed = (keyController.isPressed('l') ^ keyController.isToggled('L')) ? SPEED*3 : SPEED;
            PVector forward = new PVector(0,0,1);
            forward = camera.getTransformedVector(forward);
            forward.mult(localSpeed);

            PVector left = new PVector(-1,0,0);
            left = camera.getTransformedVector(left);
            left.mult(localSpeed);

            PVector up = new PVector(0,-1,0);
            up = camera.getTransformedVector(up);
            up.mult(localSpeed);

            float[] cameraPositionFloat = camera.getPosition();

            PVector cameraPosition = new PVector(cameraPositionFloat[0], cameraPositionFloat[1], cameraPositionFloat[2]);

            if (keyController.isPressed('W')) cameraPosition.add(forward);
            if (keyController.isPressed('S')) cameraPosition.sub(forward);
            if (keyController.isPressed('E')) cameraPosition.add(up);
            if (keyController.isPressed('Q')) cameraPosition.sub(up);
            if (keyController.isPressed('A')) cameraPosition.add(left);
            if (keyController.isPressed('D')) cameraPosition.sub(left);

            if (keyController.isPressed('w')) cameraPosition.z+=localSpeed;
            if (keyController.isPressed('s')) cameraPosition.z-=localSpeed;
            if (keyController.isPressed('e')) cameraPosition.y-=localSpeed;
            if (keyController.isPressed('q')) cameraPosition.y+=localSpeed;
            if (keyController.isPressed('a')) cameraPosition.x-=localSpeed;
            if (keyController.isPressed('d')) cameraPosition.x+=localSpeed;

            cameraPositionFloat[0] = cameraPosition.x;
            cameraPositionFloat[1] = cameraPosition.y;
            cameraPositionFloat[2] = cameraPosition.z;


            camera.getRotation()[1] = mouseX*TWO_PI/width-PI;
            camera.getRotation()[0] = -mouseY*TWO_PI/height+PI;
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

    public static void main(String[] args) {
        PApplet.main(Entry.class.getName());
    }
}
