package eu.medek.linerenderer3d.system.stlreader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 * Class used for parsing STL files.
 */
public class STLReader {
    /**
     * Path to the file to be parsed.
     */
    private Path file;

    /**
     * Basic constructor
     * @param file file to be parsed
     */
    public STLReader(Path file) {
        this.file = file;
    }

    /**
     * Try to determine whether specified file is binary or text and read it as such.
     * @return triangles specified in the file
     * @throws IOException if any error arises while reading the file
     */
    public STLTriangle[] tryRead() throws IOException {
        byte[] fileBytes = Files.readAllBytes(file);
        if (fileBytes.length < 84) return readAsTextFile();

        int triangleCount = bytesToInt(fileBytes, 80);

        if (fileBytes.length == 84 + 50*triangleCount) return readAsBinaryFile();
        else return readAsTextFile();
    }

    /**
     * Read the specified file as a binary file.
     * @return triangles specified in the file
     * @throws IOException if any error arises while reading the file
     */
    public STLTriangle[] readAsBinaryFile() throws IOException {
        byte[] fileBytes = Files.readAllBytes(file);

        int triangleCount = bytesToInt(fileBytes, 80);
        STLTriangle[] triangles = new STLTriangle[triangleCount];

        for (int i = 0, offset = 84; i < triangleCount; i++, offset += 12*4+2) {
            float normalX = bytesToFloat(fileBytes, offset);
            float normalY = bytesToFloat(fileBytes, offset + 4);
            float normalZ = bytesToFloat(fileBytes, offset + 8);
            Vertex p1 = readBinaryVertex(fileBytes, offset + 12);
            Vertex p2 = readBinaryVertex(fileBytes, offset + 24);
            Vertex p3 = readBinaryVertex(fileBytes, offset + 36);
            triangles[i] = new STLTriangle(p1, p2, p3, normalX, normalY, normalZ);
        }

        return triangles;
    }

    /**
     * Read the specified file as a text file.
     * @return triangles specified in the file
     * @throws FileNotFoundException if any error arises while reading the file
     */
    public STLTriangle[] readAsTextFile() throws FileNotFoundException {
        Scanner fileScanner = new Scanner(new FileInputStream(file.toFile()));
        fileScanner.useLocale(Locale.US);

        ArrayList<STLTriangle> trianglesList = new ArrayList<>();

        while (fileScanner.hasNext()) {
            String token = fileScanner.next();
            if ("facet".equals(token)) {
                trianglesList.add(readTextTriangle(fileScanner));
            }
        }

        return trianglesList.toArray(new STLTriangle[0]);
    }

    /**
     * Read and create the next triangle from the text file.
     * @param fileScanner scanner reading the text file
     * @return next triangle
     */
    private STLTriangle readTextTriangle(Scanner fileScanner) {
        String token;
        ArrayList<Vertex> vertices = new ArrayList<>(3);
        float normalX = 0, normalY = 0, normalZ = 0;
        while (fileScanner.hasNext() && !"endfacet".equals(token = fileScanner.next())) {
            switch (token) {
                case "normal":
                    normalX = fileScanner.nextFloat();
                    normalY = fileScanner.nextFloat();
                    normalZ = fileScanner.nextFloat();
                    break;
                case "vertex":
                    float x = fileScanner.nextFloat();
                    float y = fileScanner.nextFloat();
                    float z = fileScanner.nextFloat();
                    vertices.add(new Vertex(x, y, z));
                    break;
            }
        }

        return new STLTriangle(vertices.get(0), vertices.get(1), vertices.get(2), normalX, normalY, normalZ);
    }

    /**
     * Read a vertex/point from the binary file.
     * @param array array containing the file in byte format
     * @param offset where to start reading
     * @return vertex read from the binary file at specified offset
     */
    private Vertex readBinaryVertex(byte[] array, int offset) {
        float x = bytesToFloat(array, offset);
        float y = bytesToFloat(array, offset + 4);
        float z = bytesToFloat(array, offset + 8);
        return new Vertex(x, y, z);
    }

    /**
     * Convert 4 bytes into int.
     * @param array byte array
     * @param offset where to start reading
     * @return integer read from the array at specified offset in little endian
     */
    private static int bytesToInt(byte[] array, int offset) {
        return  (array[3 + offset]<<24)&0xff000000|
                (array[2 + offset]<<16)&0x00ff0000|
                (array[1 + offset]<< 8)&0x0000ff00|
                (array[offset])&0x000000ff;
    }

    /**
     * Convert 4 bytes into float.
     * @param array byte array
     * @param offset where to start reading
     * @return float read from the array at specified offset in little endian
     */
    private static float bytesToFloat(byte[] array, int offset) {
        int intValue = bytesToInt(array, offset);
        return Float.intBitsToFloat(intValue);
    }

    /**
     * Quick test of the functionality of this class. Prints triangles parsed from the file specified as an argument.
     * @param args path to some STL file
     * @throws IOException if any error arises while reading the file
     */
    public static void main(String[] args) throws IOException {
        Path path = Paths.get(args[0]);
        STLReader reader = new STLReader(path);
        STLTriangle[] triangles = reader.tryRead();
        for (STLTriangle triangle : triangles) {
            System.out.println(triangle);
        }
    }
}
