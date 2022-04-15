package peer.src.test;

import java.util.ArrayList;
import peer.src.main.Seed;

public class TestSeed {
    public void testSeed() {
        Seed seed = new Seed("seeds/test.txt");
        assert seed.getName().equals("test.txt") : "Seed name is not correct";
        assert seed.getKey().equals("9b82756c759dcc81911ab9643b334da9") : "Seed key is not correct";

        ArrayList<Integer> bufferMap = seed.computeBufferMap();
        System.out.println(bufferMap);
    }
}
