package peer.src.main;

import java.net.*;
import java.io.FileInputStream;
import java.util.StringJoiner;

// Class for handling communication from an other peer
public class ClientHandler extends Thread {
    private PeerConnection peer;

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
        for (Seed seed : SeedManager.getInstance().getSeeds()) {
            if (seed.getKey().equals(key)) {
                peer.sendMessage("have " + seed.getKey() + " " + seed.getBuffermap().toString());
                return;
            }
        }
        peer.sendMessage("have not");
    }

    void sendPieces(String key, int[] indices) {
        StringJoiner pieces = new StringJoiner(" ", "[", "]");
        Seed seed = SeedManager.getInstance().getSeedFromKey(key);
        for (int index : indices) {
            try {
                FileInputStream fis = new FileInputStream(seed.getFile());
                byte[] bytes = new byte[seed.getPieceSize()];
                int byteRead = fis.read(bytes, seed.getPieceSize() * index, seed.getPieceSize());
                fis.close();
                // Convert byte to hexadecimal for sending
                String hex = "";
                for (int i = 0; i < byteRead; i++) {
                    hex += String.format("%02X", bytes[i]);
                }
                pieces.add(index + ":" + hex);
            } catch (Exception e) {
                Logger.error(getClass().getSimpleName(), "Error while read piece " + index + ": " + e.getMessage());
            }
        }
        String message = "data " + key + " " + pieces.toString();
        peer.sendMessage(message);
    }
}
