package peer.connection;

import peer.seed.BufferMap;
import peer.util.Logger;
import peer.Parser;

import java.net.*;
import java.util.Map;
import java.util.StringJoiner;
import java.io.*;

// Class to talk to another peer
public class PeerConnection {
    public Socket socket;
    BufferedReader in;
    PrintWriter out;

    public PeerConnection(Socket socket) {
        this.socket = socket;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), e.getMessage());
        }
    }

    public void stop() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), e.getMessage());
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getMessage() {
        String message = "";
        try {
            message = in.readLine();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), e.getMessage());
        }
        return message;
    }

    // Ask the remote peer for the pieces of the file of key `key`
    // in the given buffermap `bufferMap`
    public Map<Integer, byte[]> getPieces(String key, BufferMap bufferMap) {
        StringJoiner joiner = new StringJoiner(" ", "[", "]");
        for (int i = 0; i < bufferMap.size(); i++) {
            if (bufferMap.has(i)) {
                joiner.add(Integer.toString(i));
            }
        }
        String message = "getpieces " + key + " " + joiner;
        sendMessage(message);
        String response = getMessage();
        return Parser.parsePieces(response, key, bufferMap);
    }
}