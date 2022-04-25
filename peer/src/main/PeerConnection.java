package peer.src.main;

import java.net.*;
import java.util.Map;
import java.util.StringJoiner;
import java.io.*;

// Class to talk to another peer
public class PeerConnection {
    Socket socket;
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

    public PeerConnection(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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

    // Ask peer for his buffermap of the file with key `key`
    // Return the buffermap or null if the peer doesn't have the file
    public BufferMap interested(String key) {
        String message = "interested " + key;
        sendMessage(message);
        String response = getMessage();
        BufferMap bufferMap = Parser.parseInterested(response, key);
        return bufferMap;
    }

    // Ask the remote peer for the pieces of the file of key `key`
    // in the given buffermap `bufferMap`
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
        Map<Integer, byte[]> pieces = Parser.parsePieces(response, key, bufferMap);
        return pieces;
    }
}