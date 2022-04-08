import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class Parser {
    String method;
    List<String> args;
    private boolean toTracker = false;

    void parse(String command) {
        String delimiter = " ";
        String[] toparse = command.split(delimiter);

        this.args = new ArrayList<>(Arrays.asList(toparse));
        
        this.method = this.args.get(0);
        this.args.remove(0);
    }

    void redirectSending() {
        List<String> trackerMethods = new ArrayList<String>();
        trackerMethods.add("announce"); 
        trackerMethods.add("look");
        trackerMethods.add("getfiles");

        if (trackerMethods.contains(this.method)) {
            this.toTracker = true;
        }
    }
}