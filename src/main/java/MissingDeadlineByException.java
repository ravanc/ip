/** Thrown when a {@code deadline} command has no {@code /by} part, or an empty one. */
public class MissingDeadlineByException extends ShannonException {

    public MissingDeadlineByException() {
        super("A deadline needs a /by, for example: deadline submit report /by 11/10/2019 5pm");
    }
}
