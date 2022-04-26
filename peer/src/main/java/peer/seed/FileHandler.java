package peer.seed;

import java.security.MessageDigest;
import java.io.*;

public class FileHandler {
    public static String getHash(File file) {
        MessageDigest md = null;
        StringBuilder hash = new StringBuilder();

        try {
            // Get instance of MD5
            md = MessageDigest.getInstance("MD5");

            // Get file input stream for reading the file content
            FileInputStream fis = new FileInputStream(file);

            // Create byte array to read data in chunks
            byte[] byteArray = new byte[1024];
            int bytesCount = 0;

            // Read file data and update in md
            while ((bytesCount = fis.read(byteArray)) != -1) {
                md.update(byteArray, 0, bytesCount);
            }

            fis.close();

            // Get the hash's bytes
            byte[] bytes = md.digest();

            // Convert bytes to hexadecimal format
            for (int i = 0; i < bytes.length; i++) {
                hash.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return hash.toString();
    }
}
