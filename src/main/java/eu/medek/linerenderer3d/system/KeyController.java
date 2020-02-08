package eu.medek.linerenderer3d.system;

/**
 * Class for containing information about pressed or toggled keys. Object gets information using
 * {@link #keyPressed} and {@link #keyReleased} methods and provides {@link #isPressed} and
 * {@link #isToggled} methods. Supports keys 0-255.
 */
public class KeyController {
    /**
     * Array containing information about which keys are pressed.
     */
    private boolean[] pressedKeys = new boolean[256];

    /**
     * Array containing information about which keys are toggled.
     */
    private boolean[] toggledKeys = new boolean[256];

    /**
     * Debug mode toggle. If true, object prints the pressed/released key to console.
     */
    private boolean debugLogging;

    /**
     * Basic constructor.
     * @param debugLogging true to enable logging of pressed/released key to console
     */
    public KeyController(boolean debugLogging) {
        this.debugLogging = debugLogging;
    }

    /**
     * Returns whether certain key is pressed. In other words whether {@link #keyPressed} has been called and
     * {@link #keyReleased} has not been called since. If supplied with value greater than 255, returns false.
     * @param keyc key to check
     * @param ignoreCase true to ignore case - for keyc='a' return true if 'a' or 'A' is pressed
     * @return true if {@link #keyPressed} has been called and {@link #keyReleased} has not been called since for keyc
     */
    public boolean isPressed(char keyc, boolean ignoreCase) {
        if (keyc >= pressedKeys.length) return false;
        if (ignoreCase) {
            char other = (Character.toLowerCase(keyc) != keyc)?Character.toLowerCase(keyc):Character.toUpperCase(keyc);
            return pressedKeys[keyc] || pressedKeys[other];
        }
        return pressedKeys[keyc];
    }

    /**
     * Returns whether certain key is toggled. In other words whether the number of {@link #keyReleased} calls for that
     * key is odd. If supplied with value greater than 255, returns false.
     * @param keyc key to check
     * @param ignoreCase true to ignore case - for keyc='a' return true if only 'a' or 'A' is toggled
     * @return true if the number of {@link #keyReleased} calls for that key is odd
     */
    public boolean isToggled(char keyc, boolean ignoreCase) {
        if (keyc >= toggledKeys.length) return false;
        if (ignoreCase) {
            char other = (Character.toLowerCase(keyc) != keyc)?Character.toLowerCase(keyc):Character.toUpperCase(keyc);
            return toggledKeys[keyc] ^ toggledKeys[other];
        }
        return toggledKeys[keyc];
    }

    /**
     * Notify object that a certain key is pressed. Does range check. Prints the pressed key if debugging is enabled.
     * @param keyc pressed key
     */
    public void keyPressed(char keyc) {
        if (keyc >= pressedKeys.length) return;
        pressedKeys[keyc] = true;
        if (debugLogging) System.out.println(keyc + " pressed");
    }

    /**
     * Notify object that a certain key is released. Does range check. Prints the released key if debugging is enabled.
     * @param keyc released key
     */
    public void keyReleased(char keyc) {
        if (keyc >= pressedKeys.length) return;
        pressedKeys[keyc] = false;
        toggledKeys[keyc] = !toggledKeys[keyc];
        if (debugLogging) System.out.println(keyc + " released");
    }
}