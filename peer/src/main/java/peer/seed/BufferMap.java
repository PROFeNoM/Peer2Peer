package peer.seed;

/**
 * Class representing a list of chunks in a file.
 */
public class BufferMap {
    /**
     * Value of the buffermap.
     */
    private long value;

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
        size = (int) Math.ceil(fileSize / (double) pieceSize);
        value = have ? (1L << size) - 1 : 0;
    }

    /**
     * Create a new buffermap with the given value.
     * 
     * @param value Value of the buffermap.
     */
    public BufferMap(long value) {
        this.value = value;
        this.size = Long.toBinaryString(value).length();
    }

    /**
     *  Check if we have the piece number `index`.
     * 
     * @param index Index of the piece to check.
     * @return True if we have the piece number `index`, false otherwise.
     */
    public boolean has(int index) {
        return (value >> index & 1L) == 1;
    }

    /**
     * Set the piece number `index` to have or not have.
     * 
     * @param index Index of the piece to set.
     * @param have  True if we have the piece number `index`, false otherwise.
     */
    public void set(int index, boolean have) {
        if (have) {
            value |= 1L << index;
        } else {
            value &= ~(1L << index);
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
     * Get the missing buffermap, i.e the buffermap representing
     * the pieces we don't have yet
     * and are available in the other buffermap.
     * 
     * @param other Other buffermap.
     */
    public BufferMap getMissingBufferMap(BufferMap other) {
        if (other == null) {
            return new BufferMap(0);
        }

        return new BufferMap(~value & other.value);
    }

    /**
     * Get the string representation of the buffermap.
     * 
     * @return String representation of the buffermap.
     */
    public String toString() {
        return Long.toString(value);
    }
}
