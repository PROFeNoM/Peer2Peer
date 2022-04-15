package peer.src.main;

import peer.src.main.util.FileHandler;
import java.io.*;

public class Seed {
    String key;
    String buffermap;
    File file;

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

    private File openFile(String path) {
        File file = new File(path);
        return file;
    }

    public String computeBufferMap() {
        return "";
    }

    public String toString() {
        return getName() + " " + file.length() + " " + getKey();
    }
}
