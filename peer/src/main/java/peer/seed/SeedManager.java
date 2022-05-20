package peer.seed;

import peer.util.Configuration;
import peer.util.Logger;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SeedManager {
    private static SeedManager instance;
    private final static String storagePath = Configuration.getInstance().getStoragePath();
    private static ArrayList<Seed> seeds = new ArrayList<Seed>();
    private static ArrayList<Seed> leechs = new ArrayList<Seed>();
    private static int pieceSize = 1000000; // Default piece size (1 MB)

    public static SeedManager getInstance() {
        if (instance == null) {
            instance = new SeedManager();
        }
        return instance;
    }

    private SeedManager() {
        try {
            restoreLeechs();
        } catch (Exception e) {
            Logger.log("Could not restore leechs " + e.getMessage());
        }

        File folder = new File(storagePath);

        if (!folder.exists()) {
            if (!folder.mkdir()) {
                Logger.error(SeedManager.class.getSimpleName(), "Could not create storage folder: " + storagePath);
                System.exit(1);
            }
        }

        findSeeds(storagePath);
    }

    // Find seeded files from the given folder
    public void findSeeds(String folderPath) {
        File folder = new File(folderPath);

        if (!folder.exists()) {
            Logger.error(SeedManager.class.getSimpleName(), "Storage folder does not exist: " + folderPath);
            System.exit(1);
        }

        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            Logger.log(getClass().getSimpleName(), "Storage folder is not a valid directory: " + folderPath);
            System.exit(1);
        }

        for (File file : listOfFiles) {
            if (!file.isFile()) {
                continue;
            }

            // Seed already exists in database as a leech
            if (isLeech(file.getName())) {
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
        seeds.add(new Seed(key, storagePath, fileName, fileSize, pieceSize, null));
    }

    // Add a leech from info
    public void addLeech(String key, String fileName, int fileSize, int pieceSize) {
        leechs.add(new Seed(key, storagePath, fileName, fileSize, pieceSize, null));
    }

    // Remove a seed given its key
    public void removeSeed(String key) {
        seeds.removeIf(seed -> seed.getKey().equals(key));
    }

    // remove a seed given its file
    public void removeSeedFromName(String fileName) {
        seeds.removeIf(seed -> seed.getName().equals(fileName));
    }

    // Remove a seed given its key
    public void removeLeech(String key) {
        leechs.removeIf(leech -> leech.getKey().equals(key));
    }

    public ArrayList<Seed> getSeeds() {
        return seeds;
    }

    public String seedsToString() {
        return "[" + seeds.stream().map(Seed::toString).collect(Collectors.joining(" ")) + "]";
    }

    public String leechesToString() {
        return "[" + leechs.stream().map(seed -> seed.key).collect(Collectors.joining(" ")) + "]";
    }

    public void saveLeechs() throws Exception {
        File file = new File("db/leechs.txt");

        if (file.exists()) {
            file.delete();
        }

        if (!file.createNewFile()) {
            throw new Exception("Could not create leechs file");
        }

        PrintWriter writer = new PrintWriter(file, "UTF-8");

        for (Seed leech : leechs) {
            if (leech.getBufferMap().isEmpty()) {
                continue;
            }
            
            writer.println(leech.name + " : " + leech.size + " : " + leech.pieceSize + " : " + leech.key + " : "
                    + leech.bufferMap);
        }

        writer.close();
    }

    public void restoreLeechs() throws Exception {
        try {
            File file = new File("db/leechs.txt");

            if (!file.exists()) {
                return;
            }

            Scanner sc = new Scanner(file);
            String line;

            while (sc.hasNextLine()) {
                line = sc.nextLine();
                String[] tokens = line.split(" : ");
                String name = tokens[0];
                long size = Long.parseLong(tokens[1]);
                int pieceSize = Integer.parseInt(tokens[2]);
                String key = tokens[3];
                int bufferMapValue = Integer.parseInt(tokens[4]);
                BufferMap bufferMap = new BufferMap(size, pieceSize, bufferMapValue);
                leechs.add(new Seed(key, storagePath, name, size, pieceSize, bufferMap));
            }
            sc.close();
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }

    public boolean isLeech(String name) {
        return getLeechFromName(name) != null;
    }

    public Seed getLeechFromName(String name) {
        for (Seed leech : leechs) {
            if (leech.getName().equals(name)) {
                return leech;
            }
        }
        return null;
    }

    public Seed getLeechFromKey(String key) {
        for (Seed leech : leechs) {
            if (leech.getKey().equals(key)) {
                return leech;
            }
        }
        return null;
    }

    public ArrayList<Seed> getLeeches() {
        return leechs;
    }

    public boolean hasSeed(String key) {
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
        Seed leech = getLeechFromKey(key);
        // Ensure we know the leech
        if (leech == null) {
            Logger.error(getClass().getSimpleName(), "No leech registered for key " + key);
            return;
        } else {
            Logger.log(getClass().getSimpleName(), "Writing pieces to file " + leech.getName());
        }

        for (Map.Entry<Integer, byte[]> piece : pieces.entrySet()) {
            leech.writePiece(piece.getKey(), piece.getValue());
        }
    }

    public void leechToSeed(Seed leech) {
        removeLeech(leech.key);
        seeds.add(leech);
    }
}
