package shannon.exception;

/** Thrown when a date was given in a form we cannot read, e.g. {@code deadline report /by tomorrow}. */
public class InvalidDateException extends ShannonException {

    /**
     * Creates the exception, whose message repeats the offending text and the expected form.
     *
     * @param text the date as the user typed it, echoed back so it is clear which part was wrong.
     */
    public InvalidDateException(String text) {
        super("I couldn't understand the date \"" + text + "\"."
                + " Please write it as yyyy-mm-dd, for example: 2026-08-09");
    }
}
