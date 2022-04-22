package peer.src.main;

import peer.src.main.util.Configuration;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;

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
        try {
            while (true) {
                System.out.print("< ");
                input = in.nextLine();
                if (input != null && !input.isEmpty()) {
                    if (input.equals("exit")) {
                        System.out.println("Good bye");
                        break;
                    }
                    String response = tracker.sendMessage(input);
                    System.out.println("> " + response);
                    Parser.parseTrackerResponse(response, this);
                }
            }
        } catch (Exception e) {
            Logger.error(getClass().getSimpleName(), e.getMessage());
        }
        in.close();
    }

    // Close the connection to the tracker and stop the server
    public void stop() {
        Logger.log(getClass().getSimpleName(), "Stopping peer");
        tracker.stop();
        peerServer.close();
    }

    // Get pieces from the remote peer
    Map<Integer, byte[]> getPieces(PeerConnection peer, String key, BufferMap bufferMap) {
        StringJoiner joiner = new StringJoiner(" ", "[", "]");
        for (int i = 0; i < bufferMap.size(); i++) {
            if (bufferMap.get(i) == 1) {
                joiner = joiner.add(Integer.toString(i));
            }
        }
        String message = "getpieces " + key + " " + joiner.toString();
        peer.sendMessage(message);
        String response = peer.getMessage();
        return Parser.parsePieces(response, key, bufferMap);
    }

    // Ask all peers for the file of the given key
    void getFile(String key, String[] peers) {
        Logger.log(getClass().getSimpleName(), "Asking peers for the file of key " + key);
        for (String peerInfo : peers) {
            String ip = peerInfo.split(":")[0];
            int port = Integer.parseInt(peerInfo.split(":")[1]);
            if (port == Configuration.getInstance().getPeerPort()) {
                continue;
            }
            Logger.log("Asking peer " + ip + ":" + port);
            Socket socket = null;
            try {
                socket = new Socket(ip, port);
            } catch (Exception e) {
                Logger.error(getClass().getSimpleName(), e.getMessage());
                continue;
            }
            PeerConnection peer = new PeerConnection(socket);
            Logger.log(getClass().getSimpleName(), "Connected to peer");
            peer.sendMessage("interested " + key);
            String response = peer.getMessage();
            Logger.log(getClass().getSimpleName(), "Peer responded " + response);
            Parser.parsePeerResponse(response, this, peer);
            peer.stop();
        }
        Logger.log(getClass().getSimpleName(), "File " + key + " downloaded");
    }
}
