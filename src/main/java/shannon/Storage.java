package shannon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import shannon.exception.InvalidDateException;
import shannon.exception.StorageException;
import shannon.task.Deadline;
import shannon.task.Event;
import shannon.task.Task;
import shannon.task.Todo;

/**
 * Reads and writes the task list as a text file, so it survives between runs.
 * <p>
 * One task per line, fields separated by {@code " | "}:
 * <pre>
 * T | 1 | read book
 * D | 0 | return book | 2026-08-09
 * E | 0 | project meeting | Aug 6th | 2-4pm
 * </pre>
 * The first field is the task type, the second is {@code 1} for done and {@code 0} for not done.
 * Fields are escaped by {@link Task#escape(String)} so that a description containing a {@code |}
 * cannot be mistaken for a field separator, and {@link #splitFields(String)} undoes that on the
 * way back in.
 */
// The whole list is rewritten on every change, which is the simplest thing that works for a list
// this small; appending only what changed costs far more complexity than a to-do list is worth.
public class Storage {

    /** The save file this Storage reads and writes. */
    private final Path file;

    /** How many lines the last {@link #load()} could not understand and skipped. */
    private int skippedLineCount;

    /**
     * Creates a Storage pointing at one save file, without reading or creating it.
     *
     * @param filePath path to the save file, e.g. {@code ./data/duke.txt}
     */
    public Storage(String filePath) {
        this.file = Path.of(filePath);
        // save() creates the file's folder, so the path must name one, as ./data/duke.txt does.
        // A bare "duke.txt" has no parent folder, and save() would fail on it.
        assert file.getParent() != null : "Save file path must include a folder";
    }

    /**
     * Overwrites the save file with the given tasks, creating the data folder if it is missing.
     *
     * @param tasks the current task list, written in the order given.
     * @throws StorageException if the file could not be written.
     */
    public void save(List<Task> tasks) throws StorageException {
        StringBuilder contents = new StringBuilder();
        for (Task task : tasks) {
            contents.append(task.toFileFormat()).append(System.lineSeparator());
        }
        try {
            Files.createDirectories(file.getParent());
            // writeString() creates the file if missing and truncates it if it already exists,
            // so the file always matches the list exactly.
            Files.writeString(file, contents.toString());
        } catch (IOException e) {
            throw StorageException.whileSaving(file.toString(), e.getMessage());
        }
    }

    /**
     * Reads the saved tasks back.
     * <p>
     * A missing file is not an error: it just means this is the first run, so an empty list is
     * returned. A line that cannot be understood is skipped rather than thrown, so one damaged
     * line does not cost the user every other task in the file; {@link #getSkippedLineCount()}
     * reports how many were skipped so the user can be told.
     *
     * @return the tasks that could be read, in file order.
     * @throws StorageException if the file exists but could not be read at all.
     */
    public ArrayList<Task> load() throws StorageException {
        skippedLineCount = 0;
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(file)) {
            return tasks;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            throw StorageException.whileLoading(file.toString(), e.getMessage());
        }
        for (String line : lines) {
            if (line.isBlank()) {
                continue; // a stray blank line is harmless, not something to warn about
            }
            Task task = parseTask(splitFields(line));
            if (task == null) {
                skippedLineCount++;
            } else {
                tasks.add(task);
            }
        }
        return tasks;
    }

    /**
     * Returns the save file this Storage reads and writes, for messages that name it.
     *
     * @return the path as it was given to the constructor
     */
    public String getFilePath() {
        return file.toString();
    }

    /**
     * Returns how many lines the last {@link #load()} skipped because it could not read them.
     *
     * @return the number of skipped lines, or 0 if nothing has been loaded yet
     */
    public int getSkippedLineCount() {
        return skippedLineCount;
    }

    /**
     * Splits one saved line into its fields, undoing the escaping done by
     * {@link Task#escape(String)}.
     *
     * @param line one line of the save file.
     * @return the fields, trimmed of the spaces around each separator.
     */
    private static List<String> splitFields(String line) {
        // The exact inverse of escape(): scan left to right, a backslash consumes the character
        // after it, and only an unescaped | ends a field. String.split() would instead cut the
        // line at an escaped pipe inside a description.
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '\\' && i + 1 < line.length()) {
                char escaped = line.charAt(++i);
                current.append(switch (escaped) {
                    case 'n' -> '\n';
                    case 'r' -> '\r';
                    default -> escaped;
                });
            } else if (character == '|') {
                fields.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }
        fields.add(current.toString().trim());
        return fields;
    }

    /**
     * Rebuilds one task from its fields, or reports that the line was not usable.
     * <p>
     * Every field is checked rather than trusted: the file is an ordinary text file that the
     * user can edit by hand, so a wrong task type, a done flag that is not {@code 0} or
     * {@code 1}, a blank description, or the wrong number of fields are all things that can
     * genuinely appear.
     *
     * @param fields the fields of one line, already unescaped
     * @return the task, or {@code null} if the line could not be understood
     */
    private static Task parseTask(List<String> fields) {
        if (fields.size() < 3) {
            return null;
        }
        String doneFlag = fields.get(1);
        String description = fields.get(2);
        boolean isValidFlag = doneFlag.equals(Task.FLAG_DONE) || doneFlag.equals(Task.FLAG_NOT_DONE);
        if (description.isEmpty() || !isValidFlag) {
            return null;
        }
        // Each task type has a fixed number of fields, so a line with too many or too few is
        // damaged even if its type letter is valid.
        Task task = switch (fields.get(0)) {
            case Todo.TYPE_CODE -> fields.size() == 3 ? new Todo(description) : null;
            case Deadline.TYPE_CODE -> fields.size() == 4 ? parseDeadline(description, fields.get(3)) : null;
            case Event.TYPE_CODE -> fields.size() == 5
                    ? parseEvent(description, fields.get(3), fields.get(4))
                    : null;
            default -> null;
        };
        if (task != null && doneFlag.equals(Task.FLAG_DONE)) {
            task.markDone();
        }
        return task;
    }

    /**
     * Rebuilds an {@link Event} from a saved line.
     *
     * @param description the task description, already unescaped.
     * @param from        the saved start time.
     * @param to          the saved end time.
     * @return the task, or {@code null} if either time is blank, which makes the caller skip
     *         the line like any other damaged one.
     */
    private static Task parseEvent(String description, String from, String to) {
        if (from.isEmpty() || to.isEmpty()) {
            return null;
        }
        return new Event(description, from, to);
    }

    /**
     * Rebuilds a {@link Deadline} from a saved line, reusing {@link Deadline#parseBy(String)} so
     * that the file accepts exactly the dates the user is allowed to type.
     *
     * @param description the task description, already unescaped.
     * @param by          the saved date, expected in {@code yyyy-mm-dd} form.
     * @return the task, or {@code null} if the date could not be read, which makes the caller
     *         skip the line like any other damaged one.
     */
    // A separate method rather than another branch of the switch in parseTask(), because a
    // try/catch does not fit inside a switch expression's arrow.
    private static Task parseDeadline(String description, String by) {
        try {
            return new Deadline(description, Deadline.parseBy(by));
        } catch (InvalidDateException e) {
            return null;
        }
    }
}
