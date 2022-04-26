package peer;

import peer.seed.BufferMap;
import peer.util.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The parser class is responsible for parsing the user input,
 * the message from the tracker and the message from the peers.
 */
public class Parser {
    /**
     * Parse a list of arguments from a string.
     * The string must be surrounded by square brackets.
     * Example : "[arg1 arg2 arg3]" -> ["arg1", "arg2", "arg3"]
     * Warning: it flattens the array.
     * 
     * @param string    The string to parse.
     * @param delimiter The delimiter to use to separate the elements.
     * @return The array of arguments.
     */
    static String[] stringToArray(String string, String delimiter) {
        if (string == null || string.isEmpty() || !string.startsWith("[") || !string.endsWith("]")) {
            System.out.println("Cannot convert string \"" + string + "\" to array");
            return null;
        }

        String[] array = string.replace("[", "").replace("]", "").split(delimiter);

        return array[0].isEmpty() ? new String[0] : array;
    }

    // Validate an input with its corresponding regex.
    private static boolean isValidMessage(String input, Command command) {
        return input.matches(Command.getRegex(command));
    }

    /**
     * Parse message type and validate it.
     * 
     * @param input
     * @return Command
     */
    public static Command getMessageType(String input) {
        if (input == null || input.isEmpty()) {
            Logger.warn("Parser", "Empty input");
            return null;
        }

        String[] tokens = input.split(" ", 2);
        Command command = Command.fromString(tokens[0]);

        return command == Command.UNKNOWN ? Command.UNKNOWN
                : isValidMessage(input, command) ? command : Command.INVALID;
    }

    /**
     * Get key from message.
     * We assume the input is a valid getfile|peers|interested|getpieces|data|have
     * message.
     * 
     * @param input
     * @return
     */
    public static String parseKey(String input) {
        String[] tokens = input.split(" ", 3);
        return tokens[1];
    }

    /**
     * Get search query from message.
     * We assume the input is a valid "look" message.
     * 
     * @param input
     * @return
     */
    public static String parseSearchQuery(String input) {
        String[] tokens = input.split(" ", 2);
        return tokens[1];
    }

    /**
     * Get search results from message.
     * We assume the input is a valid "list" message.
     * 
     * @param input
     * @return
     */
    public static String[] parseSearchResult(String input) {
        String[] tokens = input.split(" ", 2);
        return stringToArray(tokens[1], " ");
    }

    /**
     * Get buffermap from message.
     * We assume the input is a valid "have" message.
     * 
     * @param input
     * @return
     */
    public static BufferMap parseBufferMap(String input) {
        String[] tokens = input.split(" ", 3);
        return new BufferMap(Integer.parseInt(tokens[2]));
    }

    /**
     * Parse peers list from a message.
     * We assume the input is a valid "peers" message.
     * 
     * @param input
     * @return
     */
    public static ConnectionInfo[] parsePeers(String input) {
        String[] tokens = input.split(" ", 3);
        String[] args = stringToArray(tokens[2], " ");
        ConnectionInfo[] peers = new ConnectionInfo[args.length];
        
        for (int i = 0; i < args.length; i++) {
            tokens = args[i].split(":");
            String ip = tokens[0];
            int port = Integer.parseInt(tokens[1]);
            peers[i] = new ConnectionInfo(ip, port);
        }

        return peers;
    }

    /**
     * Parse pieces from a message.
     * We assume the input is a valid "data" message.
     * 
     * @param input
     * @return
     */
    public static Map<Integer, byte[]> parsePieces(String input) {
        String[] tokens = input.split(" ", 3);
        String[] args = stringToArray(tokens[2], " ");

        Map<Integer, byte[]> pieces = new HashMap<Integer, byte[]>();
        for (int i = 0; i < args.length; i++) {
            String[] data = args[i].split(":");
            int index = Integer.parseInt(data[0]);
            String hex = data[1];
            byte[] bytes = new byte[hex.length() / 2];
            for (int j = 0; j < hex.length(); j += 2) {
                bytes[j / 2] = (byte) Integer.parseInt(hex.substring(j, j + 2), 16);
            }
            pieces.put(index, bytes);
        }

        return pieces;
    }

    /**
     * Parse indices from a message.
     * We assume the input is a valid "getpieces" message.
     * 
     * @param input
     * @return int[]
     **/
    public static int[] parseIndices(String input) {
        String[] tokens = input.split(" ", 3);
        String[] args = stringToArray(tokens[2], " ");
        int[] indices = Arrays.stream(args).mapToInt(Integer::parseInt).toArray();
        return indices;
    }
}