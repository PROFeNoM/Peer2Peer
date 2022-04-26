package peer.src.main;

public class ConnectionInfo {
    String ip;
    int port;

    public ConnectionInfo(String ip, int port) {
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
