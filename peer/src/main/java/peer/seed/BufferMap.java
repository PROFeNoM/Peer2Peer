package peer.seed;

/**
 * Class representing a list of chunks in a file.
 */
public class BufferMap {
    /**
     * Value of the buffermap.
     */
    private int value;

    /**
     * Number of chunks in the file.
     */
    private int size;

    /**
     * Create a new buffermap representing a file with the given size `fileSize`,
     * separated into pieces of size `pieceSize` each.
     * We assume that we have all or no pieces.
     * TODO: We should just store the pieces we have.
     * 
     * @param fileSize  Size of the file.
     * @param pieceSize Size of each piece.
     * @param have Boolean telling if we have all or no pieces.
     */
    public BufferMap(long fileSize, int pieceSize, boolean have) {
        int nbPieces = (int) Math.ceil(fileSize / (double) pieceSize);
        size = nbPieces;
        if (have) {
            value = (1 << nbPieces) - 1;
        } else {
            value = 0;
        }
    }

    /**
     * Create a new buffermap with the given value.
     * 
     * @param value Value of the buffermap.
     */
    public BufferMap(int value) {
        this.value = value;
        size = value == 0 ? 0 : (int) Math.floor(Math.log(value) / Math.log(2)) + 1;
    }

    /**
     *  Check if we have the piece number `index`.
     * 
     * @param index Index of the piece to check.
     * @return True if we have the piece number `index`, false otherwise.
     */
    public boolean has(int index) {
        return (value >> index & 1) == 1;
    }

    /**
     * Set the piece number `index` to have or not have.
     * 
     * @param index Index of the piece to set.
     * @param have  True if we have the piece number `index`, false otherwise.
     */
    public void set(int index, boolean have) {
        if (have) {
            value |= 1 << index;
        } else {
            value &= ~(1 << index);
        }
    }

    /**
     * Get the size of the buffermap, i.e the number of chunks in the file.
     * 
     * @return Size of the buffermap.
     */
    public int size() {
        return size;
    }

    /**
     * Check if the buffermap is empty, i.e if we have no pieces.
     * 
     * @return True if the we have no piece, false otherwise.
     */
    public boolean isEmpty() {
        return value == 0;
    }

    /**
     * Check if the buffermap is full, i.e if we have all the pieces.
     * 
     * @return True if the we have all the pieces, false otherwise.
     */
    public boolean isFull() {
        return value == (1 << size) - 1;
    }

    /**
     * Get the string representation of the buffermap.
     * 
     * @return String representation of the buffermap.
     */
    public String toString() {
        return Integer.toString(value);
    }
}
