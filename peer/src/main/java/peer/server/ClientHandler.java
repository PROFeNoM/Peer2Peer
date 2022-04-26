package peer.server;

import peer.Command;
import peer.Parser;
import peer.connection.PeerConnection;
import peer.seed.Seed;
import peer.seed.SeedManager;
import peer.util.Logger;

import java.io.IOException;
import java.net.*;
import java.util.StringJoiner;

/**
 * Class for handling communication coming from an other peer.
 */
public class ClientHandler extends Thread {
    private final PeerConnection peer;

    public ClientHandler(Socket socket) throws IOException {
        peer = new PeerConnection(socket);
    }

    @Override
    public void run() {
        String input;

        while ((input = peer.getMessage()) != null) {
            if (input.isEmpty()) {
                continue;
            }

            Command command = Parser.getMessageType(input);
            String key;
            switch (command) {
                case INTERESTED:
                    key = Parser.parseKey(input);
                    interested(key);
                    break;
                case GETPIECES:
                    key = Parser.parseKey(input);
                    int[] indices = Parser.parseIndices(input);
                    sendPieces(key, indices);
                case EXIT:
                    try {
                        peer.stop();
                    } catch (IOException e) {
                        Logger.error(getClass().getSimpleName(),
                                "Failed to close connection to peer: " + e.getMessage());
                    }
                    return;
                default:
                    Logger.error(getClass().getSimpleName(), "Received unknown command from peer: " + command);
                    break;
            }
        }

        try {
            peer.stop();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Failed to close connection to peer: " + e.getMessage());
        }
    }

    void interested(String key) {
        Seed seed = SeedManager.getInstance().getSeedFromKey(key);
        if (seed == null) {
            peer.sendMessage("have " + key + " 0");
        } else {
            peer.sendMessage("have " + key + " " + seed.getBufferMap().toString());
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
            String hex = "";
            for (int i = 0; i < byteRead; i++) {
                hex += String.format("%02X", bytes[i]);
            }
            pieces.add(index + ":" + hex);
        }
        String message = "data " + key + " " + pieces.toString();
        peer.sendMessage(message);
    }
}
