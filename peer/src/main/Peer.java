package peer.src.main;

import java.io.*;
import java.net.*;
import java.util.*;

// Peer class
public class Peer {

    public static final String HOST = "127.0.0.1";

    private MulticastPeerServer multicastPeerServer;
    private PeerServer peerServer;
    private int _peerPort;
    private int _maxPeers;
    private String _fileShareVersion;
    private ArrayList<Integer> neighborsPort;
    //private ArrayList<ClientHandler> neighbours;
    private Map<Integer, ClientHandler> neighborsHandler;
    private Map<String, ArrayList<String>> keysToSeeders;

    // Connect and announce to the tracker and start the peer server
    public void start(int peerPort, String fileShareVersion, int maxPeers) {
        startServer(peerPort);
        _fileShareVersion = fileShareVersion;
        _peerPort = peerPort;
        _maxPeers = maxPeers;
        neighborsPort = new ArrayList<>();
        neighborsHandler = new HashMap<>();
        keysToSeeders = new HashMap<>();
    }

    void _startPeerServer(int port) {
        if (peerServer != null) {
            Logger.warn(getClass().getSimpleName(), "Peer server already started");
            return;
        }

        try {
            peerServer = new PeerServer(port, this);
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
                    case "announce":
                        if (Integer.parseInt(command[2]) == _peerPort)
                            announce(command[2]);
                        else
                            Logger.log(getClass().getSimpleName(), "You can't announce another peer");
                        break;
                    case "look":
                        if (!Peer.HOST.equals(command[3])) {
                            Logger.log(getClass().getSimpleName(), "Wrong IP. You can't look for another peer");
                        } else if (!String.valueOf(_peerPort).equals(command[4])) {
                            Logger.log(getClass().getSimpleName(), "Wrong port. You can't look for another peer");
                        } else {
                            look(command[1], command[2], command[3], command[4]);
                        }
                        break;
                    case "interested":
                        interested(command[1]);
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
        for (Map.Entry<Integer, ClientHandler> entry : neighborsHandler.entrySet()) {
            ClientHandler handler = entry.getValue();
            handler.exit();
        }
    }

    void neighboorhood(String version) {
        try {
            multicastPeerServer.sendUDPMessage("neighbourhood \"FileShare\" " + version);
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Cannot send UDP message: " + e.getMessage());
        }
    }

    void announce(String port) {
        // Iterate randomly over all the neighbours port, create a socket
        // and send the announce message
        List<Integer> _neighborsPort = new ArrayList<>(neighborsPort);
        Collections.shuffle(_neighborsPort);
        Logger.log(getClass().getSimpleName(), "Sending announce message to " + _neighborsPort.size() + " neighbors");
        for (int neighbourPort : _neighborsPort) {
            System.out.println("Sending announce message to " + neighbourPort);
            if (neighborsHandler.size() >= _maxPeers)  // Can't connect to more than _maxPeers
                break;

            if (neighborsHandler.containsKey(neighbourPort))  // Already connected to this neighbour
                continue;

            try {
                Socket socketToPeer = new Socket(Peer.HOST, neighbourPort);
                ClientHandler clientHandler = new ClientHandler(socketToPeer, this);
                this.addNeighbour(neighbourPort, clientHandler);
                Logger.log(getClass().getSimpleName(), "Connected to neighbour " + neighbourPort);
                clientHandler.start();
                clientHandler.announce(port);
            } catch (IOException e) {
                Logger.error(getClass().getSimpleName(), "Cannot connect to peer: " + e.getMessage());
            }
        }
    }

    void look(String criterion, String ttl, String ip, String port) {
        for (Map.Entry<Integer, ClientHandler> entry : neighborsHandler.entrySet()) {
            ClientHandler clientHandler = entry.getValue();
            clientHandler.look(criterion, String.valueOf(Integer.parseInt(ttl) - 1), ip, port);
        }
    }

    void interested(String key) {
        if (keysToSeeders.containsKey(key)) {
            ArrayList<String> seeders = keysToSeeders.get(key);
            for (String seeder : seeders) {
                String[] seederInfo = seeder.split(":");
                int seederPort = Integer.parseInt(seederInfo[1]);
                if (neighborsHandler.containsKey(seederPort)) {
                    ClientHandler clientHandler = neighborsHandler.get(seederPort);
                    clientHandler.interested(key);
                    break;
                }
            }
        }
    }

    String getVersion() {
        return _fileShareVersion;
    }

    int getPort() {
        return _peerPort;
    }

    void addNeighbourPort(int port) {
        neighborsPort.add(port);
    }

    boolean isNeighbourPort(int port) {
        return neighborsPort.contains(port);
    }

    void addNeighbour(int port, ClientHandler neighbour) {
        neighborsHandler.put(port, neighbour);
    }

    MulticastPeerServer getMulticastPeerServer() {
        return multicastPeerServer;
    }

    Map<Integer, ClientHandler> getNeighborsHandler() {
        return neighborsHandler;
    }

    int getMaxPeers() {
        return _maxPeers;
    }

    ArrayList<Integer> getNeighborsPort() {
        return neighborsPort;
    }

    Map<String, ArrayList<String>> getKeysToSeeders() {
        return keysToSeeders;
    }
}
