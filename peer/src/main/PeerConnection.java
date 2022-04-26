package peer.src.main;

import java.net.*;
import java.util.Map;
import java.util.StringJoiner;
import java.io.*;

// Class to talk to another peer
public class PeerConnection extends Connection {
    public PeerConnection(Socket socket) throws IOException {
        super(socket);
    }

    public PeerConnection(String ip, int port) throws IOException {
        super(ip, port);
    }

    // Ask peer for his buffermap of the file with key `key`
    public BufferMap getBufferMap(String key) {
        String message = "interested " + key;
        sendMessage(message);
        String response = getMessage();

        Command command = Parser.getMessageType(response);
        if (command != Command.HAVE) {
            Logger.warn("Received invalid response from peer: " + response);
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

    /** Ask the remote peer for the pieces of the file of key `key`
    * in the given buffermap `bufferMap`
    * @param key
    * @param bufferMap
    * @return Map<Integer, byte[]> pieces or null if the response is invalid
    */

    Map<Integer, byte[]> getPieces(String key, BufferMap bufferMap) {
        StringJoiner joiner = new StringJoiner(" ", "[", "]");
        for (int i = 0; i < bufferMap.size(); i++) {
            if (bufferMap.has(i)) {
                joiner = joiner.add(Integer.toString(i));
            }
        }
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