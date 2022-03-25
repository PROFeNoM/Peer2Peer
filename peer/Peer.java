import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Peer {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

    }

    public String sendMessage(String msg) {
        out.println(msg);
        String response;
        try {
            response = in.readLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            response = "rien";
            System.exit(-1);
        }
        return response;
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public String getHash(String fileName) {
        MessageDigest md;
        String myHash;

        try {
            md = MessageDigest.getInstance("MD5");                  // Static getInstance method is called with hashing MD5
            byte[] digest = md.digest(fileName.getBytes());         // digest() method is called to calculate message digest of an input digest() return array of byte    
            BigInteger no = new BigInteger(1, digest);       // Convert byte array into signum representation
            myHash = no.toString(16);                      // Convert message digest into hex value
            while (myHash.length() < 32) {
                myHash = "0" + myHash;
            }
        }catch (NoSuchAlgorithmException e) {
            myHash= "rien";
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return myHash;
    }
}
