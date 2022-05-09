package peer.src.main;

import java.net.*;
import java.util.Map;
import java.util.StringJoiner;

// Class for handling communication from another peer
public class ClientHandler extends Thread {
    final private PeerConnection peerConnection;
    final private Peer peer;

    public ClientHandler(Socket socket, Peer peer) {
        peerConnection = new PeerConnection(socket);
        this.peer = peer;
    }

    @Override
    public void run() {
        if (peer.getNeighborsHandler().size() > peer.getMaxPeers()) {
            peerConnection.stop();
            Logger.log(getClass().getSimpleName(), "Max peers reached");
            return;
        }

        String input;
        while ((input = peerConnection.getMessage()) != null) {
            if (!input.isEmpty())
                Logger.log(getClass().getSimpleName(), "Received " + input);
            Parser.parseRequest(input, this);

            if (input.equals(".")) {
                peerConnection.stop();
                break;
            }
        }
    }

    void exit() {
        peerConnection.sendMessage("exit" + " " + peer.getPort());
        peerConnection.stop();
        this.stop();
        Logger.log(getClass().getSimpleName(), "Disconnecting from " + peerConnection.socket.getPort());
    }

    void acceptExit(int port) {
        peer.getNeighborsHandler().remove(port);
        System.out.println("Removed " + port + " from neighbors handler");
        peer.getNeighborsPort().removeIf(p -> (p == port));
        System.out.println("Removed " + port + " from neighbors port");
        Logger.log(getClass().getSimpleName(), "Peer " + port + " disconnected");
        peerConnection.stop();
        this.stop();
    }

    void interested(String key) {
        Seed seed = SeedManager.getInstance().getSeedFromKey(key);
        if (seed == null) {
            peerConnection.sendMessage("have " + key + " 0");
        } else {
            peerConnection.sendMessage("have " + key + " " + seed.getBuffermap().toString());
        }
    }

    void sendPieces(String key, int[] indices) {
        StringJoiner pieces = new StringJoiner(" ", "[", "]");
        Seed seed = SeedManager.getInstance().getSeedFromKey(key);

        if (seed == null) {
            Logger.error(getClass().getSimpleName(), "Seed asked not found");
            // TODO : send better error message
            peerConnection.sendMessage("not found");
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
        peerConnection.sendMessage(message);
    }

    void announce(String port) {
        String message = "announce listen " + port;
        peerConnection.sendMessage(message);
    }

    void acceptAnnounce(int port) {
        peer.addNeighbour(port, this);
        Logger.log(getClass().getSimpleName(), "Added " + port + " to neighbors handler");
        peerConnection.sendMessage("ok");
    }

    void look(String criterion, String ttl, String ip, String port) {
        String message = "look " + criterion + " " + ttl + " " + ip + " " + port;
        peerConnection.sendMessage(message);
    }

    void acceptLook(String criterion, String ttl, String ip, String port) {
        StringBuilder message;
        Seed seed = SeedManager.getInstance().getSeedFromName(criterion);
        if (Integer.parseInt(ttl) < 1) {
            message = new StringBuilder("file at " + ip + ":" + port);
        } else if (seed == null) {
            int sent = 0;
            for (Map.Entry<Integer, ClientHandler> entry : peer.getNeighborsHandler().entrySet()) {
                ClientHandler clientHandler = entry.getValue();
                if (entry.getKey() != Integer.parseInt(port)) {
                    sent++;
                    clientHandler.look(criterion, String.valueOf(Integer.parseInt(ttl) - 1), ip, port);
                }
            }
            if (sent == 0) {
                message = new StringBuilder("file at " + ip + ":" + port);
            } else {
                return;
            }
        } else {
            String seederIp = peerConnection.socket.getInetAddress().getHostAddress();
            String seederPort = Integer.toString(peerConnection.socket.getLocalPort());
            String seeder = seederIp + ":" + seederPort;
            if (!seed.seeders.contains(seeder)) {
                seed.seeders.add(seeder);
            }

            message = new StringBuilder("file at " + seeder + " have "
                    + seed.getName() + " " + seed.getSize() + " " + seed.getPieceSize() + " " + seed.getKey());

            message.append(" seeders [");
            for (String _s : seed.seeders) {
                message.append(_s).append(" ");
            }
            // remove last character
            message.deleteCharAt(message.length() - 1);
            message.append("]");

            if (!seed.leechers.isEmpty()) {
                message.append(" leechers [");
                for (String leecher : seed.leechers) {
                    message.append(leecher).append(" ");
                }
                message.deleteCharAt(message.length() - 1);
                message.append("]");
            }
        }
        peerConnection.sendMessage(message.toString());
    }
}
