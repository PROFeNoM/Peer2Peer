package peer.src.main;

import java.io.*;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

// Peer class
public class Peer {
    private TrackerConnection tracker;
    private PeerServer peerServer;

    // Connect and announce to the tracker and start the peer server
    public void start(String trackerIp, int trackerPort, int peerPort) {
        tracker = new TrackerConnection(trackerIp, trackerPort);
        tracker.connect();
        tracker.announce(peerPort);
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
        String input;
        while (true) {
            System.out.print("< ");
            try {
                input = in.nextLine();
            } catch (NoSuchElementException e) {
                continue;
            }
            if (input != null && !input.isEmpty()) {
                String[] command = Parser.parseInput(input);
                if (command == null) {
                    continue;
                }
                switch (command[0]) {
                    case "getfile":
                        getFile(command[1]);
                        break;
                    case "look":
                        String response = tracker.sendMessage(command[0] + " " + command[1]);
                        System.out.println("> " + response);
                        Parser.parseTrackerResponse(response, this);
                        break;
                    case "exit":
                        System.out.println("Good bye");
                        in.close();
                        stop();
                        System.exit(0);
                        break;
                }
            }
        }
    }

    // Close the connection to the tracker and stop the server
    public void stop() {
        Logger.log(getClass().getSimpleName(), "Stopping peer");
        tracker.stop();
        peerServer.close();
    }

    // Ask all peers for the file of the given key
    void getFile(String key) {
        PeerInfo[] peers = tracker.getPeers(key);

        for (PeerInfo peerInfo : peers) {
            // TODO: Prevent asking ourselves
            Logger.log("Asking peer " + peerInfo.toString());

            PeerConnection peer = null;
            try {
                peer = new PeerConnection(peerInfo.getIp(), peerInfo.getPort());
            } catch (IOException e) {
                Logger.error(getClass().getSimpleName(),
                        "Cannot connect to peer " + peerInfo.toString() + ": " + e.getMessage());
                continue;
            }

            // Ask the peer for the pieces of the file he has
            BufferMap bufferMap = peer.interested(key);

            // If the peer does not have the file, continue
            if (bufferMap.isEmpty()) {
                Logger.log("Peer " + peerInfo.toString() + " has no pieces of the file");
                peer.stop();
                continue;
            }

            Logger.log("Peer " + peerInfo.toString() + " has " + bufferMap.size() + " pieces of the file");

            Map<Integer, byte[]> pieces = peer.getPieces(key, bufferMap);
            peer.stop();

            SeedManager.getInstance().writePieces(key, pieces);
        }
    }
}
