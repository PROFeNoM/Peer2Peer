package peer;

/**
 * This class contains the information about a connection.
 */
public class ConnectionInfo {
    private final String ip;
    private final int port;

    /**
     * Constructor.
     * @param ip IP address of the connection.
     * @param port Port of the connection.
     */
    public ConnectionInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * Get the IP address of the connection.
     * @return IP address of the connection.
     */
    public String getIp() {
        return ip;
    }

    /**
     * Get the port of the connection.
     * @return Port of the connection.
     */
    public int getPort() {
        return port;
    }

    /**
     * Return a string representation of the connection.
     * @return String with IP and port of the connection.
     */
    public String toString() {
        return ip + ":" + port;
    }
}
