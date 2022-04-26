package peer.src.main;

import peer.src.main.util.Configuration;

public class Main {
    public static void main(String[] args) {
        int peerPort;
        for (String arg : args) {
            System.out.println(arg);
            if (arg.equals("-h")) {
                System.out.println("Usage: java [-D peerPort=<peerPort>] peer.src.main.Main [-h]");
                System.exit(0);
            }
        }

        peerPort = System.getProperty("peerPort") == null
                ? Configuration.getInstance().getPeerPort()
                : Integer.parseInt(System.getProperty("peerPort"));

        Peer peer = new Peer();
        peer.start(peerPort, "0.0.1");
        peer.run();
        peer.stop();
    }
}
