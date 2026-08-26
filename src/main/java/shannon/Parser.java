package shannon;

import shannon.exception.EmptyDescriptionException;
import shannon.exception.EmptyKeywordException;
import shannon.exception.InvalidDateException;
import shannon.exception.InvalidTaskNumberException;
import shannon.exception.MissingDeadlineByException;
import shannon.exception.MissingEventTimeException;
import shannon.exception.ShannonException;
import shannon.task.Deadline;
import shannon.task.Event;
import shannon.task.Task;
import shannon.task.Todo;

/**
 * One line the user typed, broken into the parts the rest of the program needs.
 * <p>
 * A {@code Parser} is made from a single line of input and can then answer questions about it:
 * which command it is, and what the text after the command word means for that particular
 * command. Everything that pulls a line of text apart lives here, so the rest of the program
 * works with task numbers and {@link Task} objects rather than with strings.
 * <p>
 * <b>Why an object rather than static helper methods.</b> The obvious alternative is a class of
 * static methods taking the text as an argument. That works, but nearly every one of them needs
 * both halves of the line: the argument to read, and the command word so an error message can
 * show a matching example ({@code Try: delete 2} after a bad {@code delete}). Passing that pair
 * to every call, and keeping the two halves in step, is exactly the bookkeeping an object exists
 * to remove. Splitting once in the constructor also means a line cannot be split two different
 * ways in two different places.
 * <p>
 * A parse method reports a line it cannot make sense of by throwing a {@link ShannonException},
 * so the command loop keeps handling every error in one {@code catch}.
 */
public class Parser {

    /** The first word, e.g. {@code deadline}. Empty if the user typed nothing. */
    private final String command;

    /** Everything after the command word, untrimmed of its inner structure. Never null. */
    private final String argument;

    /**
     * Splits a line into its command word and the rest.
     *
     * @param input one line exactly as the user typed it
     */
    public Parser(String input) {
        // Limit of 2, so that a command typed on its own (e.g. "todo") still yields a command
        // word and an empty argument, and can therefore report the right error.
        String[] words = input.trim().split("\\s+", 2);
        this.command = words[0];
        this.argument = words.length > 1 ? words[1] : "";
    }

    /** Returns the command word, which the caller compares against the commands it knows. */
    public String getCommand() {
        return command;
    }

    /**
     * Reads the task number typed after the command word.
     * <p>
     * Only the reading is done here. Whether that number refers to a task that exists is
     * {@link TaskList}'s business, and is checked when the number is used.
     *
     * @return the number the user typed, counting from 1
     * @throws InvalidTaskNumberException if the argument is not a whole number
     */
    public int parseTaskNumber() throws ShannonException {
        try {
            return Integer.parseInt(argument.trim());
        } catch (NumberFormatException e) {
            // Translate Java's low-level parsing error into one of our own, so the command
            // loop only ever has to know about ShannonException.
            throw new InvalidTaskNumberException(command, argument.trim());
        }
    }

    /**
     * Reads {@code find <keyword>}.
     * <p>
     * The whole of the rest of the line is the keyword, so {@code find team meeting} looks for
     * that phrase rather than for either word on its own. Splitting the argument into separate
     * words would be a larger feature: it would have to decide whether a task must contain all
     * of them or any of them, which is more than the command needs to be useful.
     *
     * @return the text to search for, with the spaces around it removed
     * @throws EmptyKeywordException if no keyword was given
     */
    public String parseKeyword() throws ShannonException {
        String keyword = argument.trim();
        if (keyword.isEmpty()) {
            throw new EmptyKeywordException();
        }
        return keyword;
    }

    /**
     * Reads {@code todo <description>}.
     *
     * @throws EmptyDescriptionException if no description was given
     */
    public Task parseTodo() throws ShannonException {
        String description = argument.trim();
        if (description.isEmpty()) {
            throw new EmptyDescriptionException("todo", "todo visit new theme park");
        }
        return new Todo(description);
    }

    /**
     * Reads {@code deadline <description> /by <yyyy-mm-dd>}.
     *
     * @throws MissingDeadlineByException if the {@code /by} part is missing or blank
     * @throws EmptyDescriptionException  if no description was given before the {@code /by}
     * @throws InvalidDateException       if the {@code /by} part is not a date in {@code yyyy-mm-dd} form
     */
    public Task parseDeadline() throws ShannonException {
        // Split on the marker itself rather than " /by ", so that "deadline /by Friday"
        // is reported as a missing description rather than a missing /by.
        String[] parts = argument.split("/by", 2);
        // The two checks are separate so the user is told exactly which half is missing.
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new MissingDeadlineByException();
        }
        if (parts[0].trim().isEmpty()) {
            throw new EmptyDescriptionException("deadline", "deadline submit report /by 2026-08-09");
        }
        // The date is turned into a LocalDate here, at the edge of the program, so that a
        // Deadline object can never hold a date we failed to understand.
        return new Deadline(parts[0].trim(), Deadline.parseBy(parts[1]));
    }

    /**
     * Reads {@code event <description> /from <start> /to <end>}.
     *
     * @throws MissingEventTimeException if the {@code /from} or {@code /to} part is missing or blank
     * @throws EmptyDescriptionException if no description was given before the {@code /from}
     */
    public Task parseEvent() throws ShannonException {
        // Split on the markers themselves (see parseDeadline) so a missing description is
        // reported as such instead of looking like a missing /from.
        String[] parts = argument.split("/from", 2);
        String[] times = parts.length < 2 ? new String[0] : parts[1].split("/to", 2);
        if (times.length < 2 || times[0].trim().isEmpty() || times[1].trim().isEmpty()) {
            throw new MissingEventTimeException();
        }
        if (parts[0].trim().isEmpty()) {
            throw new EmptyDescriptionException("event", "event team meeting /from 2026-08-09 2pm /to 4pm");
        }
        return new Event(parts[0].trim(), times[0].trim(), times[1].trim());
    }
}
