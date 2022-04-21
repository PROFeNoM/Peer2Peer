package peer.src.main;

import java.util.*;
import peer.src.main.util.FileHandler;
import java.io.*;

// Class representing a file to seed
public class Seed {
    String key; // MD5 hash of the file
    ArrayList<Integer> buffermap; // List of chunks in the file
    File file; // File
    int pieceSize = 1024; // Size of each chunk

    public Seed(String path) {
        this.file = openFile(path);
        this.key = FileHandler.getHash(this.file);
        this.buffermap = computeBufferMap();
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

    public ArrayList<Integer> getBuffermap() {
        return buffermap;
    }

    private File openFile(String path) {
        File file = new File(path);

        if (!file.exists()) {
            System.out.println("File does not exist");
            System.exit(1);
        }

        return file;
    }

    // A function that returns the values where bit number are at 1
    public ArrayList<Integer> computeBufferMap() {
        int bufferMap = 1457;
        int bufferSize = (int) Math.ceil((double) file.length() / pieceSize);
        ArrayList<Integer> bufferMaped = new ArrayList<Integer>();
        while (bufferMap > 0) {
            if ((bufferMap % 2) == 1) {
                bufferMaped.add(1);
            }else {
                bufferMaped.add(0);
            }
//            System.out.println(bufferMaped);
            bufferMap >>= 1;
        }
        return bufferMaped; // trouver un moyen de reverse la liste
        // return Collections.reverse(bufferMaped);
    }

    public String toString() {
        return getName() + " " + file.length() + " " + pieceSize + " " + getKey();
    }
}