package peer.src.main;

public class PeerInfo {
    String ip;
    int port;

    public PeerInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String toString() {
        return ip + ":" + port;
    }
}