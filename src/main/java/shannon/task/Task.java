package shannon.task;

/**
 * A single task: its description, and whether it is done.
 * Fields are {@code protected} so that subclasses can reuse them.
 */
public abstract class Task {
    /** What the user wants to do, exactly as they typed it. */
    protected String description;

    /** Whether the task has been marked done. New tasks start not done. */
    protected boolean isDone;

    /**
     * Creates a task that is not yet done.
     *
     * @param description what the user wants to do
     */
    public Task(String description) {
        // Parser and Storage both reject a blank description before building a task, so a blank
        // one reaching here means a new caller forgot that check.
        assert description != null && !description.isBlank() : "Task description must not be blank";
        this.description = description;
        this.isDone = false;
    }

    /** Returns the task's description, exactly as the user typed it. */
    public String getDescription() {
        return description;
    }

    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as done. */
    public void markDone() {
        this.isDone = true;
    }

    /** Marks this task as not done yet, undoing a {@link #markDone()}. */
    public void unmarkDone() {
        this.isDone = false;
    }

    /** Renders as {@code [X] read book}. */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns this task as one line of the save file, e.g. {@code T | 1 | read book}.
     * Each subclass supplies its own type letter and any extra fields it needs.
     *
     * @return the line to write to the save file
     */
    public abstract String toFileFormat();

    /**
     * Builds the part of the save line that every task shares.
     *
     * @param type the single letter identifying the subclass in the save file.
     * @return e.g. {@code T | 1 | read book}.
     */
    protected String encode(String type) {
        // The type and the done flag are values we control, so only the description needs escaping.
        return type + " | " + (isDone ? "1" : "0") + " | " + escape(description);
    }

    /**
     * Escapes one field so that it always survives a round trip through the save file.
     * <p>
     * {@code \\}, {@code \|}, {@code \n} and {@code \r} stand for backslash, pipe, newline and
     * carriage return; without this a description such as {@code buy milk | eggs} would look like
     * an extra field when the file is read back.
     *
     * @param field the raw text of one field.
     * @return the same text, safe to write as one part of one line.
     */
    protected static String escape(String field) {
        // A single pass, not chained String.replace() calls: chaining is correct only if backslash
        // happens to be replaced first, and quietly corrupts the field if anyone reorders it.
        StringBuilder escaped = new StringBuilder();
        for (char character : field.toCharArray()) {
            switch (character) {
                case '\\' -> escaped.append("\\\\");
                case '|' -> escaped.append("\\|");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                default -> escaped.append(character);
            }
        }
        return escaped.toString();
    }
}
