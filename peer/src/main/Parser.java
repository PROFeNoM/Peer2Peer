package peer.src.main;

import java.util.Arrays;

class Parser {

    // Parse a response and call the appropriate method
    public static void parseResponse(String response, Peer peer) {
        Logger.log(Parser.class.getSimpleName(), "Parsing response: " + response);
        String[] tokens = response.split("[ \\[\\]]");
        String command = tokens[0];
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

        switch (command) {
            case "peers":
                String key = args[0];
                String[] peers = new String[args.length - 2];
                for (int i = 2; i < args.length; i++) {
                    peers[i - 2] = args[i];
                }
                peer.getFile(key, peers);
            case "ok":
            case "list":
                break;
            default:
                System.err.println("Unknown command: " + command);
                break;
        }
    }

    // Parse a request and call the appropriate method
    public static void parseRequest(String request, ClientHandler client) {
        Logger.log(Parser.class.getSimpleName(), "Parsing request: " + request);
        String[] splited = request.split("[ \\[\\]]");
        String command = splited[0];
        String[] args = Arrays.copyOfRange(splited, 1, splited.length);

        switch(command) {
            default:
                break;
        }
    }
}