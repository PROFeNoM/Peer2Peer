package peer.src.main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Peer class
public class Peer {
    private Tracker tracker;
    private PeerServer peerServer;

    // Find files to seed, connect to the tracker and start the peer server
    public void start(String trackerIp, int trackerPort, int peerPort) {
        tracker = new Tracker(trackerIp, trackerPort);
        tracker.connect();
        tracker.announce(peerPort, SeedManager.getInstance().seedsToString(), SeedManager.getInstance().leechesToString());
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
                        Parser.parseResponse(response, this);
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
        Logger.log("Stopping peer");
        try {
            tracker.stop();
            peerServer.close();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Error while stopping peer: " + e.getMessage());
            System.exit(1);
        }
    }

    void have(String key, ArrayList<Integer> buffermap) {
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        for (Seed seed : SeedManager.getInstance().getSeeds()) {
            if (seed.getKey().equals(key)) {
                for (int i = 0; i < seed.getBuffermap().size(); i++) {
                    if (seed.getBuffermap().get(i) == 0 && buffermap.get(i) == 1) {
                        indexes.add(i);
                    }
                }
            }
        }
        // sendMessageToPeer("getpieces" + " " + indexes.stream().map(Object::toString).collect(Collectors.joining(" ")));
    }

    void getPieces(String key, ArrayList<Integer> indexes) {
        ArrayList<String> bytes = new ArrayList<String>();
        String seedKey;
        for (Seed seed : SeedManager.getInstance().getSeeds()) {
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

                // sendMessageToPeer("data" + " " + seedKey + " "+ bytes.stream().map(Object::toString).collect(Collectors.joining(" ")));
            }
        }
    }

    void data(List<String> args) {

    }

    // Ask all peers for the file of the given key
    void getFile(String key, String[] peers) {
        Logger.log("Asking peers for the file of key " + key);
        for (String peerInfo : peers) {
            String ip = peerInfo.split(":")[0];
            int port = Integer.parseInt(peerInfo.split(":")[1]);
            Logger.log("Asking peer " + ip + ":" + port);
            RemotePeer peer = new RemotePeer(ip, port);
            peer.connect();
            String response = peer.sendMessage("interested" + " " + key);
            Logger.log("Peer responded " + response);
            peer.close();
        }
    }
}
