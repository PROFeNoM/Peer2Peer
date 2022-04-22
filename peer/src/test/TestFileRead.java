package peer.src.test;

import java.io.*;
import peer.src.main.*;

public class TestFileRead {
    public static void main(String[] args) throws Exception {
        File file = new File("peer/seeds/test.txt");
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[3];
        fis.getChannel().position(9);
        int byteRead = fis.read(bytes, 0, 3);
        File output = new File("peer/seeds/output.txt");
        RandomAccessFile raf2;
        if (!output.exists()) {
            output.createNewFile();
            raf2 = new RandomAccessFile(output, "rw");
            raf2.setLength(27);
            raf2.seek(9);
            raf2.write(bytes, 0, byteRead);
            raf2.close();
        }
        FileInputStream fis2 = new FileInputStream(output);
        byte[] bytes2 = new byte[3];
        byte[] bytes3 = new byte[3];
        int readDebut = fis2.read(bytes2, 0, 3);
        fis2.getChannel().position(9);
        int readFin = fis2.read(bytes3, 0, 3);

        System.out.println("Codes\n Début : " + bytes2 + " \nFin : " + bytes3);
        System.out.println("output\n Début : " + new String(bytes2) + " \nFin : " + new String(bytes3));

    }
}
