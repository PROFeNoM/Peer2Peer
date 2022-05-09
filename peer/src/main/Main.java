package peer.src.main;

import peer.src.main.util.Configuration;

public class Main {
    public static void main(String[] args) {
        int peerPort, maxPeers;
        for (String arg : args) {
            System.out.println(arg);
            if (arg.equals("-h")) {
                System.out.println("Usage: java [-D peerPort=<peerPort>] [-D peerMax=<peerMax>] peer.src.main.Main [-h]");
                System.exit(0);
            }
        }

        peerPort = System.getProperty("peerPort") == null
                ? Configuration.getInstance().getPeerPort()
                : Integer.parseInt(System.getProperty("peerPort"));

        maxPeers = System.getProperty("peerMax") == null
                ? Configuration.getInstance().getMaxPeerToConnect()
                : Integer.parseInt(System.getProperty("peerMax"));

        Peer peer = new Peer();
        peer.start(peerPort, "0.0.1", maxPeers);
        peer.run();
        peer.stop();
    }
}
