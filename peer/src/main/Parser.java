package peer.src.main;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class Parser {

    // Parse a response and call the appropriate method
    public static void parseTrackerResponse(String response, Peer peer) {
        Logger.log(Parser.class.getSimpleName(), "Parsing tracker response: " + response);
        String[] tokens = response.split("[ \\[\\]]");
        String command = tokens[0];
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);
        String key = "";

        switch (command) {
            case "peers":
                key = args[0];
                String[] peers = new String[args.length - 2];
                for (int i = 2; i < args.length; i++) {
                    peers[i - 2] = args[i];
                }
                peer.getFile(key, peers);
                break;
            case "ok":
            case "list":
                break;
            default:
                System.err.println("Received unknown command from tracker: " + command);
                break;
        }
    }

    public static void parsePeerResponse(String response, Peer peer, PeerConnection remotePeer) {
        Logger.log(Parser.class.getSimpleName(), "Parsing remote peer response: " + response);
        String[] tokens = response.split("[ \\[\\]]");
        String command = tokens[0];
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);
        String key = "";
        switch (command) {
            case "have":
                if (args[0] == "not") {
                    Logger.log(Parser.class.getSimpleName(), "Peer does not have file");
                    break;
                }
                key = args[0];
                BufferMap bufferMap = new BufferMap(Integer.parseInt(args[1]));
                Map<Integer, byte[]> pieces = peer.getPieces(remotePeer, key, bufferMap);
                SeedManager.writePieces(key, pieces);
                break;
        }
    }

    // Parse a request and call the appropriate method
    public static void parseRequest(String request, ClientHandler clientHandler) {
        Logger.log(Parser.class.getSimpleName(), "Parsing request: " + request);
        String[] splited = request.split("[ \\[\\]]");
        String command = splited[0];
        String[] args = Arrays.copyOfRange(splited, 1, splited.length);

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

        Map<Integer, byte[]> pieces = new HashMap<Integer, byte[]>();
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
}