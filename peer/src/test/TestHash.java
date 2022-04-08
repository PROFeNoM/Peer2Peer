package peer.src.test;

import peer.src.main.util.FileHandler;

import java.io.File;
import java.io.IOException;

class TestHash {
    // Test the hash function
    public void testHash() {
        FileHandler fileHandler = new FileHandler();
        String expectedHash = "d41d8cd98f00b204e9800998ecf8427e";
        File file = new File("test.txt");

        try {
            assert file.createNewFile() : "File already exists";
        } catch (IOException e) {
            System.out.println("Error creating file " + e);
        }

        String hash = fileHandler.getHash(file);
        file.delete();

        assert hash.equals(expectedHash) : "Hash is not correct";
    }
}
