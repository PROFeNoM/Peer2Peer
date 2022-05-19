package peer.src.main;

// Class representing a list of chunks in a file
public class BufferMap {
    private long value;
    private int size;

    // We assume that we have all the pieces
    // TODO: We should just store the pieces we have
    public BufferMap(long fileSize, int pieceSize, boolean have) {
        size = (int) Math.ceil(fileSize / (double) pieceSize);
        value = have ? (1L << size) - 1 : 0;
    }

    public BufferMap(long value) {
        this.value = value;
        size = Long.toBinaryString(value).length();
    }

    // Return a boolean telling if we have the piece number `index`
    public boolean has(int index) {
        return (value >> index & 1L) == 1;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public String toString() {
        return Long.toString(value);
    }
}
