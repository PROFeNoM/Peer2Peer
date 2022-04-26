package peer.seed;

import peer.util.Configuration;
import peer.util.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class SeedManager {
    ArrayList<Seed> seeds = new ArrayList<Seed>();
    private static SeedManager instance;
    private static int pieceSize = 3; // Default piece size

    public static SeedManager getInstance() {
        if (instance == null) {
            String seedFolder = System.getProperty("seedsFolder") != null ? System.getProperty("seedsFolder")
                    : Configuration.getInstance().getSeedsFolder();
            Logger.log(SeedManager.class.getSimpleName(), "Seeds folder: " + seedFolder);
            instance = new SeedManager(seedFolder);
        }
        return instance;
    }

    private SeedManager(String folderPath) {
        findSeeds(folderPath);
    }

    // Find seeded files from the given folder
    private void findSeeds(String folderPath) {
        File folder = new File(folderPath);

        if (!folder.exists()) {
            Logger.error(SeedManager.class.getSimpleName(), "Seeds folder does not exist: " + folderPath);
            System.exit(1);
        }

        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            Logger.log(getClass().getSimpleName(), "Seeds folder is not a valid directory: " + folderPath);
            return;
        }

        for (File file : listOfFiles) {
            if (!file.isFile()) {
                continue;
            }

            // Seed already exists in database
            String key = FileHandler.getHash(file);
            if (hasSeed(key)) {
                continue;
            }
            addSeed(file, pieceSize);
        }

        Logger.log(getClass().getSimpleName(), "Seeds found: " + seeds.size());
    }

    // Add a seed from file
    public void addSeed(File file, int pieceSize) {
        seeds.add(new Seed(file, pieceSize));
    }

    // Add a seed from info
    public void addSeed(String key, String fileName, int fileSize, int pieceSize) {
        seeds.add(new Seed(key, fileName, fileSize, pieceSize));
    }

    // Remove a seed given its key
    public void removeSeed(String key) {
        seeds.removeIf(seed -> seed.getKey().equals(key));
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

    public boolean hasSeed(String key) {
        // TODO: optimize
        return getSeedFromKey(key) != null;
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
        Seed seed = getSeedFromKey(key);
        // Ensure we know the seed
        if (seed == null) {
            Logger.error(getClass().getSimpleName(), "No seed registered for key " + key);
            return;
        } else {
            Logger.log(getClass().getSimpleName(), "Writing pieces to file " + seed.getName());
        }

        for (Map.Entry<Integer, byte[]> piece : pieces.entrySet()) {
            seed.writePiece(piece.getKey(), piece.getValue());
        }
    }
}
