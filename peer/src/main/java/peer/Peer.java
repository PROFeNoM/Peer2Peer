package peer;

import peer.connection.ConnectionInfo;
import peer.connection.PeerConnection;
import peer.connection.TrackerConnection;
import peer.seed.BufferMap;
import peer.seed.SeedManager;
import peer.server.PeerServer;
import peer.util.Logger;

import java.io.*;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The Peer class is the main class of the peer.
 * It is responsible for etablishing a connection for the tracker,
 * starting the peer server and handling the user input.
 * It can make a search query to the tracker (look) and download a file (getfile).
 */
public class Peer {
    private TrackerConnection tracker;
    private PeerServer peerServer;

    /**
     * Connect and announce to the tracker and start the peer server.
     * 
     * @param trackerIp
     * @param trackerPort
     * @param peerPort
     */
    public void start(String trackerIp, int trackerPort, int peerPort) {
        try {
            tracker = new TrackerConnection(trackerIp, trackerPort);
            tracker.announce(peerPort);
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Failed to connect to tracker: " + e.getMessage());
            System.exit(1);
        } catch (RuntimeException e) {
            Logger.error(getClass().getSimpleName(), "Failed to announce to tracker: " + e.getMessage());
            System.exit(1);
        }

        Logger.log(getClass().getSimpleName(), "Announced to tracker");

        try {
            startServer(peerPort);
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Cannot start server: " + e.getMessage());
            System.exit(1);
        }
        Logger.log(getClass().getSimpleName(), "Server started on port " + peerPort);
    }

    /**
     * Run the user command line interface.
     */
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

            if (input == null || input.isEmpty()) {
                continue;
            }

            Command command = Parser.getMessageType(input);
            switch (command) {
                case LOOK:
                    String searchQuery = Parser.parseSearchQuery(input);
                    look(searchQuery);
                    break;
                case GETFILE:
                    String key = Parser.parseKey(input);
                    getFile(key);
                    break;
                case EXIT:
                    System.out.println("Good bye");
                    in.close();
                    stop();
                    System.exit(0);
                    break;
                default:
                    Logger.warn(getClass().getSimpleName(),
                            "Unknown command: " + command + ", available commands: look, getfile, exit");
                    break;
            }
        }
    }

    /**
     * Close the connection to the tracker and stop the server.
     */
    public void stop() {
        Logger.log(getClass().getSimpleName(), "Stopping application");

        try {
            tracker.stop();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(),
                    "Error while stopping connection to the tracker: " + e.getMessage());
        }

        try {
            peerServer.close();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(),
                    "Error while stopping the peer server: " + e.getMessage());
        }
    }

    /**
     * Start the peer server on given port.
     * 
     * @param port
     */
    private void startServer(int port) throws IOException {
        if (peerServer != null) {
            Logger.warn(getClass().getSimpleName(), "Server already started");
            return;
        }

        peerServer = new PeerServer(port);
        peerServer.start();
    }

    /**
     * Make a search request to the tracker.
     * 
     * @param searchQuery The message containing the search request.
     */
    private void look(String searchQuery) {
        String[] filesInfo = tracker.look(searchQuery);
        for (int i = 0; i + 3 < filesInfo.length; i += 4) {
            String fileName = filesInfo[i];
            int fileLength = Integer.parseInt(filesInfo[i + 1]);
            int pieceSize = Integer.parseInt(filesInfo[i + 2]);
            String fileKey = filesInfo[i + 3];
            SeedManager.getInstance().addSeed(fileKey, fileName, fileLength, pieceSize);
        }
    }

    /**
     * Get a file.
     * 
     * Ask the tracker for the list of peers that have the file,
     * then ask the peers for the pieces of the file they have
     * and save them to the current directory.
     * 
     * @param key The key of the file to get.
     */
    private void getFile(String key) {
        ConnectionInfo[] peers = tracker.getPeers(key);

        for (ConnectionInfo peerInfo : peers) {
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
            BufferMap bufferMap = peer.getBufferMap(key);

            // If the peer does not have the file, continue
            if (bufferMap == null || bufferMap.isEmpty()) {
                Logger.log("Peer " + peerInfo.toString() + " has no pieces of the file");
                try {
                    peer.stop();
                } catch (IOException e) {
                    Logger.error(getClass().getSimpleName(),
                            "Error while stopping connection to peer " + peerInfo.toString() + ": " + e.getMessage());
                }
                continue;
            }

            Logger.log("Peer " + peerInfo.toString() + " has " + bufferMap.size() + " pieces of the file");

            Map<Integer, byte[]> pieces = peer.getPieces(key, bufferMap);

            try {
                peer.stop();
            } catch (IOException e) {
                Logger.error(getClass().getSimpleName(),
                        "Error while stopping connection to peer " + peerInfo.toString() + ": " + e.getMessage());
            }

            if (pieces == null || pieces.isEmpty()) {
                Logger.warn("Failed to get pieces from Peer " + peerInfo.toString());
                continue;
            }

            SeedManager.getInstance().writePieces(key, pieces);
        }
    }
}
