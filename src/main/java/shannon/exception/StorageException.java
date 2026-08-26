package shannon.exception;

/**
 * Thrown when the task list cannot be written to, or read from, the save file.
 * <p>
 * It extends {@link ShannonException} so a storage failure is reported through the same single
 * {@code catch} as every other error, and the chatbot keeps running afterwards.
 * <p>
 * The constructor is private and the two cases are built by named factory methods, because
 * saving and loading need different wording and a constructor cannot be overloaded on the
 * same {@code (String, String)} signature twice.
 */
public class StorageException extends ShannonException {

    /** Private, so that every instance is built by one of the two factory methods below. */
    private StorageException(String message) {
        super(message);
    }

    /**
     * Returns the exception for a failed save, which reassures the user that the in-memory
     * list is still correct.
     *
     * @param filePath the save file that could not be written, so the user knows where to look.
     * @param reason   the underlying failure, e.g. {@code "Permission denied"}.
     */
    public static StorageException whileSaving(String filePath, String reason) {
        return new StorageException("I couldn't save your tasks to " + filePath
                + " (" + reason + "). Your list is up to date here,"
                + " but it may be lost when Shannon exits.");
    }

    /**
     * Returns the exception for a failed load, which tells the user the session starts empty.
     *
     * @param filePath the save file that could not be read.
     * @param reason   the underlying failure, e.g. {@code "Is a directory"}.
     */
    public static StorageException whileLoading(String filePath, String reason) {
        return new StorageException("I couldn't read your saved tasks from " + filePath
                + " (" + reason + "). Starting with an empty list.");
    }
}
