package peer.seed;

import org.junit.Test;
import static org.junit.Assert.*;

public class BufferMapTest {
    @Test
    public void testEmptyBufferMap() {
        BufferMap bufferMap = new BufferMap(0);

        assertTrue("Empty BufferMap should be empty", bufferMap.isEmpty());
        assertEquals("Empty BufferMap should have a size of 0", 0, bufferMap.size());
        assertFalse("Empty BufferMap should not have any piece", bufferMap.has(0) || bufferMap.has(5));
    }
}
