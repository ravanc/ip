package shannon;

import shannon.exception.InvalidTaskNumberException;
import shannon.exception.ShannonException;
import shannon.exception.StorageException;
import shannon.exception.TaskNotFoundException;
import shannon.exception.UnknownCommandException;
import shannon.task.Task;

/**
 * The Shannon chatbot: a to-do list you talk to at the terminal.
 * <p>
 * This class is only the conductor. It holds the collaborators that do the actual work and
 * decides, for each command, which of them to ask:
 * <ul>
 *   <li>{@link Ui} &mdash; everything the user types and sees</li>
 *   <li>{@link Parser} &mdash; making sense of a typed line</li>
 *   <li>{@link TaskList} &mdash; the tasks, and the rules for reaching them</li>
 *   <li>{@link Storage} &mdash; loading from and saving to the file</li>
 * </ul>
 * They are instance fields rather than statics, so a Shannon is a thing you can make: two with
 * different save files would not interfere, and a test could point one at a scratch file.
 */
public class Shannon {

    /** Where the task list is saved by default, relative to the project root. */
    private static final String DATA_FILE_PATH = "./data/duke.txt";

    /** Handles all reading from and writing to the terminal. */
    private final Ui ui;

    /** Reads and writes the save file. */
    private final Storage storage;

    /** The tasks. Not final: {@link #loadTasks()} replaces it with the list restored from disk. */
    private TaskList tasks;

    /**
     * Sets up the chatbot without touching the disk or the screen.
     * <p>
     * The save file is deliberately <em>not</em> read here. Loading has to report what it found,
     * and those messages belong after the greeting, so reading the file is the first thing
     * {@link #run()} does; the empty list built here is what the program falls back on if that
     * reading fails.
     *
     * @param filePath the save file to use, e.g. {@code ./data/duke.txt}.
     */
    public Shannon(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList();
    }

    /**
     * Greets the user, restores the saved tasks, then answers commands until {@code bye}.
     */
    public void run() {
        ui.showLine();
        ui.showWelcome();
        loadTasks();
        ui.showLine();

        Parser parser = new Parser(ui.readCommand());
        while (!"bye".equals(parser.getCommand())) {
            ui.showLine();
            // Every handler reports problems by throwing a ShannonException, so all error
            // messages are printed here in one place instead of being scattered around.
            try {
                handleCommand(parser);
            } catch (ShannonException e) {
                ui.showError(e.getMessage());
            }
            ui.showLine();

            parser = new Parser(ui.readCommand());
        }

        ui.showLine();
        ui.showGoodbye();
        ui.showLine();
    }

    /**
     * Replaces {@link #tasks} with what the save file holds, and says what was found.
     * <p>
     * Any problem here is reported and then ignored: a chatbot that refuses to start because of
     * a damaged save file is less useful than one that starts empty and says so. The empty list
     * built by the constructor stays in place in that case, so there is always a list to use.
     */
    private void loadTasks() {
        try {
            tasks = new TaskList(storage.load());
            ui.showLoaded(tasks.size());
            ui.showSkippedLines(storage.getSkippedLineCount(), storage.getFilePath());
        } catch (StorageException e) {
            ui.showError(e.getMessage());
        }
    }

    /**
     * Carries out one command the user typed.
     * <p>
     * This is the whole vocabulary of the chatbot in one place: each branch names a command and
     * the one thing it does. Pulling the text apart has already happened in {@link Parser}, so
     * nothing here touches a string beyond comparing the command word.
     *
     * @param parser the line the user typed, already split up.
     * @throws UnknownCommandException if the command word is not one we know.
     * @throws ShannonException        if the command was understood but could not be carried out.
     */
    private void handleCommand(Parser parser) throws ShannonException {
        switch (parser.getCommand()) {
            case "list" -> ui.showTaskList(tasks.asList());
            // find only reads the list, so unlike the commands below it does not save afterwards.
            case "find" -> ui.showFoundTasks(tasks.find(parser.parseKeyword()));
            case "mark" -> markTask(parser, true);
            case "unmark" -> markTask(parser, false);
            case "delete" -> deleteTask(parser);
            case "todo" -> addTask(parser.parseTodo());
            case "deadline" -> addTask(parser.parseDeadline());
            case "event" -> addTask(parser.parseEvent());
            default -> throw new UnknownCommandException(parser.getCommand());
        }
    }

    /**
     * Stores an already-built task, then writes the whole list to disk.
     * Every {@code todo}/{@code deadline}/{@code event} command funnels through here so the
     * confirmation message and the save live in exactly one place.
     *
     * @param task the task to add
     * @throws StorageException if the task was added but could not be saved to disk
     */
    private void addTask(Task task) throws ShannonException {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        // Saved last, so the user always sees the confirmation first: if saving fails, the task
        // really is in the list, and the warning that follows says only that it is not on disk.
        storage.save(tasks.asList());
    }

    /**
     * Handles {@code delete <task number>}.
     *
     * @param parser the line the user typed, already split up
     * @throws InvalidTaskNumberException if the argument is not a whole number
     * @throws TaskNotFoundException      if the number does not match any task in the list
     * @throws StorageException           if the shortened list could not be saved to disk
     */
    private void deleteTask(Parser parser) throws ShannonException {
        // deleteTask returns the task it removed, so the confirmation can still show what went.
        Task task = tasks.deleteTask(parser.parseTaskNumber());
        ui.showTaskDeleted(task, tasks.size());
        storage.save(tasks.asList());
    }

    /**
     * Handles both {@code mark} and {@code unmark}, which differ only in the flag they set.
     *
     * @param parser the line the user typed, already split up
     * @param isDone {@code true} for {@code mark}, {@code false} for {@code unmark}
     * @throws InvalidTaskNumberException if the argument is not a whole number
     * @throws TaskNotFoundException      if the number does not match any task in the list
     * @throws StorageException           if the changed list could not be saved to disk
     */
    private void markTask(Parser parser, boolean isDone) throws ShannonException {
        Task task = tasks.getTask(parser.parseTaskNumber());
        if (isDone) {
            task.markDone();
        } else {
            task.unmarkDone();
        }
        ui.showTaskMarked(task, isDone);
        storage.save(tasks.asList());
    }

    /**
     * Starts the chatbot with the default save file.
     *
     * @param args command line arguments, which are not used
     */
    public static void main(String[] args) {
        new Shannon(DATA_FILE_PATH).run();
    }
}
