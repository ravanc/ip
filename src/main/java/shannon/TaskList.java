package shannon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import shannon.exception.TaskNotFoundException;
import shannon.task.Task;

/**
 * The list of tasks, and the operations that change it.
 * <p>
 * Wrapping the {@link ArrayList} keeps the list and the rule protecting it together: every command
 * that names a task ({@code mark}, {@code unmark}, {@code delete}) must check that the number
 * refers to a real task, and here that check cannot be skipped, because the only way in is through
 * a method that performs it.
 * <p>
 * {@link #getTask(int)} and {@link #deleteTask(int)} take the number the user typed, counting from
 * 1, not a position counting from 0 &mdash; hence those names rather than {@code get}/{@code remove},
 * which a reader would rightly expect to be zero-based.
 */
public class TaskList {

    /** The tasks, in the order they were added. */
    private final ArrayList<Task> tasks;

    /** Starts an empty list, for a first run or after a save file could not be read. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Starts from tasks already restored from the save file.
     *
     * @param tasks the loaded tasks, in file order; copied so that later changes to the list
     *              cannot be made behind this object's back.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task the user asked for, leaving it in the list.
     *
     * @param taskNumber the number the user typed, counting from 1
     * @return the task with that number
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
     * @return the task that was removed
     * @throws TaskNotFoundException if no task has that number
     */
    public Task deleteTask(int taskNumber) throws TaskNotFoundException {
        // ArrayList.remove(int) also shifts the later tasks down to close the gap, which with a
        // plain array we would have had to do by hand.
        return tasks.remove(indexOf(taskNumber));
    }

    /**
     * Returns every task whose description contains {@code keyword}, in list order.
     * <p>
     * The match ignores case and looks anywhere in the description, so {@code find book} also
     * finds "Bookshop trip": someone searching their own list is recalling it roughly, not
     * quoting it. Only the description is searched, not a deadline's date or an event's times.
     * <p>
     * The result is a plain {@link List} rather than another {@code TaskList}, because it is a
     * snapshot to be shown and nothing more: deleting from it would not delete from the real
     * list, so it should not offer the methods that look as though it would.
     *
     * @param keyword the text to look for; may be several words
     * @return the matching tasks, unmodifiable and possibly empty
     */
    public List<Task> find(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(lowerKeyword)) {
                matches.add(task);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    /** Returns how many tasks are in the list. */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the tasks in the order they were added, for code that only reads them: the
     * {@link Ui} to print, and the {@link Storage} to save.
     *
     * @return an unmodifiable view, so that handing the tasks out cannot become a second way of
     *         changing them that bypasses the checks above
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
     * @param taskNumber the number the user typed, counting from 1.
     * @return the matching index, counting from 0.
     * @throws TaskNotFoundException if the number does not match any task in the list.
     */
    private int indexOf(int taskNumber) throws TaskNotFoundException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new TaskNotFoundException(taskNumber, tasks.size());
        }
        return taskNumber - 1; // the user counts from 1, the list from 0
    }
}
