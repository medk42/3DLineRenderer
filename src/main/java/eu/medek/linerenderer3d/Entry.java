package eu.medek.linerenderer3d;

import processing.core.PApplet;

public class Entry extends PApplet {

    @Override
    public void settings() {
        size(640, 480);
    }

    @Override
    public void setup() {
        background(0);
        stroke(255);
        rect(100,100,200,300);
    }

    public static void main(String[] args) {
        PApplet.main(Entry.class.getName());
    }
}
