import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.util.Scanner;

// Peer class
public class Peer {
    private Socket clientSocket;
    private Socket trackerSocket;
    private ServerSocket serverSocket;
    private PrintWriter trackerOut;
    private BufferedReader trackerIn;
    private PeerServer peerServer;

    public Peer() {
    }

    // Connect to the tracker and start the peer server
    public void start(String trackerIp, int trackerPort, int peerPort) {
        connectToTracker(trackerIp, trackerPort);
        startServer(peerPort);
    }

    // Connect to the tracker on the given ip and port
    public void connectToTracker(String ip, int port) {
        try {
            trackerSocket = new Socket(ip, port);
            trackerOut = new PrintWriter(trackerSocket.getOutputStream(), true);
            trackerIn = new BufferedReader(new InputStreamReader(trackerSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.out.println("Cannot connect to tracker: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Cannot connect to tracker: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Connected to tracker on port " + port);
    }

    // Connect to a peer on the given ip and port
    public void connectToPeer(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.out.println("Cannot connect to peer: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Cannot connect to peer: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Connected to peer on port " + port);
    }

    // Start a server on given port
    void startServer(int port) {
        if (peerServer != null) {
            System.out.println("Server already started");
            return;
        }

        try {
            serverSocket = new ServerSocket(port);
            peerServer = new PeerServer(serverSocket);
            peerServer.start();
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            System.out.println("Cannot start server: " + e.getMessage());
            System.exit(1);
        }
    }

    public void run() {
        Scanner in = new Scanner(System.in);
        try {
            while (true) {
                String inputLine;
                System.out.print("< ");
                if ((inputLine = in.nextLine()) != null) {
                    if (!inputLine.isEmpty()) {
                        if (inputLine.equals("exit")) {
                            System.out.println("Good bye");
                            break;
                        }
                        String response = sendMessageToTracker(inputLine);
                        System.out.println("> " + response);
                    } else {
                        System.out.print("< ");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Close the connection to the tracker and stop the server
    public void stop() {
        try {
            trackerIn.close();
            trackerOut.close();
            trackerSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error while stopping peer: " + e.getMessage());
            System.exit(1);
        }
    }

    // Send a message to the tracker and return the response
    public String sendMessageToTracker(String msg) {
        trackerOut.println(msg);
        String response = "";
        try {
            response = trackerIn.readLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return response;
    }

    // Get the MD5 hash of the given file
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
