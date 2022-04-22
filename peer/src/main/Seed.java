package peer.src.main;

import peer.src.main.util.FileHandler;
import java.io.*;

// Class representing a file to seed
public class Seed {
    String key; // MD5 hash of the file
    BufferMap buffermap; // Buffermap representing the list of chunks in the file
    File file; // File
    int pieceSize = 1024; // Size of each chunk

    public Seed(String path) {
        this.file = openFile(path);
        this.key = FileHandler.getHash(this.file);
        this.buffermap = new BufferMap(file, pieceSize);
    }

    public String getName() {
        return file.getName();
    }

    public String getKey() {
        return key;
    }

    public int getPieceSize() {
        return pieceSize;
    }

    public BufferMap getBuffermap() {
        return buffermap;
    }

    public File getFile() {
        return file;
    }

    private File openFile(String path) {
        File file = new File(path);

        if (!file.exists()) {
            System.out.println("File does not exist");
            System.exit(1);
        }

        return file;
    }

    public String toString() {
        return getName() + " " + file.length() + " " + pieceSize + " " + getKey();
    }
}
