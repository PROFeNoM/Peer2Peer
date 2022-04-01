import java.net.*;
import java.io.*;

public class Tracker {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            // while (true) {
                // inputLine = in.readLine();
            while ((inputLine = in.readLine()) != null) {
                if (inputLine != null)
                    System.out.println(inputLine);
                    out.println(inputLine);
                if (".".equals(inputLine)) {
                    out.println("good bye");
                    break;
                }
                out.println(inputLine);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        } finally {
            try {
                serverSocket.close();
                clientSocket.close();
                out.close();
                in.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }            
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