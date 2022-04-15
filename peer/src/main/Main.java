package peer.src.main;

import peer.src.main.util.Configuration;

public class Main {
    public static void main(String[] args) {
        String trackerIp;
        int trackerPort;
        int peerPort;
        for (String arg : args) {
            System.out.println(arg);
            if (arg.equals("-h")) {
                System.out.println("Usage: java [-D trackerIp=<trackerIp>] [-D trackerPort=<trackerPort>] [-D peerPort=<peerPort>] peer.src.main.Main [-h]");
                System.exit(0);
            }
        }

        trackerIp = System.getProperty("trackerIp") == null
                ? Configuration.getInstance().getTrackerIp()
                : System.getProperty("trackerIp");
        trackerPort = System.getProperty("trackerPort") == null
                ? Configuration.getInstance().getTrackerPort()
                : Integer.parseInt(System.getProperty("trackerPort"));
        peerPort = System.getProperty("peerPort") == null
                ? Configuration.getInstance().getPeerPort()
                : Integer.parseInt(System.getProperty("peerPort"));

        System.out.println("port " + System.getProperty("trackerPort"));
        Peer peer = new Peer();
        peer.start(trackerIp, trackerPort, peerPort);
        peer.run();
        peer.stop();
    }
}
