package shannon;

import shannon.exception.InvalidTaskNumberException;
import shannon.exception.ShannonException;
import shannon.exception.StorageException;
import shannon.exception.TaskNotFoundException;
import shannon.exception.UnknownCommandException;
import shannon.task.Task;

/**
 * The Shannon chatbot: a to-do list you talk to.
 * <p>
 * This class is only the conductor. It holds the collaborators that do the actual work and
 * decides, for each command, which of them to ask:
 * <ul>
 *   <li>{@link Ui} &mdash; the wording of every reply, and the terminal</li>
 *   <li>{@link Parser} &mdash; making sense of a typed line</li>
 *   <li>{@link TaskList} &mdash; the tasks, and the rules for reaching them</li>
 *   <li>{@link Storage} &mdash; loading from and saving to the file</li>
 * </ul>
 * They are instance fields rather than statics, so a Shannon is a thing you can make: two with
 * different save files would not interfere, and a test could point one at a scratch file.
 * <p>
 * There are two ways in, and they share all their logic. {@link #run()} is the terminal version:
 * read a line, print the reply, repeat. {@link #getResponse(String)} answers a single line and
 * hands the reply back as a string, which is what the JavaFX window in {@code shannon.gui} uses.
 * Because {@code run()} is written in terms of {@code getResponse}, the two interfaces cannot
 * drift apart.
 */
public class Shannon {

    /** Where the task list is saved by default, relative to the project root. */
    private static final String DATA_FILE_PATH = "./data/duke.txt";

    /** The command word that ends the conversation. */
    private static final String EXIT_COMMAND = "bye";

    /** Builds the wording of every reply, and prints them when running in the terminal. */
    private final Ui ui;

    /** Reads and writes the save file. */
    private final Storage storage;

    /** The tasks. Not final: {@link #getStartupMessage()} replaces it with the restored list. */
    private TaskList tasks;

    /**
     * Sets up the chatbot without touching the disk or the screen.
     * <p>
     * The save file is deliberately <em>not</em> read here. Loading has to report what it found,
     * and those messages belong after the greeting, so reading the file is part of
     * {@link #getStartupMessage()}; the empty list built here is what the program falls back on
     * if that reading fails.
     *
     * @param filePath the save file to use, e.g. {@code ./data/duke.txt}.
     */
    public Shannon(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList();
    }

    /**
     * Sets up the chatbot with the default save file.
     * A convenience for the GUI, which has no reason to choose a different file.
     */
    public Shannon() {
        this(DATA_FILE_PATH);
    }

    /**
     * Greets the user, restores the saved tasks, then answers commands until {@code bye}.
     * This is the terminal version of the chatbot.
     */
    public void run() {
        ui.showLine();
        ui.showLogo();
        ui.showMessage(getStartupMessage());
        ui.showLine();

        String input = ui.readCommand();
        while (!isExitCommand(input)) {
            ui.showLine();
            ui.showMessage(getResponse(input));
            ui.showLine();

            input = ui.readCommand();
        }

        ui.showLine();
        ui.showMessage(getResponse(input));
        ui.showLine();
    }

    /**
     * Returns the reply to one line the user typed.
     * <p>
     * This is the whole chatbot reduced to a single function: text in, text out. Nothing here
     * prints, so the caller is free to put the answer in a terminal, in a chat bubble, or in a
     * test assertion. Every handler reports problems by throwing a {@link ShannonException}, so
     * all failures are turned into a message in this one place instead of being scattered around.
     *
     * @param input one line exactly as the user typed it.
     * @return what the chatbot says in response, ready to be shown as-is.
     */
    public String getResponse(String input) {
        try {
            return handleCommand(new Parser(input));
        } catch (ShannonException e) {
            return ui.getErrorMessage(e.getMessage());
        }
    }

    /**
     * Returns the greeting together with a report of what was restored from the save file, and
     * replaces {@link #tasks} with those restored tasks.
     * <p>
     * Any problem loading is reported and then ignored: a chatbot that refuses to start because
     * of a damaged save file is less useful than one that starts empty and says so. The empty
     * list built by the constructor stays in place in that case, so there is always a list to use.
     *
     * @return everything the user should see before typing their first command.
     */
    public String getStartupMessage() {
        StringBuilder message = new StringBuilder(ui.getWelcomeMessage());
        try {
            tasks = new TaskList(storage.load());
            appendIfPresent(message, ui.getLoadedMessage(tasks.size()));
            appendIfPresent(message,
                    ui.getSkippedLinesMessage(storage.getSkippedLineCount(), storage.getFilePath()));
        } catch (StorageException e) {
            appendIfPresent(message, ui.getErrorMessage(e.getMessage()));
        }
        return message.toString();
    }

