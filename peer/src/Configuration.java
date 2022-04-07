import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    final static String CONFIG_FILE = "config.ini";

    private static String getProperty(String key) {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(CONFIG_FILE));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return prop.getProperty(key);
    }

    public static int getPort() {
        return Integer.parseInt(getProperty("peer-port"));
    }

    public static String getIp() {
        return getProperty("peer-ip");
    }
}
