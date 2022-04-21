package peer.src.main;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class SeedManager {
    ArrayList<Seed> seeds;

    public SeedManager(String folderPath) {
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
}
