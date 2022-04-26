package peer.seed;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.assertEquals;

public class FileUtilsTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Test
    public void testHash() throws IOException {
        final String expectedHash = "d41d8cd98f00b204e9800998ecf8427e";
        final File file = folder.newFile("file.txt");

        final String actualHash = FileUtils.getMD5Hash(file);

        assertEquals("MD5 hash is not correct", expectedHash, actualHash);
    }
}
