package peer.src.main;

import peer.src.main.util.FileHandler;
import java.io.*;

// Class representing a file to seed
public class Seed {
    String key; // MD5 hash of the file
    String name; // Name of the file
    BufferMap buffermap; // Buffermap representing the list of chunks in the file
    File file; // File
    int pieceSize; // Size of each chunk
    int size; // Size of the file

    // Create a seed from an existing file
    public Seed(File file, int pieceSize) {
        this.file = file;
        this.key = FileHandler.getHash(file);
        this.name = file.getName();
        this.pieceSize = pieceSize;
        this.size = (int) file.length();
        this.buffermap = new BufferMap(size, pieceSize);
    }

    // Create a seed entry from info
    public Seed(String key, String name, int fileSize, int pieceSize) {
        this.file = null;
        this.key = key;
        this.name = name;
        this.pieceSize = pieceSize;
        this.size = fileSize;
        this.buffermap = new BufferMap(size, pieceSize);
    }

    public String getName() {
        return name;
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

    public String toString() {
        return getName() + " " + file.length() + " " + pieceSize + " " + getKey();
    }
}
