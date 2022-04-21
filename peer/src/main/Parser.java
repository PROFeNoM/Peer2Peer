package peer.src.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Parser {

    static List<Object> parse(String command) {
        String delimiter = " ";
        String method;
        List<String> args;

        String[] toparse = command.split(delimiter);
        args = new ArrayList<>(Arrays.asList(toparse));

        method = args.get(0);
        args.remove(0);
        
        return Arrays.asList(method, args);
    }
}