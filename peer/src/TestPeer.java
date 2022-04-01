public class TestPeer {
    public static void main(String[] args) {

        // if (args.length < 2) {
        //     System.out.println("usage: java TestPeer <ip> <port>");
        //     System.exit(1);
        // }
        


        String ip = args[0];
        int port;
        Peer peer = new Peer();
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
            peer.startServer(port);
        }else {
            port = Integer.parseInt(args[1]);
            peer.startConnection(ip, port);            
        }
        peer.sendMessage("Hello");
        peer.stopConnection();
    }
}