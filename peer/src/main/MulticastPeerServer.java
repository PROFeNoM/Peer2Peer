package peer.src.main;

import peer.src.main.ClientHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

// Server to listen for incoming connections
// Multicast UDP
// When a client connects, establish a connection with the client with ClientHandler
public class MulticastPeerServer extends Thread {
    public static final int MULTICAST_PORT = 6789;
    public static final String MULTICAST_ADDRESS = "228.5.6.7";
    public static final int MAX_PACKET_SIZE = 1024;

    private final MulticastSocket multicastSocket;
    private final Peer _peer;

    public MulticastPeerServer(Peer peer) throws IOException {
        _peer = peer;
        multicastSocket = new MulticastSocket(MULTICAST_PORT);
        multicastSocket.joinGroup(InetAddress.getByName(MULTICAST_ADDRESS));

        // Allow ReuseAddress
        multicastSocket.setReuseAddress(true);
    }

    @Override
    public void run() {
        try {
            receiveUDPMessage();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Cannot receive UDP message: " + e.getMessage());
        }

        // Listen for incoming connections
        // Accept client's socket
        // Create a corresponding client handler
        /*
        try {
            while (true) {
                byte[] buffer = new byte[MAX_PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(packet);

                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();

                Logger.log(getClass().getSimpleName(), "Received connection request from " + clientAddress + ":" + clientPort);
                Logger.log(getClass().getSimpleName(), "Message: " + new String(packet.getData()));

                Socket clientSocket = new Socket(clientAddress, clientPort);
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), e.getMessage());
        }*/
    }

    public void close() {
        try {
            multicastSocket.leaveGroup(InetAddress.getByName(MULTICAST_ADDRESS));
            multicastSocket.close();

            Logger.log(getClass().getSimpleName(), "Closed multicast socket");
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Cannot close server: " + e.getMessage());
        }
    }
    public void receiveUDPMessage() throws IOException {
        byte[] buffer = new byte[MAX_PACKET_SIZE];

        while (true) {
            //Logger.log(getClass().getSimpleName(), "Waiting for message...");

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            multicastSocket.receive(packet);
            String message = new String(packet.getData(), packet.getOffset(),packet.getLength());

            Logger.log(getClass().getSimpleName(), "Received message: " + message);

            Parser.parseUDPMessage(message, this);

        }
    }

    public void sendUDPMessage(String message) throws IOException {
        //Logger.log(getClass().getSimpleName(), "Sending message: " + message);
        byte[] buffer = message.getBytes();
        InetAddress address = InetAddress.getByName(MULTICAST_ADDRESS);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, MULTICAST_PORT);
        multicastSocket.send(packet);
        //Logger.log(getClass().getSimpleName(), "Sent message: " + message);
    }

    Peer getPeer() {
        return _peer;
    }
}
