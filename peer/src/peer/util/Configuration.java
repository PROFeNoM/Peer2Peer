package peer.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    private static Configuration instance;
    private final String CONFIG_FILE = "config.ini";
    private final Properties prop = new Properties();

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

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public String getTrackerIp() {
        return prop.getProperty("tracker-ip");
    }

    public int getTrackerPort() {
        return Integer.parseInt(prop.getProperty("tracker-port"));
    }

    public int getPeerPort() {
        return Integer.parseInt(prop.getProperty("peer-port"));
    }

    public int getMaxPeerToConnect() {
        return Integer.parseInt(prop.getProperty("peer-max"));
    }

    public int getMaxPieces() {
        return Integer.parseInt(prop.getProperty("pieces-max"));
    }

    public String getSeedsFolder() {
        return prop.getProperty("seeds-folder");
    }

    public int getMaxTTL() { return Integer.parseInt(prop.getProperty("ttl-max")); }
}
