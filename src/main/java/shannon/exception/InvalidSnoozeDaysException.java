package shannon.exception;

/**
 * Thrown when the number of days given to {@code snooze} is not a whole number of at least 1,
 * e.g. {@code snooze 2 soon} or {@code snooze 2 0}.
 */
public class InvalidSnoozeDaysException extends ShannonException {

    /**
     * Builds the message for a number of days that could not be used.
     *
     * @param days the text that was given as the number of days
     */
    public InvalidSnoozeDaysException(String days) {
        super("\"" + days + "\" is not a number of days I can snooze by. "
                + "Try: snooze 2 3, to push task 2 back by 3 days.");
    }
}
