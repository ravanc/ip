/** Thrown when an {@code event} command is missing its {@code /from} or {@code /to} part. */
public class MissingEventTimeException extends ShannonException {

    public MissingEventTimeException() {
        super("An event needs a /from and a /to, for example: "
                + "event team meeting /from 2/10/2019 2pm /to 4pm");
    }
}
