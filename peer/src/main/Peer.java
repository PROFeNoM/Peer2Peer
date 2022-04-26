package peer.src.main;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

// Peer class
public class Peer {
    private MulticastPeerServer multicastPeerServer;
    private PeerServer peerServer;

    // Connect and announce to the tracker and start the peer server
    public void start(int peerPort) {
        startServer(peerPort);
    }

    void _startPeerServer(int port) {
        if (peerServer != null) {
            Logger.warn(getClass().getSimpleName(), "Peer server already started");
            return;
        }

        try {
            peerServer = new PeerServer(port);
            peerServer.start();
            Logger.log(getClass().getSimpleName(), "Peer Server started on port " + port);
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Cannot start multicast server: " + e.getMessage());
        }
    }

    void _startMulticastPeerServer() {
        if (multicastPeerServer != null) {
            Logger.warn(getClass().getSimpleName(), "Server already started");
            return;
        }

        try {
            multicastPeerServer = new MulticastPeerServer();
            multicastPeerServer.start();
            Logger.log(getClass().getSimpleName(), "Multicast server started");
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Cannot start server: " + e.getMessage());
            System.exit(1);
        }
    }

    // Start a server on given port
    void startServer(int port) {
        _startPeerServer(port);
        _startMulticastPeerServer();
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
                    case "neighbourhood" -> neighboorhood(command[2]);
                    case "exit" -> {
                        System.out.println("Good bye");
                        in.close();
                        stop();
                        System.exit(0);
                    }
                }
            }
        }
    }

    // Close the connection to the tracker and stop the server
    public void stop() {
        Logger.log(getClass().getSimpleName(), "Stopping peer");
        multicastPeerServer.close();
        peerServer.close();
    }

    void neighboorhood(String version) {
        try {
            multicastPeerServer.sendUDPMessage("neighbourhood \"FileShare\" " + version);
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Cannot send UDP message: " + e.getMessage());
        }
    }
}
