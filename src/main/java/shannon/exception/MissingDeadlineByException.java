package shannon.exception;

/** Thrown when a {@code deadline} command has no {@code /by} part, or an empty one. */
public class MissingDeadlineByException extends ShannonException {

    /** Builds the message showing a correctly formed {@code deadline} command. */
    public MissingDeadlineByException() {
        super("A deadline needs a /by, for example: deadline submit report /by 2026-08-09");
    }
}
