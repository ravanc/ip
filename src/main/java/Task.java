/**
 * A single task: its description, and whether it is done.
 * Fields are {@code protected} so that future subclasses can reuse them.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    public void markDone() {
        this.isDone = true;
    }

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
     */
    public abstract String toFileFormat();

    /**
     * Builds the part of the save line that every task shares.
     * The type and the done flag are values we control, so only the description needs escaping.
     *
     * @param type the single letter identifying the subclass in the save file
     * @return e.g. {@code T | 1 | read book}
     */
    protected String encode(String type) {
        return type + " | " + (isDone ? "1" : "0") + " | " + escape(description);
    }

    /**
     * Escapes one field so that it always survives a round trip through the save file.
     * <p>
     * Without this, a description such as {@code buy milk | eggs} would look like an extra
     * field when the file is read back, and a {@code \} before a separator could hide it.
     * <p>
     * The rule is the usual one: a backslash starts an escape, and
     * {@code \\}, {@code \|}, {@code \n} and {@code \r} stand for backslash, pipe, newline and
     * carriage return. To decode, scan left to right and on each backslash consume the next
     * character, mapping {@code n} and {@code r} to the line breaks and anything else to itself.
     * <p>
     * Written as a single pass rather than chained {@link String#replace} calls: chaining is
     * correct only if backslash happens to be replaced first, and quietly corrupts the field
     * if anyone reorders the calls. A single pass cannot be broken that way.
     *
     * @param field the raw text of one field
     * @return the same text, safe to write as one part of one line
     */
    protected static String escape(String field) {
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
