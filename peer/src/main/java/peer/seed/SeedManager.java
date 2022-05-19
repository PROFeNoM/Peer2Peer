package peer.seed;

import peer.util.Configuration;
import peer.util.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;


public class SeedManager {
    private static SeedManager instance;
    private static String seedFolder;
    private static ArrayList<Seed> seeds = new ArrayList<Seed>();
    private static ArrayList<Seed> leechs = new ArrayList<Seed>();
    private static int pieceSize = 1000000; // Default piece size (1 MB)


    public static SeedManager getInstance() {
        if (instance == null) {
            String seedFolder = Configuration.getInstance().getStoragePath();
            instance = new SeedManager(seedFolder);
        }
        return instance;
    }

    private SeedManager(String folderPath) {
        findSeeds(folderPath);
    }

    // Find seeded files from the given folder
    public void findSeeds(String folderPath) {
        File folder = new File(folderPath);

        if (!folder.exists()) {
            Logger.error(SeedManager.class.getSimpleName(), "Seeds folder does not exist: " + folderPath);
            System.exit(1);
        }

        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            Logger.log(getClass().getSimpleName(), "Seeds folder is not a valid directory: " + folderPath);
            System.exit(1);
        }

        for (File file : listOfFiles) {
            if (!file.isFile()) {
                continue;
            }

            // Seed already exists in database
            String key = FileUtils.getMD5Hash(file);
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
        seeds.add(new Seed(key, seedFolder + "/" + fileName, fileSize, pieceSize, null));
    }

    // Add a leech from info
    public void addLeech(String key, String fileName, int fileSize, int pieceSize, BufferMap bufferMap) {
        leechs.add(new Seed(key, seedFolder + "/" + fileName, fileSize, pieceSize, bufferMap));
    }

    // Remove a seed given its key
    public void removeSeed(String key) {
        seeds.removeIf(seed -> seed.getKey().equals(key));
    }

    // remove a seed given its file
    public void removeSeedFromName(String fileName) {
        seeds.removeIf(seed -> seed.getName().equals(fileName));
    }

    public ArrayList<Seed> getSeeds() {
        return seeds;
    }

    public String seedsToString() {
        return "[" + seeds.stream().map(Seed::toString).collect(Collectors.joining(" ")) + "]";
    }

    public String leechesToString() {
        return "[" + leechs.stream().map(Seed::toString).collect(Collectors.joining(" ")) + "]";
    }

    public static void saveLeechs() throws Exception {
        for (Seed leech : leechs) {
            try {
                PrintWriter writer = new PrintWriter("../../db/leechs.txt", "UTF-8");
                writer.println(leech.name + " : " + leech.size + " : " + leech.pieceSize + " : " + leech.key + " : " + leech.bufferMap);
                writer.close();
            } catch (IOException e) {
            Logger.error(e.getMessage());
            }
        }
    }

    public static void restoreLeechs() throws Exception {
        try {
            File file = new File("../../db/leechs/txt");
            Scanner sc = new Scanner(file);
            String line;

            while (sc.hasNextLine()) {
                line = sc.nextLine();
                String [] tokens = line.split(" : ");
                leechs.add(new Seed(tokens[3], tokens[0], Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), new BufferMap(Integer.parseInt(tokens[4]))));
            }
            sc.close();
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
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
