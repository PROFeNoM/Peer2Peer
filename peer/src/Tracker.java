import java.net.*;
import java.io.*;

public class Tracker {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public Tracker(int port) {
        startConnection(port);
    }

    public void startConnection(int port) {
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void run() {
        String input;
        try {
            while ((input = in.readLine()) != null) {
                if (!input.isEmpty())
                    System.out.println("> " + input);

                if (input.equals(".")) {
                    out.println("good bye");
                    break;
                }
                out.println("200");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void stop() {
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