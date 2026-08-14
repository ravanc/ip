/** Thrown when the first word of the input is not a command Shannon knows. */
public class UnknownCommandException extends ShannonException {

    private static final String COMMAND_LIST = "Try: todo, deadline, event, list, mark, unmark or bye.";

    /**
     * @param command the unrecognised command word, or an empty string if the user entered a blank line
     */
    public UnknownCommandException(String command) {
        super(command.isEmpty()
                ? "You didn't type anything! " + COMMAND_LIST
                : "Sorry, I don't know what \"" + command + "\" means. " + COMMAND_LIST);
    }
}
