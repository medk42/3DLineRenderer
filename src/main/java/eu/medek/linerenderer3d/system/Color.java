package eu.medek.linerenderer3d.system;

public class Color {
    public static int fromRGB(int r, int g, int b) {
        return 0xff000000 | (r << 16) | (g << 8) | b;
    }

    public static int getR(int color) {
        return (color >> 16) & 0xff;
    }

    public static int getG(int color) {
        return (color >> 8) & 0xff;
    }

    public static int getB(int color) {
        return color & 0xff;
    }
}
