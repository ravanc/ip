package shannon.exception;

/**
 * Thrown when {@code snooze} names a task that has no due date to move, i.e. anything but a
 * deadline. An event's times are kept as the text the user typed, so they cannot be shifted.
 */
public class UnsnoozableTaskException extends ShannonException {

    /**
     * Builds the message for a task that cannot be snoozed.
     *
     * @param taskNumber the number the user typed, counting from 1
     * @param task       the task it refers to, as displayed, so the user can see why
     */
    public UnsnoozableTaskException(int taskNumber, String task) {
        super("I can only snooze deadlines, and task " + taskNumber + " isn't one:\n  " + task);
    }
}