    /**
     * Returns whether a line the user typed asks to end the conversation.
     * The GUI needs to know this so it can close the window afterwards.
     *
     * @param input one line exactly as the user typed it.
     * @return {@code true} if the line is the {@code bye} command.
     */
    public boolean isExitCommand(String input) {
        return EXIT_COMMAND.equals(new Parser(input).getCommand());
    }

    /**
     * Adds a message to what has been built so far, on a new line, unless it is empty.
     * The {@code get...Message} methods return an empty string when they have nothing to report,
     * and this keeps those non-reports from leaving blank lines behind.
     *
     * @param message  what has been built so far.
     * @param addition the message to add, which may be empty.
     */
    private static void appendIfPresent(StringBuilder message, String addition) {
        if (!addition.isEmpty()) {
            message.append("\n").append(addition);
        }
    }

    /**
     * Carries out one command the user typed and returns the reply to it.
     * <p>
     * This is the whole vocabulary of the chatbot in one place: each branch names a command and
     * the one thing it does. Pulling the text apart has already happened in {@link Parser}, so
     * nothing here touches a string beyond comparing the command word.
     *
     * @param parser the line the user typed, already split up.
     * @return the reply to that command.
     * @throws UnknownCommandException if the command word is not one we know.
     * @throws ShannonException        if the command was understood but could not be carried out.
     */
    private String handleCommand(Parser parser) throws ShannonException {
        return switch (parser.getCommand()) {
            case EXIT_COMMAND -> ui.getGoodbyeMessage();
            case "list" -> ui.getTaskListMessage(tasks.asList());
            // find only reads the list, so unlike the commands below it does not save afterwards.
            case "find" -> ui.getFoundTasksMessage(tasks.find(parser.parseKeyword()));
            case "mark" -> markTask(parser, true);
            case "unmark" -> markTask(parser, false);
            case "delete" -> deleteTask(parser);
            case "todo" -> addTask(parser.parseTodo());
            case "deadline" -> addTask(parser.parseDeadline());
            case "event" -> addTask(parser.parseEvent());
            default -> throw new UnknownCommandException(parser.getCommand());
        };
    }

    /**
     * Stores an already-built task, writes the whole list to disk, and returns the confirmation.
     * Every {@code todo}/{@code deadline}/{@code event} command funnels through here so the
     * confirmation message and the save live in exactly one place.
     *
     * @param task the task to add.
     * @return the confirmation to show the user.
     * @throws StorageException if the task was added but could not be saved to disk.
     */
    private String addTask(Task task) throws ShannonException {
        tasks.add(task);
        String message = ui.getTaskAddedMessage(task, tasks.size());
        // Saved after the message is built, so a failure here replaces nothing the user has
        // already been told: the task really is in the list, and only the disk copy is missing.
        storage.save(tasks.asList());
        return message;
    }

    /**
     * Handles {@code delete <task number>}.
     *
     * @param parser the line the user typed, already split up.
     * @return the confirmation to show the user.
     * @throws InvalidTaskNumberException if the argument is not a whole number.
     * @throws TaskNotFoundException      if the number does not match any task in the list.
     * @throws StorageException           if the shortened list could not be saved to disk.
     */
    private String deleteTask(Parser parser) throws ShannonException {
        // deleteTask returns the task it removed, so the confirmation can still show what went.
        Task task = tasks.deleteTask(parser.parseTaskNumber());
        String message = ui.getTaskDeletedMessage(task, tasks.size());
        storage.save(tasks.asList());
        return message;
    }

    /**
     * Handles both {@code mark} and {@code unmark}, which differ only in the flag they set.
     *
     * @param parser the line the user typed, already split up.
     * @param isDone {@code true} for {@code mark}, {@code false} for {@code unmark}.
     * @return the confirmation to show the user.
     * @throws InvalidTaskNumberException if the argument is not a whole number.
     * @throws TaskNotFoundException      if the number does not match any task in the list.
     * @throws StorageException           if the changed list could not be saved to disk.
     */
    private String markTask(Parser parser, boolean isDone) throws ShannonException {
        Task task = tasks.getTask(parser.parseTaskNumber());
        if (isDone) {
            task.markDone();
        } else {
            task.unmarkDone();
        }
        String message = ui.getTaskMarkedMessage(task, isDone);
        storage.save(tasks.asList());
        return message;
    }

    /**
     * Starts the chatbot in the terminal, with the default save file.
     * The GUI has its own entry point in {@code shannon.gui.Launcher}.
     *
     * @param args command line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Shannon(DATA_FILE_PATH).run();
    }
}
