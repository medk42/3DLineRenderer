package eu.medek.linerenderer3d;

public interface Renderer {
    /**
     * Get the width of the canvas.
     * @return width of the canvas
     */
    int getWidth();

    /**
     * Get the height of the canvas.
     * @return height of the canvas
     */
    int getHeight();

    /**
     * Set the stroke color.
     * @param r red component (0-255)
     * @param g green component (0-255)
     * @param b blue component (0-255)
     */
    void setStrokeColor(int r, int g, int b);

    /**
     * Set the stroke weight.
     * @param strokeWeight the stroke weight
     */
    void setStrokeWeight(float strokeWeight);

    /**
     * Draw a line between two points. Points should be inside the screen coordinates [0,width)x[0,height) when called
     * from the World class.
     * @param x0 X coordinate of the first point
     * @param y0 Y coordinate of the first point
     * @param x1 X coordinate of the second point
     * @param y1 Y coordinate of the second point
     */
    void line(float x0, float y0, float x1, float y1);
}
