/** Thrown when a task is added but the list has already reached its maximum size. */
public class TaskListFullException extends ShannonException {

    /**
     * @param maxTasks the maximum number of tasks Shannon can hold
     */
    public TaskListFullException(int maxTasks) {
        super("Sorry, my list is full! I can only remember " + maxTasks + " tasks.");
    }
}
