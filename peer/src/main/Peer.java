package peer.src.main;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

// Peer class
public class Peer {
    private Socket clientSocket;
    private Tracker tracker;
    private PeerServer peerServer;
    private SeedManager seedManager;

    // Find files to seed, connect to the tracker and start the peer server
    public void start(String trackerIp, int trackerPort, int peerPort, String seedFolder) {
        tracker = new Tracker(trackerIp, trackerPort);
        tracker.connect();
        seedManager = new SeedManager(seedFolder);
        tracker.announce(peerPort, seedManager.seedsToString(), seedManager.leechesToString());
        startServer(peerPort);
    }

    // Start a server on given port
    void startServer(int port) {
        if (peerServer != null) {
            Logger.warn(getClass().getSimpleName(), "Server already started");
            return;
        }

        try {
            peerServer = new PeerServer(port);
            peerServer.start();
            Logger.log(getClass().getSimpleName(), "Server started on port " + port);
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Cannot start server: " + e.getMessage());
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
                        String response = tracker.sendMessage(inputLine);
                        System.out.println("> " + response);
                    } else {
                        System.out.print("< ");
                    }
                }
            }
        } catch (Exception e) {
            Logger.error(getClass().getSimpleName(), e.getMessage());
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                Logger.error(getClass().getSimpleName(), e.getMessage());
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
            tracker.stop();
            peerServer.close();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Error while stopping peer: " + e.getMessage());
            System.exit(1);
        }
    }

    // Connect to a peer on the given ip and port
    public void connectToPeer(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            Logger.error(getClass().getSimpleName(), "Cannot connect to peer: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Cannot connect to peer: " + e.getMessage());
            System.exit(1);
        }
        Logger.log(getClass().getSimpleName(), "Connected to peer on port " + port);
    }

    void processResponse(String response) {
        List<Object> parsed = Parser.parse(response);
        String method = (String) parsed.get(0);
        List<String> args = (List<String>) parsed.get(1);

        switch(method){
            case "interested":
                interested((String) args.get(0));
                break;
            
            case "have":
                break;

            case "getPieces":
                break;

            case "data":
                break;
        
            case "peers":
                break;
        }
    }

    void sendMessageToPeer(String msg) {
    }

    void interested(String key) {
        for (Seed seed : seedManager.getSeeds()) {
            if (seed.getKey().equals(key)) {
                sendMessageToPeer("have " + seed.getKey() + " " + seed.getBuffermap());
                return;
            }
        }
        sendMessageToPeer("have not");
    }

    void have(String key, ArrayList<Integer> buffermap) {
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        for (Seed seed : seedManager.getSeeds()) {
            if (seed.getKey().equals(key)) {
                for (int i = 0; i < seed.getBuffermap().size(); i++) {
                    if (seed.getBuffermap().get(i) == 0 && buffermap.get(i) == 1) {
                        indexes.add(i);
                    }
                }
            }
        }
        sendMessageToPeer("getpieces" + " " + indexes.stream().map(Object::toString).collect(Collectors.joining(" ")));
    }

    void getPieces(String key, ArrayList<Integer> indexes) {
        ArrayList<String> bytes = new ArrayList<String>();
        String seedKey;
        for (Seed seed : seedManager.getSeeds()) {
            if (seed.getKey().equals(key)) {
                seedKey = seed.getKey();
                for (int id : indexes)
                    try {
                        FileInputStream fis = new FileInputStream(seed.getName());
                        byte[] byteArray = new byte[seed.getPieceSize()];
                        int bytesCount = fis.read(byteArray, seed.getPieceSize() * indexes.get(id),
                                seed.getPieceSize());
                        bytes.add(id + ":" + byteArray);
                    } catch (Exception e) {
                        Logger.error(getClass().getSimpleName(), e.getMessage());
                        throw new RuntimeException(e);
                    }

                sendMessageToPeer(
                        "data" + " " + seedKey + " "
                                + bytes.stream().map(Object::toString).collect(Collectors.joining(" ")));
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
}
