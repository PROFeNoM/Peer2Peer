package peer.src.main;

import peer.src.main.util.FileHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class SeedManager {
    ArrayList<Seed> seeds = new ArrayList<Seed>();
    private static SeedManager instance;
    private static int pieceSize = 64; // Default piece size

    public static SeedManager getInstance() {
        if (instance == null) {
            instance = new SeedManager("seeds");
        }
        return instance;
    }
    
    private SeedManager(String folderPath) {
        findSeeds(folderPath);
    }

    // Find seeded files from the given folder
    private void findSeeds(String folderPath) {
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            Logger.log(getClass().getSimpleName(), "No seeds found");
            return;
        }

        for (File file : listOfFiles) {
            if (!file.isFile()) {
                continue;
            }

            // Seed already exists in database
            String key = FileHandler.getHash(file);
            if (getSeedFromKey(key) != null) {
                continue;
            }

            addSeed(file, pieceSize);
        }

        Logger.log(getClass().getSimpleName(), "Seeds found: " + seeds.size());
    }

    // Add a seed from file
    private void addSeed(File file, int pieceSize) {
        seeds.add(new Seed(file, pieceSize));
    }

    // Add a seed from info
    public void addSeed(String key, String fileName, int fileSize, int pieceSize) {
        seeds.add(new Seed(key, fileName, fileSize, pieceSize));
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
    public void writePieces(String key, Map<Integer, byte[]> pieces) {
        // We need to know the seed to be able to write to it
        Seed seed = getSeedFromKey(key);
        if (seed == null) {
            Logger.error(getClass().getSimpleName(), "No seed registered for key " + key);
            return;
        } else {
            Logger.log(getClass().getSimpleName(), "Writing pieces to file");
        }

        // TODO: check the buffermap to write only the missing pieces
        if (seed.getFile() != null) {
            // Logger.log(getClass().getSimpleName(), "File already exists");
            // return; // To be able to test locally
        }

        File file = new File(seed.getName());
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
