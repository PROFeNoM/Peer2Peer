package peer.src.main;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class SeedManager {
    ArrayList<Seed> seeds;
    private static SeedManager instance;

    public static SeedManager getInstance() {
        if (instance == null) {
            instance = new SeedManager("seeds");
        }
        return instance;
    }
    
    private SeedManager(String folderPath) {
        seeds = findSeeds(folderPath);
    }

    // Find seeded files from the given folder
    private ArrayList<Seed> findSeeds(String folderPath) {
        seeds = new ArrayList<Seed>();
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            Logger.log(getClass().getSimpleName(), "No seeds found");
            return seeds;
        }

        for (File file : listOfFiles) {
            if (file.isFile()) {
                seeds.add(new Seed(file.getAbsolutePath()));
            }
        }

        Logger.log(getClass().getSimpleName(), "Seeds found: " + seeds.size());
        return seeds;
    }

    public ArrayList<Seed> getSeeds() {
        return seeds;
    }

    public String seedsToString() {
        return "[" + seeds.stream().map(Seed::toString).collect(Collectors.joining(" ")) + "]";
    }

    public String leechesToString() {
        return "[]";
    }

    public Seed getSeedFromKey(String key) {
        for (Seed seed : seeds) {
            if (seed.getKey().equals(key)) {
                return seed;
            }
        }
        return null;
    }

    // Write pieces to file
    public static void writePieces(String key, Map<Integer, byte[]> pieces) {
        // TODO: We need to know the seed to be able to write to it
        // Seed seed = getSeedFromKey(key);
        // if (seed == null) {
        //     Logger.error(getClass().getSimpleName(), "No seed found for key " + key);
        //     return;
        // }

        File file = new File(key + ".txt");
        try {
            file.createNewFile();
        } catch (Exception e) {
            Logger.error(SeedManager.class.getSimpleName(), "Error while creating file " + key + ": " + e.getMessage());
            return;
        }

        for (Map.Entry<Integer, byte[]> entry : pieces.entrySet()) {
            try {
                FileOutputStream fos = new FileOutputStream(file, true);
                fos.write(entry.getValue());
                fos.close();
            } catch (Exception e) {
                Logger.error(SeedManager.class.getSimpleName(), "Error while writing piece " + entry.getKey() + ": " + e.getMessage());
            }
        }
        Logger.log(SeedManager.class.getSimpleName(), "File " + key + " written");
    }
}
