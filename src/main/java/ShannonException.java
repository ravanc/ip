/**
 * Base class for every error Shannon reports back to the user.
 * <p>
 * It is a checked exception (it extends {@link Exception}, not {@code RuntimeException})
 * because these are expected mistakes a user makes at the prompt, so the compiler should
 * force the command loop to deal with them.
 * <p>
 * The message of each subclass is the text shown to the user, which lets the command loop
 * handle every error with a single {@code catch} and one {@code println}.
 */
public class ShannonException extends Exception {

    public ShannonException(String message) {
        super(message);
    }
}
