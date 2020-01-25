package eu.medek.linerenderer3d.system;

public class KeyController {
    private boolean[] pressedKeys = new boolean[256];
    private boolean[] toggledKeys = new boolean[256];
    private boolean debugLogging;

    public KeyController(boolean debugLogging) {
        this.debugLogging = debugLogging;
    }

    public boolean isPressed(char keyc) {
        return pressedKeys[keyc];
    }

    public boolean isToggled(char keyc) {
        return toggledKeys[keyc];
    }

    public void keyPressed(char keyc) {
        if (keyc >= pressedKeys.length) return;
        pressedKeys[keyc] = true;
        if (debugLogging) System.out.println(keyc + " pressed");
    }

    public void keyReleased(char keyc) {
        if (keyc >= pressedKeys.length) return;
        pressedKeys[keyc] = false;
        toggledKeys[keyc] = !toggledKeys[keyc];
        if (debugLogging) System.out.println(keyc + " released");
    }
}