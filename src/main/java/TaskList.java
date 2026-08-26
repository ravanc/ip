import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The list of tasks, and the operations that change it.
 * <p>
 * The point of wrapping the {@link ArrayList} rather than passing one around is that the list and
 * the rule protecting it now live together. Every command that names a task ({@code mark},
 * {@code unmark}, {@code delete}) has to check that the number refers to a real task; when the
 * list was a bare field, that check sat in a separate method that any new command could forget to
 * call. Here it cannot be skipped, because the only way in is through a method that performs it.
 * <p>
 * <b>Task numbers, not indices.</b> {@link #getTask(int)} and {@link #deleteTask(int)} take the
 * number the user typed, counting from 1, not a position counting from 0. That is why they are
 * named {@code getTask}/{@code deleteTask} rather than {@code get}/{@code remove}: a reader who
 * sees {@code list.get(0)} rightly expects the first element, and calling the methods after the
 * collection ones would invite exactly that mistake. Converting from the user's counting to the
 * list's happens here, once, instead of in each caller.
 */
public class TaskList {

    private final ArrayList<Task> tasks;

    /** Starts an empty list, for a first run or after a save file could not be read. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Starts from tasks already restored from the save file.
     *
     * @param tasks the loaded tasks, in file order; copied so that later changes to the list
     *              cannot be made behind this object's back
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task the user asked for, leaving it in the list.
     *
     * @param taskNumber the number the user typed, counting from 1
     * @throws TaskNotFoundException if no task has that number
     */
    public Task getTask(int taskNumber) throws TaskNotFoundException {
        return tasks.get(indexOf(taskNumber));
    }

    /**
     * Removes the task the user asked for and returns it, so the caller can still show what was
     * deleted.
     *
     * @param taskNumber the number the user typed, counting from 1
     * @throws TaskNotFoundException if no task has that number
     */
    public Task deleteTask(int taskNumber) throws TaskNotFoundException {
        // ArrayList.remove(int) also shifts the later tasks down to close the gap, which with a
        // plain array we would have had to do by hand.
        return tasks.remove(indexOf(taskNumber));
    }

    /** Returns how many tasks are in the list. */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the tasks in the order they were added, for code that only reads them: the
     * {@link Ui} to print, and the {@link Storage} to save.
     * <p>
     * The returned list is unmodifiable, so that handing the tasks out cannot become a second
     * way of changing them that bypasses the checks above.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Turns a task number the user typed into a position in the list, checking it on the way.
     * <p>
     * Private, because a position is this class's own business: no caller outside should ever
     * hold one and risk using it after the list has changed.
     *
     * @param taskNumber the number the user typed, counting from 1
     * @return the matching index, counting from 0
     * @throws TaskNotFoundException if the number does not match any task in the list
     */
    private int indexOf(int taskNumber) throws TaskNotFoundException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new TaskNotFoundException(taskNumber, tasks.size());
        }
        return taskNumber - 1; // the user counts from 1, the list from 0
    }
}
