package peer.seed;

import java.io.*;

import peer.util.Logger;

// Class representing a file to seed
public class Seed {
    /**
     * MD5 hash of the file.
     */
    String key;

    /**
     * Name of the file.
     */
    String name;

    /**
     * Buffermap representing the list of chunks in the file.
     */
    BufferMap bufferMap;

    /**
     * File instance.
     */
    File file;

    /**
     * Size of each chunk in bytes.
     */
    int pieceSize;

    /**
     * Size of the file in bytes.
     */
    long size;

    // Create a seed from an existing file
    public Seed(File file, int pieceSize) {
        this.file = file;
        this.key = FileUtils.getMD5Hash(file);
        this.name = file.getName();
        this.pieceSize = pieceSize;
        this.size = file.length();
        this.bufferMap = new BufferMap(size, pieceSize, true);
    }

    // Create a seed entry from info
    public Seed(String key, String seedFolder, String name, long fileSize, int pieceSize, BufferMap bufferMap) {
        // TODO: Check if file exists
        this.file = new File(seedFolder+"/"+name);
        this.key = key;
        this.name = name;
        this.pieceSize = pieceSize;
        this.size = fileSize;
        this.bufferMap = bufferMap == null ? new BufferMap(size, pieceSize, false) : bufferMap;
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

    public BufferMap getBufferMap() {
        return bufferMap;
    }

    public long getSize() {
        return size;
    }

    // Read the piece at index `index` in the given buffer
    // Return the number of bytes read
    public int readPiece(int index, byte[] piece) {
        if (!bufferMap.has(index)) {
            Logger.error(getClass().getSimpleName(),
                    "Trying to read a piece that is not present (piece " + index + ")");
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
            Logger.error(getClass().getSimpleName(),
                    "Trying to write a piece that is already present (piece " + index + ")");
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

        bufferMap.set(index, true);

        Logger.log(getClass().getSimpleName(), "Wrote piece " + index + " to file " + name);
    }

    public String toString() {
        return getName() + " " + file.length() + " " + pieceSize + " " + getKey();
    }
}
