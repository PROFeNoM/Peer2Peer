package peer.seed;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class TestSeed {
    private Seed emptySeed;
    private Seed fullSeed;
    String key = "key";
    String name = "name";
    private int pieceSize = 3;
    private int numPieces = 10;


    @BeforeEach
    public void setUp(@TempDir File tempDir) throws IOException {
        emptySeed = new Seed(key, name, pieceSize, numPieces);
        File file = new File(tempDir, "file.txt");
        file.createNewFile();
        fullSeed = new Seed(file, pieceSize);
    }

    @Test
    public void testInitialisation() {
        assertTrue(emptySeed.getBufferMap().isEmpty(), "New empty seed should have an empty buffermap");
        assertTrue(fullSeed.getBufferMap().isFull(), "New full seed should have a full buffermap");
    }

    @Test
    public void testWritePieces() {
        byte[] piece = new byte[pieceSize];
        for (int i = 0; i < pieceSize; i++) {
            piece[i] = (byte) i;
        }

        int index = 3;
        assertFalse(emptySeed.getBufferMap().has(index), "Empty should not have a piece at index " + index + " before write");
        emptySeed.writePiece(index, piece);
        assertTrue(emptySeed.getBufferMap().has(index), "Seed should have a piece at index " + index + " after write");

        byte[] pieceRead = new byte[pieceSize];
        int byteRead = emptySeed.readPiece(index, pieceRead);

        assertEquals(pieceSize, byteRead, "Should read the whole piece");
        assertArrayEquals(piece, pieceRead, "Should read the same piece");
    }
}
