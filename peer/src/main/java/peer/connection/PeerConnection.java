package peer.connection;

import peer.Command;
import peer.Parser;
import peer.seed.BufferMap;
import peer.util.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Class to talk to another peer.
 * It can be used to get its buffermap for a specific key (interested),
 * or ask for the pieces he has for a specific key (getpieces).
 */
public class PeerConnection extends Connection {
    /**
     * Create a new peer connection through the given socket.
     * 
     * @param socket Socket to use for the connection.
     * @throws IOException If an error occurs while creating the connection.
     */
    public PeerConnection(Socket socket) throws IOException {
        super(socket);
    }

    /**
     * Create a new peer connection with the given ip and port.
     * 
     * @param ip   IP address to connect to.
     * @param port Port to connect to.
     * @throws IOException If an error occurs while creating the connection.
     */
    public PeerConnection(String ip, int port) throws IOException {
        super(ip, port);
    }

    /**
     * Ask peer for his buffermap of the file with key `key`.
     * 
     * @param key Key of the file to get the buffermap of.
     * @return Buffermap of the file of key `key`.
     */
    public BufferMap getBufferMap(String key) {
        String message = "interested " + key;
        sendMessage(message);
        Logger.log("< " + message);
        String response = getMessage();
        Logger.log("> " + response);

        Command command = Parser.getMessageType(response);
        if (command != Command.HAVE) {
            Logger.warn("Received invalid response from peer");
            return null;
        }

        String receivedKey = Parser.parseKey(response);
        if (!key.equals(receivedKey)) {
            Logger.warn("Received wrong key from peer: " + receivedKey);
            return null;
        }

        BufferMap bufferMap = Parser.parseBufferMap(response);
        return bufferMap;
    }

    /**
     * Ask peer for the pieces of the file of key `key`
     * in the given buffermap `bufferMap`.
     * Indices start at 1.
     * 
     * @param key Key of the file to get the pieces of.
     * @param bufferMap Buffermap of the file of key `key`.
     * @return Map<Integer, byte[]> pieces or null if the response is invalid.
     */
    public Map<Integer, byte[]> getPieces(String key, BufferMap bufferMap, int maxPieces) {
        StringJoiner joiner = new StringJoiner(" ", "[", "]");
        int nbPieces = 0;

        for (int i = 1; i <= bufferMap.size() && nbPieces < maxPieces; i++) {
            if (bufferMap.has(i)) {
                joiner = joiner.add(Integer.toString(i));
                nbPieces++;
            }
        }

        Logger.log(getClass().getSimpleName(), "Asking for " + nbPieces + " pieces");

        String message = "getpieces " + key + " " + joiner.toString();
        sendMessage(message);
        String response = getMessage();

        Command command = Parser.getMessageType(response);
        if (command != Command.DATA) {
            Logger.warn("Received invalid response from peer: " + response);
            return null;
        }

        String receivedKey = Parser.parseKey(response);
        if (!key.equals(receivedKey)) {
            Logger.warn("Received wrong key from peer: " + receivedKey);
            return null;
        }

        Map<Integer, byte[]> pieces = Parser.parsePieces(response);
        return pieces;
    }
}
