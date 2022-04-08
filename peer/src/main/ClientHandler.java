package peer.src.main;

import java.io.*;
import java.net.*;

// Class for handling communication from an other peer
public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Error while creating client: " + e);
        }
    }

    @Override
    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (!inputLine.isEmpty())
                    System.out.println("[client]: " + inputLine);

                if (inputLine.equals(".")) {
                    out.println("good bye");
                    break;
                }

                out.println("200");
            }
        } catch (IOException e) {
            System.out.println("Error while reading from client: " + e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error while closing connection to client: " + e);
            }
        }
    }
}
