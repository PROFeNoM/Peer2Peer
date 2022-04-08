package peer.src.main;

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
            System.out.println("Cannot find config file: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Cannot read config file: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Configuration file " + CONFIG_FILE + " loaded");
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
}
