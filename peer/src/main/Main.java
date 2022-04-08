package peer.src.main;

import peer.src.main.util.Configuration;

public class Main {
    public static void main(String[] args) {
        String trackerIp;
        int trackerPort;
        int peerPort;

        if (args.length > 1) {
            try {
                trackerIp = args[0];
                trackerPort = Integer.parseInt(args[1]);
                peerPort = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.out.println("Usage: java TestPeer <tracker-ip> <tracker-port> <peer-port>");
                return;
            }
        } else {
            trackerIp = System.getProperty("trackerIp") == null ? Configuration.getInstance().getTrackerIp() : System.getProperty("trackerIp");
            trackerPort = System.getProperty("trackerPort") == null ? Configuration.getInstance().getTrackerPort() : Integer.parseInt(System.getProperty("trackerPort"));
            peerPort = System.getProperty("peerPort") == null ? Configuration.getInstance().getPeerPort() : Integer.parseInt(System.getProperty("peerPort"));
        }

        Peer peer = new Peer();
        peer.start(trackerIp, trackerPort, peerPort);
        peer.run();
        peer.stop();
    }
}
