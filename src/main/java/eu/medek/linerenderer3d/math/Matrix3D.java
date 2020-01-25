package eu.medek.linerenderer3d.math;

import processing.core.PApplet;
import processing.core.PVector;

/**
  First value: row
  Second value: column
*/
class Matrix3D {
    public static float[][] getTranslate(float dx, float dy, float dz) {
        return new float[][] {{1,0,0,dx}, {0,1,0,dy}, {0,0,1,dz}, {0,0,0,1}};
    }

    public static float[][] getRotateX(float angle) {
        return new float[][] {{1,0,0,0}, {0, PApplet.cos(angle),-PApplet.sin(angle),0}, {0,PApplet.sin(angle),PApplet.cos(angle),0}, {0,0,0,1}};
    }

    public static float[][] getRotateY(float angle) {
        return new float[][] {{PApplet.cos(angle),0,PApplet.sin(angle),0}, {0,1,0,0}, {-PApplet.sin(angle),0,PApplet.cos(angle),0}, {0,0,0,1}};
    }

    public static float[][] getRotateZ(float angle) {
        return new float[][] {{PApplet.cos(angle),-PApplet.sin(angle),0,0}, {PApplet.sin(angle),PApplet.cos(angle),0,0}, {0,0,1,0}, {0,0,0,1}};
    }

    public static float[][] getScale(float sx, float sy, float sz) {
        return new float[][] {{sx,0,0,0}, {0,sy,0,0}, {0,0,sz,0}, {0,0,0,1}};
    }

    public static float[][] multiply(float[][] matrixA, float[][] matrixB) {
        float[][] result = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    result[i][j] += matrixA[i][k]*matrixB[k][j];
                }
            }
        }
        return result;
    }

    public static float[] multiply(float[][]matrix, float[] vector) {
        float[] result = new float[4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
        }
        return result;
    }
    public static float[][] multiply(float[][] first, float[][]... others) {
        float[][] result = first;
        for (float[][] other : others) result = multiply(result, other);
        return result;
    }

    public static float[] toVector(PVector position) {
        return new float[]{position.x, position.y, position.z, 1};
    }

    public static PVector toPosition(float[] vector) {
        return new PVector(vector[0]/vector[3], vector[1]/vector[3], vector[2]/vector[3]);
    }
}