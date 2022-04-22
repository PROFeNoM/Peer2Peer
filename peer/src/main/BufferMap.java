package peer.src.main;

// Class representing a list of chunks in a file
public class BufferMap {
    private int value;
    private int size;

    // We assume that we have all the pieces
    // TODO: We should just store the pieces we have
    public BufferMap(long fileSize, int pieceSize) {
        int nbPieces = (int) Math.ceil(fileSize / (double) pieceSize);
        size = nbPieces;
        value = (1 << nbPieces) - 1;
    }

    public BufferMap(int value) {
        this.value = value;
        size = (int) Math.floor(Math.log(value) / Math.log(2)) + 1;
    }

    // Return a bit telling if we have the piece number `index`
    public int get(int index) {
        return value >> index & 1;
    }

    public int size() {
        return size;
    }

    public String toString() {
        return Integer.toString(value);
    }
}
