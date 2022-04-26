package peer.src.main;

import java.io.*;

// Class to talk to the tracker
public class TrackerConnection extends Connection {
    TrackerConnection(String ip, int port) throws IOException {
        super(ip, port);
    }

    // Send the initial announce message to the tracker
    public void announce(int peerPort) throws RuntimeException {
        String message = "announce";
        message += " listen " + peerPort;
        message += " seed " + SeedManager.getInstance().seedsToString();
        message += " leech " + SeedManager.getInstance().leechesToString();

        sendMessage(message);
        String response = getMessage();

        if (Parser.getMessageType(response) != Command.OK) {
            throw new RuntimeException("Failed to announce to tracker: " + response);
        }
    }

    // Get all peers that have the file of the given key
    public ConnectionInfo[] getPeers(String key) {
        String message = "getfile " + key;

        sendMessage(message);
        String response = getMessage();

        if (Parser.getMessageType(response) != Command.PEERS) {
            Logger.error(getClass().getSimpleName(),
                    "Received unexpected response from tracker: " + response);
            return null;
        }

        String receivedKey = Parser.parseKey(response);
        if (!key.equals(receivedKey)) {
            Logger.error(getClass().getSimpleName(), "Received wrong key from tracker: " + receivedKey);
            return null;
        }

        ConnectionInfo[] peers = Parser.parsePeers(response);
        return peers;
    }

    /**
     * Make a search request to the tracker.
     * 
     * @param searchQuery The query to search for.
     * @return The list of files that match the query.
     */
    public String[] look(String searchQuery) {
        String message = "look " + searchQuery;
        ;
        sendMessage(message);
        String response = getMessage();
        System.out.println("> " + response.toString());

        if (Parser.getMessageType(response) != Command.LIST) {
            Logger.error(getClass().getSimpleName(),
                    "Received unexpected response from tracker: " + response);
            return null;
        }

        return Parser.parseSearchResult(response);
    }
}
