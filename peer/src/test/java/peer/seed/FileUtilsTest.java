package peer.seed;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

public class FileUtilsTest {
    private static File file;

    @BeforeAll
    public static void setUp(@TempDir File tempDir) throws IOException {
        file = new File(tempDir, "file.txt");
        file.createNewFile();
    }
    
    @Test
    public void testGetMD5Hash() throws IOException {
        final String actualHash = FileUtils.getMD5Hash(file);
        final String expectedHash = "d41d8cd98f00b204e9800998ecf8427e";
        assertEquals(expectedHash, actualHash, "MD5 hash is not correct");
    }
}
