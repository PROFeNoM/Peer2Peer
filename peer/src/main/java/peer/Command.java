package peer;

/**
 * Class representing all the available commands.
 */
public enum Command {
    ANNOUNCE,
    LOOK,
    LIST,
    GETFILE,
    PEERS,
    INTERESTED,
    HAVE,
    GETPIECES,
    DATA,
    OK,
    EXIT,
    UNKNOWN,
    INVALID;

    /**
     * Return a regex to validate the given command.
     * 
     * @param command Command to get the regex of.
     * @return The regex of the command.
     */
    public static String getRegex(Command command) {
        switch (command) {
            case ANNOUNCE:
                return "^announce listen [0-9]*( seed \\[(.* \\d* \\d* [0-9a-f]+)*\\])?( leech \\[([a-f0-9 ]+)*\\])?$";
            case LOOK:
                // TODO: check criterion
                return "^look \\[.*\\]$";
            case LIST:
                return "^list \\[.*\\]$";
            case GETFILE:
                // TODO: check hash length
                return "^getfile [0-9a-f]+$";
            case PEERS:
                return "^peers [0-9a-f]+ \\[.*\\]$";
            case INTERESTED:
                return "^interested [0-9a-f]+$";
            case HAVE:
                return "^have [0-9a-f]+ \\d*$";
            case GETPIECES:
                return "^getpieces [0-9a-f]+ \\[.*\\]$";
            case DATA:
                return "^data [0-9a-f]+ \\[.*\\]$";
            case OK:
                return "^ok$";
            case EXIT:
                return "^exit$";
            default:
                // TODO: is this the right way to handle this?
                return "^.*$";
        }
    }

    /**
     * Return the command corresponding to the given string.
     * 
     * @param command Command to get the command of.
     * @return The command corresponding to the given string.
     */
    public static Command fromString(String command) {
        try {
            return Command.valueOf(command.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    /**
     * Get the string representation of the command.
     * 
     * @return Command name in lowercase.
     */
    public static String toString(Command command) {
        return command.name().toLowerCase();
    }
}