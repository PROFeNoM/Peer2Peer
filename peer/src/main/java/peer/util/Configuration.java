package peer.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Class to read the application configuration file.
 */
public class Configuration {
    /**
     * Unique instance of the class.
     */
    private static Configuration instance;

    /**
     * Path to the configuration file.
     */
    private final String CONFIG_FILE = "config.ini";

    /**
     * Properties of the configuration file.
     */
    private final Properties prop = new Properties();

    /**
     * Private constructor.
     * Load the configuration file.
     */
    private Configuration() {
        try {
            prop.load(new FileInputStream(CONFIG_FILE));

        } catch (FileNotFoundException e) {
            Logger.error(getClass().getSimpleName(), "Cannot find config file: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Cannot read config file: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("Configuration file " + CONFIG_FILE + " loaded");
        System.out.println("Tracker IP: " + getTrackerIp());
        System.out.println("Tracker port: " + getTrackerPort());
        System.out.println("Peer port: " + getPeerPort());
        System.out.println("Storage path: " + getStoragePath());
        System.out.println("Max pieces: " + getMaxPieces());
        System.out.println("Max peers to connect: " + getMaxPeerToConnect());
        }

    /** 
     * Get the unique instance of the class.
     */
    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    /**
     * Get the tracker IP address.
     * @return The value of "tracker-ip" in the configuration file.
     */
    public String getTrackerIp() {
        return System.getProperty("tracker-ip", prop.getProperty("tracker-ip"));
    }

    /**
     * Get the tracker port.
     * @return The value of "tracker-port" in the configuration file.
     */
    public int getTrackerPort() {
        return Integer.parseInt(System.getProperty("tracker-port", prop.getProperty("tracker-port")));
    }

    /**
     * Get the peer port.
     * @return The value of "peer-port" in the configuration file.
     */
    public int getPeerPort() {
        return Integer.parseInt(System.getProperty("peer-port", prop.getProperty("peer-port")));
    }

    /**
     * Get the maximum number of peers allowed to connect
     * to the peer server at the same time.
     * @return The value of "peer-max" in the configuration file.
     */
    public int getMaxPeerToConnect() {
        return Integer.parseInt(System.getProperty("max-peers", prop.getProperty("max-peers")));
    }

    /**
     * Get the maximum number of pieces to ask at the same time.
     * @return The value of "pieces-max" in the configuration file.
     */
    public int getMaxPieces() {
        return Integer.parseInt(System.getProperty("max-pieces", prop.getProperty("max-pieces")));
    }

    /**
     * Get the path to the folder containing the files to seed.
     * @return The value of "seed-folder" in the configuration file.
     */
    public String getStoragePath() {
        return System.getProperty("storage-path", prop.getProperty("storage-path"));
    }

    /**
     * Get the path to the folder containing the file to seed.
     * @return The value of "seed-folder" in the configuration file.
     */
    public int getUpdateInterval() {
        return Integer.parseInt(System.getProperty("update-interval", prop.getProperty("update-interval")));
    }

    /**
     * Tell if debug mode is enabled.
     * @return True if debug mode is enabled, false otherwise.
     */
    public boolean isDebug() {
        return Boolean.parseBoolean(System.getProperty("verbose", prop.getProperty("verbose")));
    }
}

