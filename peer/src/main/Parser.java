package peer.src.main;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class Parser {
    String method;
    List<String> args;

    void parse(String command) {
        String delimiter = " ";
        String[] toparse = command.split(delimiter);

        this.args = new ArrayList<>(Arrays.asList(toparse));
        
        this.method = this.args.get(0);
        this.args.remove(0);
    }
}