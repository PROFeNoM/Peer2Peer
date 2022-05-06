package peer.src.main;

import peer.src.main.util.FileHandler;
import java.io.*;
import java.util.ArrayList;

// Class representing a file to seed
public class Seed {
    String key; // MD5 hash of the file
    String name; // Name of the file
    BufferMap bufferMap; // Buffermap representing the list of chunks in the file
    File file; // File
    int pieceSize; // Size of each chunk
    int size; // Size of the file

    ArrayList<String> seeders; // List of seeders
    ArrayList<String> leechers; // List of leechers

    // Create a seed from an existing file
    public Seed(File file, int pieceSize) {
        this.file = file;
        this.key = FileHandler.getHash(file);
        this.name = file.getName();
        this.pieceSize = pieceSize;
        this.size = (int) file.length();
        this.bufferMap = new BufferMap(size, pieceSize, true);

        seeders = new ArrayList<>();
        leechers = new ArrayList<>();
    }

    // Create a seed entry from info
    public Seed(String key, String name, int fileSize, int pieceSize) {
        // TODO: Check if file exists
        this.file = new File(name);
        this.key = key;
        this.name = name;
        this.pieceSize = pieceSize;
        this.size = fileSize;
        this.bufferMap = new BufferMap(size, pieceSize, false);

        seeders = new ArrayList<>();
        leechers = new ArrayList<>();
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
        return bufferMap;
    }

    public int getSize() {
        return size;
    }

    // Read the piece at index `index` in the given buffer
    // Return the number of bytes read
    public int readPiece(int index, byte[] piece) {
        if (!bufferMap.has(index)) {
            Logger.error(getClass().getSimpleName(), "Trying to read a piece that is not present (piece " + index + ")");
            return -1;
        }

        int byteRead = 0;
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(index * pieceSize);
            byteRead = raf.read(piece);
            raf.close();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Error reading piece from file: " + e.getMessage());
        }

        Logger.log(getClass().getSimpleName(), "Read piece " + index + " from file " + name);

        return byteRead;
    }

    // Write the piece at index `index` from the given buffer
    public void writePiece(int index, byte[] piece) {
        if (bufferMap.has(index)) {
            Logger.error(getClass().getSimpleName(), "Trying to write a piece that is already present (piece " + index + ")");
            return;
        }

        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(index * pieceSize);
            raf.write(piece, 0, piece.length);
            raf.close();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Error writing piece to file: " + e.getMessage());
        }

        Logger.log(getClass().getSimpleName(), "Wrote piece " + index + " to file " + name);
    }

    public void addSeeder(String ip, String port) {
        seeders.add(ip + ":" + port);
    }

    public void addLeecher(String ip, String port) {
        leechers.add(ip + ":" + port);
    }

    public String toString() {
        return getName() + " " + file.length() + " " + pieceSize + " " + getKey();
    }
}
