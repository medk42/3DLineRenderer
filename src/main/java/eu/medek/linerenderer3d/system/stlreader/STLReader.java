package eu.medek.linerenderer3d.system.stlreader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class STLReader {
    private Path file;

    public STLReader(Path file) {
        this.file = file;
    }

    public STLTriangle[] readAsBinaryFile() throws IOException {
        byte[] fileBytes = Files.readAllBytes(file);

        int triangleCount = bytesToInt(fileBytes, 80);
        STLTriangle[] triangles = new STLTriangle[triangleCount];

        for (int i = 0, offset = 84; i < triangleCount; i++, offset += 12*4+2) {
            float normalX = bytesToFloat(fileBytes, offset);
            float normalY = bytesToFloat(fileBytes, offset + 4);
            float normalZ = bytesToFloat(fileBytes, offset + 8);
            Vertex p1 = readVertex(fileBytes, offset + 12);
            Vertex p2 = readVertex(fileBytes, offset + 24);
            Vertex p3 = readVertex(fileBytes, offset + 36);
            triangles[i] = new STLTriangle(p1, p2, p3, normalX, normalY, normalZ);
        }

        return triangles;
    }

    private Vertex readVertex(byte[] array, int offset) {
        float x = bytesToFloat(array, offset);
        float y = bytesToFloat(array, offset + 4);
        float z = bytesToFloat(array, offset + 8);
        return new Vertex(x, y, z);
    }

    private static int bytesToInt(byte[] array, int offset) {
        return  (array[3 + offset]<<24)&0xff000000|
                (array[2 + offset]<<16)&0x00ff0000|
                (array[1 + offset]<< 8)&0x0000ff00|
                (array[0 + offset])&0x000000ff;
    }

    private static float bytesToFloat(byte[] array, int offset) {
        int intValue = bytesToInt(array, offset);
        return Float.intBitsToFloat(intValue);
    }

    public static void main(String[] args) throws IOException {
        Path path = Path.of(args[0]);
        STLReader reader = new STLReader(path);
        STLTriangle[] triangles = reader.readAsBinaryFile();
        for (STLTriangle triangle : triangles) {
            System.out.println(triangle);
        }
    }
}
