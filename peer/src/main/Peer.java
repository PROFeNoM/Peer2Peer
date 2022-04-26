package peer.src.main;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

// Peer class
public class Peer {
    private MulticastPeerServer multicastPeerServer;
    private PeerServer peerServer;
    private int _peerPort;
    private String _fileShareVersion;

    // Connect and announce to the tracker and start the peer server
    public void start(int peerPort, String fileShareVersion) {
        startServer(peerPort);
        _fileShareVersion = fileShareVersion;
        _peerPort = peerPort;
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
            Logger.error(getClass().getSimpleName(), "Cannot start peer server: " + e.getMessage());
        }
    }

    void _startMulticastPeerServer() {
        if (multicastPeerServer != null) {
            Logger.warn(getClass().getSimpleName(), "Multicast server already started");
            return;
        }

        try {
            multicastPeerServer = new MulticastPeerServer(this);
            multicastPeerServer.start();
            Logger.log(getClass().getSimpleName(), "Multicast server started");
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Cannot start multicast server: " + e.getMessage());
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
                    case "neighbourhood":
                        neighboorhood(command[2]);
                        break;
                    case "exit":
                        System.out.println("Good bye");
                        in.close();
                        stop();
                        System.exit(0);
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

    String getVersion() {
        return _fileShareVersion;
    }

    int getPort() {
        return _peerPort;
    }
}
