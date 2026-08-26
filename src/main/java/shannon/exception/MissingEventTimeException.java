package shannon.exception;

/** Thrown when an {@code event} command is missing its {@code /from} or {@code /to} part. */
public class MissingEventTimeException extends ShannonException {

    /** Builds the message showing a correctly formed {@code event} command. */
    public MissingEventTimeException() {
        super("An event needs a /from and a /to, for example: "
                + "event team meeting /from 2026-08-09 2pm /to 4pm");
    }
}
