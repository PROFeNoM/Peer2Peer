package peer.src.main;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class Parser {
    // Parse user input
    public static String[] parseInput(String input) {
        String[] tokens = input.split(" ", 3);

        String command = tokens[0];

        switch (command) {
            case "neighbourhood":
                if (tokens.length < 3) {
                    Logger.error(Parser.class.getSimpleName(), "Invalid command: " + input);
                    return null;
                }
                String version = tokens[2];
                return new String[] { "neighbourhood", "FileShare", version };
            case "exit":
                return new String[] { "exit" };
            default:
                System.out.println("Unknown command, available commands: neighbourhood, exit");
                return null;
        }
    }

    public static void parseUDPMessage(String message, MulticastPeerServer socket) {
        String[] tokens = message.split(" ");
        String command = tokens[0];
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

        switch (command) {
            case "neighbourhood":
                // neighbourhood FileShare <version>
                if (args.length < 2) {
                    Logger.error(Parser.class.getSimpleName(), "Invalid command: " + message);
                } else {
                    String version = args[1];
                    Peer peer = socket.getPeer();
                    String peerVersion = peer.getVersion();
                    int peerPort = peer.getPort();

                    if (version.equals(peerVersion)) {
                        // Response: neighbour "FileShare" <version> <port>
                        try {
                            socket.sendUDPMessage("neighbour \"FileShare\" " + peerVersion + " " + peerPort);
                        } catch (Exception e) {
                            Logger.error(Parser.class.getSimpleName(), "Failed to send UDP message: " + e.getMessage());
                        }
                    }
                }
        }
    }

    // Parse a response and call the appropriate method
    public static void parseTrackerResponse(String response, Peer peer) {
        String[] tokens = response.split("[ \\[\\]]");
        String command = tokens[0];
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);
        String key = "";

        switch (command) {
            case "ok":
            case "peers":
                break;
            case "list":
                for (int i = 1; i + 3 < args.length; i += 4) {
                    String fileName = args[i];
                    int fileLength = Integer.parseInt(args[i + 1]);
                    int pieceSize = Integer.parseInt(args[i + 2]);
                    String fileKey = args[i + 3];
                    SeedManager.getInstance().addSeed(fileKey, fileName, fileLength, pieceSize);
                }
                break;
            default:
                System.err.println("Received unknown command from tracker: " + command);
                break;
        }
    }

    public static BufferMap parseInterested(String response, String key) {
        Logger.log(Parser.class.getSimpleName(), "Parsing remote peer response: " + response);
        String[] tokens = response.split("[ \\[\\]]");
        if (tokens.length < 2) {
            System.err.println("Received invalid response from remote peer: " + response);
            return null;
        }

        String command = tokens[0];

        if (!"have".equals(command)) {
            Logger.warn("Received unknown command from peer: " + command);
            return null;
        }

        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);
        String receivedKey = args[0];

        if (!receivedKey.equals(key)) {
            Logger.warn("Received wrong key from peer: " + receivedKey);
            return null;
        }

        return new BufferMap(Integer.parseInt(args[1]));
    }

    // Parse a request and call the appropriate method
    public static void parseRequest(String request, ClientHandler clientHandler) {
        Logger.log(Parser.class.getSimpleName(), "Parsing request: " + request);
        String[] tokens = request.split("[ \\[\\]]");
        String command = tokens[0];
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

        switch (command) {
            case "interested":
                String key = args[0];
                clientHandler.interested(key);
                break;
            case "getpieces":
                key = args[0];
                int[] indices = new int[args.length - 2];
                for (int i = 2; i < args.length; i++) {
                    indices[i - 2] = Integer.parseInt(args[i]);
                }
                clientHandler.sendPieces(key, indices);
                break;
            default:
                Logger.error(Parser.class.getSimpleName(), "Received unknown command from peer: " + command);
                break;
        }
    }

    public static Map<Integer, byte[]> parsePieces(String response, String key, BufferMap bufferMap) {
        Logger.log(Parser.class.getSimpleName(), "Parsing pieces response: " + response);
        String[] tokens = response.split("[ \\[\\]]");
        String command = tokens[0];

        if (!"data".equals(command)) {
            Logger.error(Parser.class.getSimpleName(), "Received invalid command from peer: " + command);
            return null;
        }

        if (!key.equals(tokens[1])) {
            Logger.error(Parser.class.getSimpleName(), "Received pieces for wrong file");
            return null;
        }

        Map<Integer, byte[]> pieces = new HashMap<>();
        for (int i = 3; i < tokens.length; i++) {
            String[] data = tokens[i].split(":");
            int index = Integer.parseInt(data[0]);
            String hex = data[1];
            byte[] bytes = new byte[hex.length() / 2];
            for (int j = 0; j < hex.length(); j += 2) {
                bytes[j / 2] = (byte) Integer.parseInt(hex.substring(j, j + 2), 16);
            }
            pieces.put(index, bytes);
        }

        return pieces;
    }

    public static PeerInfo[] parsePeers(String response, String key) {
        Logger.log(Parser.class.getSimpleName(), "Parsing peers response: " + response);
        String[] tokens = response.split(" ", 3);

        if (tokens.length < 3) {
            Logger.error(Parser.class.getSimpleName(), "Received invalid response from tracker: " + response);
            return null;
        }

        String command = tokens[0];
        String receivedKey = tokens[1];
        // TODO check if correct (use JSONArray ?)
        String[] data = tokens[2].substring(1, tokens[2].length() - 1).split(" ");
        if (data[0].isEmpty()) {
            Logger.log(Parser.class.getSimpleName(), "No peers found");
            return new PeerInfo[0];
        }

        if (!"peers".equals(command)) {
            Logger.error(Parser.class.getSimpleName(), "Received invalid command from peer: " + command);
            return null;
        }

        if (!key.equals(receivedKey)) {
            Logger.error(Parser.class.getSimpleName(), "Received peers for wrong file");
            return null;
        }

        PeerInfo[] peers = new PeerInfo[data.length];
        System.out.println(Arrays.toString(data));
        for (int i = 0; i < data.length; i++) {
            String[] peer = data[i].split(":");
            String ip = peer[0];
            int port = Integer.parseInt(peer[1]);
            peers[i] = new PeerInfo(ip, port);
        }

        return peers;
    }
}