package peer;

import peer.connection.ConnectionInfo;
import peer.connection.PeerConnection;
import peer.connection.TrackerConnection;
import peer.seed.BufferMap;
import peer.seed.Seed;
import peer.seed.SeedManager;
import peer.server.PeerServer;
import peer.util.Configuration;
import peer.util.Logger;

import java.io.*;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The Peer class is the main class of the peer.
 * It is responsible for etablishing a connection for the tracker,
 * starting the peer server and handling the user input.
 * It can make a search query to the tracker (look) and download a file
 * (getfile).
 */
public class Peer {
    /**
     * Object used to talk to the tracker.
     */
    private TrackerConnection tracker;

    /**
     * Peer server to receive connections from other peers.
     */
    private PeerServer peerServer;

    /**
     * Maximum number of pieces to ask for at a time.
     */
    private static final int MAX_PIECES = Configuration.getInstance().getMaxPieces();

    /**
     * Connect and announce to the tracker and start the peer server.
     * 
     * @param trackerIp   The tracker's ip.
     * @param trackerPort The tracker's port.
     * @param peerPort    The port to listen to.
     */
    public void start(String trackerIp, int trackerPort, int peerPort) {
        try {
            tracker = new TrackerConnection(trackerIp, trackerPort);
            tracker.announce(peerPort);
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Failed to connect to tracker: " + e.getMessage());
            System.exit(1);
        }
        catch (RuntimeException e) {
        Logger.error(getClass().getSimpleName(), "Failed to announce to tracker: " +
        e.getMessage());
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
                    return;
                default:
                    Logger.warn(getClass().getSimpleName(),
                            command + ", available commands: look, getfile, exit");
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
            SeedManager.getInstance().saveLeechs();
            tracker.stop();
        } catch (Exception e) {
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
    public void look(String searchQuery) {
        String[] filesInfo = tracker.look(searchQuery);
        for (int i = 0; i + 3 < filesInfo.length; i += 4) {
            String fileName = filesInfo[i];
            int fileLength = Integer.parseInt(filesInfo[i + 1]);
            int pieceSize = Integer.parseInt(filesInfo[i + 2]);
            String fileKey = filesInfo[i + 3];
            if (SeedManager.getInstance().hasSeed(fileKey)) {
                continue;
            }
            SeedManager.getInstance().addLeech(fileKey, fileName, fileLength, pieceSize);
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
    public void getFile(String key) {
        Seed seed = SeedManager.getInstance().getSeedFromKey(key);
        if (seed == null)
            seed = SeedManager.getInstance().getLeechFromKey(key);

        if (seed == null) {
            Logger.error(getClass().getSimpleName(), "Unknown file key: " + key);
            return;
        }

        if (seed.getBufferMap().isFull()) {
            Logger.warn(getClass().getSimpleName(), "Already have the full file");
            return;
        }

        ConnectionInfo[] peers = tracker.getPeers(key);

        for (ConnectionInfo peerInfo : peers) {
            Logger.log("Asking peer " + peerInfo.toString());

            PeerConnection peer = null;
            try {
                peer = new PeerConnection(peerInfo.getIp(), peerInfo.getPort());
            } catch (IOException e) {
                Logger.error(getClass().getSimpleName(),
                        "Cannot connect to peer " + peerInfo.toString() + ": " + e.getMessage());
                continue;
            }

            BufferMap ownedBufferMap;
            BufferMap peerBufferMap;
            BufferMap missingBufferMap;

            while (true) {
                // Get a buffermap representing the pieces of the file that the peer has and
                // that we don't have yet
                ownedBufferMap = seed.getBufferMap();
                peerBufferMap = peer.getBufferMap(key);
                missingBufferMap = ownedBufferMap.getMissingBufferMap(peerBufferMap);

                if (missingBufferMap.isEmpty()) {
                    // If there is no piece to ask for, continue to the next peer
                    break;
                }

                Map<Integer, byte[]> pieces = peer.getPieces(key, missingBufferMap, MAX_PIECES);

                if (pieces == null || pieces.isEmpty()) {
                    Logger.warn("Failed to get pieces from Peer " + peerInfo.toString());
                    break;
                }

                SeedManager.getInstance().writePieces(key, pieces);
            }
             
            try {
                peer.stop();
            } catch (IOException e) {
                Logger.error(getClass().getSimpleName(),
                        "Error while stopping connection to peer " + peerInfo.toString() + ": " + e.getMessage());
            }

            if (seed.getBufferMap().isFull()) {
                SeedManager.getInstance().leechToSeed(seed);
                Logger.log("File downloaded");
                return;
            }
        }
    }
}
