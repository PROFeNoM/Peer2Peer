package peer.seed;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BufferMapTest {
    @Test
    public void testEmptyBufferMap() {
        BufferMap bufferMap = new BufferMap(0);

        assertTrue(bufferMap.isEmpty(), "Empty BufferMap should be empty");
        assertEquals(1, bufferMap.size(), "Empty BufferMap should have a size of 1");
        assertFalse(bufferMap.has(0) || bufferMap.has(5), "Empty BufferMap should not have any piece");
    }
}
