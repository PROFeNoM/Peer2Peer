import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

class Parser {
    boolean parse(String command) {
        return true;
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        String delimiter = " ";

        String input;
        System.out.print("< ");
        while (!(input = in.nextLine()).equals("exit")) {
            String[] command = input.split(delimiter);
            System.out.print("> ");
            System.out.println(Arrays.toString(command));
            System.out.print("< ");
        }

        in.close();
    }
}