package eu.medek.linerenderer3d.system;

/**
 * Class containing methods for converting between color and it's integer representation.
 */
public class Color {
    /**
     * Create integer from color components.
     * @param r red component
     * @param g green component
     * @param b blue component
     * @return integer representing the color specified by red, green and blue components - 0xffRRGGBB
     */
    public static int fromRGB(int r, int g, int b) {
        return 0xff000000 | (r << 16) | (g << 8) | b;
    }

    /**
     * Function for extracting the red component from integer representation of a color.
     * @param color integer representation of color
     * @return the red component - 0x??RR????
     */
    public static int getR(int color) {
        return (color >> 16) & 0xff;
    }

    /**
     * Function for extracting the green component from integer representation of a color.
     * @param color integer representation of color
     * @return green component - 0x????GG??
     */
    public static int getG(int color) {
        return (color >> 8) & 0xff;
    }

    /**
     * Function for extracting the blue component from integer representation of a color.
     * @param color integer representation of color
     * @return blue component - 0x??????BB
     */
    public static int getB(int color) {
        return color & 0xff;
    }
}
