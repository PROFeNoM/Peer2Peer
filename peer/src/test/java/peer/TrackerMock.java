package peer.src.test;

import java.net.*;
import java.io.*;

public class TrackerMock extends Thread {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int port;

    public TrackerMock(int port) {
        this.port = port;
    }

    void startConnection(int port) {
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    boolean isPeerConnected() {
        return clientSocket.isConnected();
    }

    public void run() {
        startConnection(port);
    }

    void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
}