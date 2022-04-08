import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Peer {
    private Socket clientSocket;
    private Socket trackerSocket;
    private ServerSocket serverSocket;
    private PrintWriter out;
    private BufferedReader in;

    public Peer() {}

    public void start(String trackerIp, int trackerPort, int peerPort) {  
        connectToTracker(trackerIp, trackerPort);
        startServer(peerPort);
    }

    // Connect to the tracker
    public void connectToTracker(String ip, int port) {
        try {
            trackerSocket = new Socket(ip, port);
            out = new PrintWriter(trackerSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(trackerSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.out.println("Cannot connect to tracker: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Cannot connect to tracker: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Connected to tracker on port " + port);
    }

    // Connect to a peer
    public void connectToPeer(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.out.println("Cannot connect to peer: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Cannot connect to peer: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Connected to peer on port " + port);
    }

    // a function that starts a server on given port
    void startServer(int port) {
        if (serverSocket != null) {
            System.out.println("Server already started");
            return;
        }

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public void stop() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
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
            System.exit(1);
        }
        return response;
    }

    public String getHash(File file) {
        MessageDigest md;
        StringBuilder myHash;

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
            ;

            fis.close();

            // Get the hash's bytes
            byte[] bytes = md.digest();

            // Convert bytes to hexadecimal format
            myHash = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                myHash.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return myHash.toString();
    }
}
