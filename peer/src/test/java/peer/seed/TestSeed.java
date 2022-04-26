package peer.seed;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSeed {
    private Seed seed;
    String key = "key";
    String name = "name";
    private int pieceSize = 16384;
    private int numPieces = 10;


    @Before
    public void setUp() {
        seed = new Seed(key, name, pieceSize, numPieces);
    }

    @Test
    public void testInitialisation() {
        assertTrue("New empty seed should have an empty buffermap", seed.getBufferMap().isEmpty());
    }

    @Test
    public void testWritePieces() {
        byte[] piece = new byte[pieceSize];
        for (int i = 0; i < pieceSize; i++) {
            piece[i] = (byte) i;
        }
        seed.writePiece(0, piece);

        assertTrue("Seed should have a piece at index 0", seed.getBufferMap().has(0));

        byte[] pieceRead = new byte[pieceSize];
        int byteRead = seed.readPiece(0, pieceRead);

        assertEquals(pieceSize, byteRead, "Should read the whole piece");
        assertArrayEquals(piece, pieceRead, "Should read the same piece");
    }
}
