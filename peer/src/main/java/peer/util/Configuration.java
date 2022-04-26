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
        Logger.log(getClass().getSimpleName(), "Configuration file " + CONFIG_FILE + " loaded");
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
        return prop.getProperty("tracker-ip");
    }

    /**
     * Get the tracker port.
     * @return The value of "tracker-port" in the configuration file.
     */
    public int getTrackerPort() {
        return Integer.parseInt(prop.getProperty("tracker-port"));
    }

    /**
     * Get the peer port.
     * @return The value of "peer-port" in the configuration file.
     */
    public int getPeerPort() {
        return Integer.parseInt(prop.getProperty("peer-port"));
    }

    /**
     * Get the maximum number of peers allowed to connect
     * to the peer server at the same time.
     * @return The value of "peer-max" in the configuration file.
     */
    public int getMaxPeerToConnect() {
        return Integer.parseInt(prop.getProperty("peer-max"));
    }

    /**
     * Get the maximum number of pieces to ask at the same time.
     * @return The value of "pieces-max" in the configuration file.
     */
    public int getMaxPieces() {
        return Integer.parseInt(prop.getProperty("pieces-max"));
    }

    /**
     * Get the path to the folder containing the file to seed.
     * @return The value of "seed-folder" in the configuration file.
     */
    public String getSeedsFolder() {
        return prop.getProperty("seeds-folder");
    }
}
