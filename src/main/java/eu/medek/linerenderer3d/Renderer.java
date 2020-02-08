package eu.medek.linerenderer3d;

public interface Renderer {
    int getWidth();
    int getHeight();

    void setStrokeColor(int r, int g, int b);
    void setStrokeWeight(float strokeWeight);

    void line(float x0, float y0, float x1, float y1);
}
