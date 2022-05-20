package peer.connection;

import peer.Command;
import peer.Parser;
import peer.seed.SeedManager;
import peer.util.Logger;

import java.io.*;

/**
 * Class to talk to the tracker peer.
 * It can be used to get its buffermap for a specific key (interested),
 * or ask for the pieces he has for a specific key (getpieces).
 */
public class TrackerConnection extends Connection {
    /**
     * Create a new tracker connection with the given ip and port.
     * 
     * @param ip   IP address to connect to.
     * @param port Port to connect to.
     * @throws IOException If an error occurs while creating the connection.
     */
    public TrackerConnection(String ip, int port) throws IOException {
        super(ip, port);
    }

    /**
     *  Send the initial announce message to the tracker.
     * 
     * @param peerPort Port of the peer server.
     * @throws RuntimeException If the tracker do not tell us that we are not registered successfully.
     */
    public void announce(int peerPort) throws RuntimeException {
        String message = "announce";
        message += " listen " + peerPort;
        message += " seed " + SeedManager.getInstance().seedsToString();
        message += " leech " + SeedManager.getInstance().leechesToString();
        
        Logger.log("< " + message);
        sendMessage(message);
        String response = getMessage();

        if (Parser.getMessageType(response) != Command.OK) {
            throw new RuntimeException("Failed to announce to tracker: " + response);
        }
    }

    /**
     * Update the tracker with the current state of the peer.
     */
    public void update() throws RuntimeException {
        String message = "update";
        message += " seed " + SeedManager.getInstance().seedsToString();
        message += " leech " + SeedManager.getInstance().leechesToString();
        
        sendMessage(message);
        String response = getMessage();

        if (Parser.getMessageType(response) != Command.OK) {
            throw new RuntimeException("Failed to update tracker: " + response);
        }
    }

    /**
     * Send a request to the tracker to get all peers that have the file of the given key.
     * 
     * @param key Key of the file to get the peers of.
     * @return List of peers that seeds the file of the given key.
     */
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
