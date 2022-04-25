package peer.src.main;

// Class representing a list of chunks in a file
public class BufferMap {
    private int value;
    private int size;

    // We assume that we have all the pieces
    // TODO: We should just store the pieces we have
    public BufferMap(long fileSize, int pieceSize, boolean have) {
        int nbPieces = (int) Math.ceil(fileSize / (double) pieceSize);
        size = nbPieces;
        if (have) {
            value = (1 << nbPieces) - 1;
        } else {
            value = 0;
        }
    }

    public BufferMap(int value) {
        this.value = value;
        size = (int) Math.floor(Math.log(value) / Math.log(2)) + 1;
    }

    // Return a boolean telling if we have the piece number `index`
    public boolean has(int index) {
        return (value >> index & 1) == 1;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public String toString() {
        return Integer.toString(value);
    }
}
