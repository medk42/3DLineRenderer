package eu.medek.linerenderer3d.system;

/**
 * Class containing methods for generating transformation matrices for certain transformations.
 * <ul>
 *  <li>First value: row</li>
 *  <li>Second value: column</li>
 * </ul>
*/
public class Matrix3D {
    /**
     * Get transformation matrix for moving points.
     * @param dx move in x coordinate
     * @param dy move in y coordinate
     * @param dz move in z coordinate
     * @return matrix to translate (x,y,z) to (x+dx,y+dy,z+dz)
     */
    public static float[][] getTranslate(float dx, float dy, float dz) {
        return new float[][] {{1,0,0,dx}, {0,1,0,dy}, {0,0,1,dz}, {0,0,0,1}};
    }

    /**
     * Get matrix for rotation about the x-axis.
     * @param angle angle to rotate by
     * @return matrix to rotate points about the x-axis by specified angle
     */
    public static float[][] getRotateX(float angle) {
        return new float[][] {{1,0,0,0}, {0, (float)Math.cos(angle),-(float)Math.sin(angle),0}, {0,(float)Math.sin(angle),(float)Math.cos(angle),0}, {0,0,0,1}};
    }

    /**
     * Get matrix for rotation about the y-axis.
     * @param angle angle to rotate by
     * @return matrix to rotate points about the y-axis by specified angle
     */
    public static float[][] getRotateY(float angle) {
        return new float[][] {{(float)Math.cos(angle),0,(float)Math.sin(angle),0}, {0,1,0,0}, {-(float)Math.sin(angle),0,(float)Math.cos(angle),0}, {0,0,0,1}};
    }

    /**
     * Get matrix for rotation about the z-axis.
     * @param angle angle to rotate by
     * @return matrix to rotate points about the z-axis by specified angle
     */
    public static float[][] getRotateZ(float angle) {
        return new float[][] {{(float)Math.cos(angle),-(float)Math.sin(angle),0,0}, {(float)Math.sin(angle),(float)Math.cos(angle),0,0}, {0,0,1,0}, {0,0,0,1}};
    }

    /**
     * Matrix for scaling in all 3 directions.
     * @param sx scale in x coordinate
     * @param sy scale in y coordinate
     * @param sz scale in z coordinate
     * @return matrix to scale (x,y,z) to (sx*x, sy*y, sz*z)
     */
    public static float[][] getScale(float sx, float sy, float sz) {
        return new float[][] {{sx,0,0,0}, {0,sy,0,0}, {0,0,sz,0}, {0,0,0,1}};
    }

    /**
     * Multiply two matrices - in other words, chain transformations. Dimensions need to match, there is no dimension check!
     * @param matrixA first matrix
     * @param matrixB second matrix
     * @return the result of multiplying matrixA by matrix B from the right i.e. <code>matrixA * matrixB</code>
     */
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

    /**
     * Multiply a vector by a matrix from the left - in other words apply transformation specified by the matrix to the
     * vector. Dimensions need to match, there is no dimension check!
     * @param matrix matrix containing transformation
     * @param vector vector to be transformed
     * @return vector created by applying the matrix transformation to the input vector
     */
    public static float[] multiply(float[][]matrix, float[] vector) {
        float[] result = new float[4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
        }
        return result;
    }

    /**
     * Chain multiple transformations in one call.
     * @param first first matrix
     * @param others matrices to multiply the first matrix by
     * @return the resulting transformation matrix created by multiplying from left to right
     */
    public static float[][] multiply(float[][] first, float[][]... others) {
        float[][] result = first;
        for (float[][] other : others) result = multiply(result, other);
        return result;
    }

    /**
     * Create a 4D vector that can be used in matrix transformations from the Vector object.
     * @param position the Vector object (3D vector)
     * @return 4D vector that can be used in matrix transformations
     */
    public static float[] toVector(Vector position) {
        return new float[]{position.x, position.y, position.z, 1};
    }

    /**
     * Convert 4D vector back to the 3D Vector object by dividing by the last coordinate.
     * @param vector 4D vector
     * @return new Vector object represented by the 4D vector
     */
    public static Vector toPosition(float[] vector) {
        return new Vector(vector[0]/vector[3], vector[1]/vector[3], vector[2]/vector[3]);
    }
}