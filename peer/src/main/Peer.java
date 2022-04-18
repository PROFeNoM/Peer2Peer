package peer.src.main;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Peer class
public class Peer {
    private Socket clientSocket;
    private Socket trackerSocket;
    private ServerSocket serverSocket;
    private PrintWriter trackerOut;
    private BufferedReader trackerIn;
    private PeerServer peerServer;
    Parser parser;
    private Seed[] files;

    // Connect to the tracker and start the peer server
    public void start(String trackerIp, int trackerPort, int peerPort) {
        connectToTracker(trackerIp, trackerPort);
        startServer(peerPort);
        announceToTracker(peerPort);
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
    }

    // Send the initial announce message to the tracker
    public void announceToTracker(int peerPort) {
        String message = "announce";
        message += " listen " + peerPort;
        message += " seed " + getSeed();
        message += " leech " + getLeech();
        trackerOut.println(message);

        try {
            String response = trackerIn.readLine();
            if (response.equals("ok")) {
                System.out.println("Announced to tracker");
            } else {
                System.out.println("Failed to announce to tracker: " + response);
            }
        } catch (IOException e) {
            System.out.println("Failed to announce to tracker: " + e.getMessage());
            System.exit(1);
        }
    }

    public String getSeed() {
        return "[]";
    }

    public String getLeech() {
        return "[]";
    }

    // Start a server on given port
    void startServer(int port) {
        if (peerServer != null) {
            System.out.println("Server already started");
            return;
        }
        System.out.println("Starting server on port " + port);

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

    // Send a message either to the tracker or to the peers
    public String sendMessage(String msg) {
        return "";
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

    void ProcessResponse() {
        java.lang.reflect.Method method;
        try {
            method = this.getClass().getMethod(parser.method);
            method.invoke(parser.method, parser.args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    void interested(String key) {
        boolean have = false;
        for (Seed file : files) {
            if (file.key.equals(key)) {
                sendMessageToPeer("have " + file.key + " " + file.buffermap);
                have = true;
            }
        }
        if (!have)
            sendMessageToPeer("have not");
    }

    void have(String key, ArrayList<Integer> buffermap) {
        ArrayList<Integer> indexes;
        for (Seed file : files) {
            if (file.key.equals(key)) {
                for (int i ; i < file.buffermap.length ; i++) {
                    if (file.buffermap[i] == 0 && buffermap.get(i) == 1) {
                        indexes.add(i);
                    }
                }
            }
        }
        getPieces(key, indexes);
    }

    void getPieces(String key, ArrayList<Integer> indexes) {
        ArrayList<byte[]> bytes;
        for (Seed file : files) {
            if (file.key.equals(key)) {
                for (int id : indexes)
                    try {
                        FileInputStream fis = new FileInputStream(file.name);
                        byte[] byteArray = new byte[file.pieceSize];
                        int bytesCount = 0;
        
                        bytesCount = fis.read(byteArray, file.pieceSize*indexes.get(id));
                        bytes.add(byteArray);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
            }
        }
    }

    void data(List<String> args) {

    }

    void ok(List<String> args) {
        // do nothing
    }

    void peers(List<String> args) {

    }

    void list(List<String> args) {

    }
}
