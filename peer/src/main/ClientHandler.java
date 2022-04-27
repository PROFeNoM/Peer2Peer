package peer.src.main;

import java.net.*;
import java.util.StringJoiner;

// Class for handling communication from another peer
public class ClientHandler extends Thread {
    final private PeerConnection peer;

    public ClientHandler(Socket socket) {
        peer = new PeerConnection(socket);
    }

    @Override
    public void run() {
        String input;
        while ((input = peer.getMessage()) != null) {
            if (!input.isEmpty())
                Logger.log(getClass().getSimpleName(), "Received " + input);
            Parser.parseRequest(input, this);

            if (input.equals(".")) {
                peer.stop();
                break;
            }
        }
    }

    void interested(String key) {
        Seed seed = SeedManager.getInstance().getSeedFromKey(key);
        if (seed == null) {
            peer.sendMessage("have " + key + " 0");
        } else {
            peer.sendMessage("have " + key + " " + seed.getBuffermap().toString());
        }
    }

    void sendPieces(String key, int[] indices) {
        StringJoiner pieces = new StringJoiner(" ", "[", "]");
        Seed seed = SeedManager.getInstance().getSeedFromKey(key);

        if (seed == null) {
            Logger.error(getClass().getSimpleName(), "Seed asked not found");
            // TODO : send better error message
            peer.sendMessage("not found");
            return;
        }

        for (int index : indices) {
            byte[] bytes = new byte[seed.getPieceSize()];
            int byteRead = seed.readPiece(index, bytes);
            // Convert byte to hexadecimal for sending
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < byteRead; i++) {
                hex.append(String.format("%02X", bytes[i]));
            }
            pieces.add(index + ":" + hex);
        }
        String message = "data " + key + " " + pieces;
        peer.sendMessage(message);
    }

    void announce(String port) {
        String message = "announce listen " + port;
        peer.sendMessage(message);
    }

    void acceptAnnounce(int port) {
        peer.sendMessage("ok");
    }
}
