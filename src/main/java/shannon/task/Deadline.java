package shannon.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import shannon.exception.InvalidDateException;

/**
 * A task that must be done before a specific date, e.g. {@code submit report by 2026-08-09}.
 * <p>
 * The date is kept as a {@link LocalDate} rather than as the text the user typed, so it can be
 * displayed in a friendlier format than it was entered in, and so later features such as sorting
 * become possible without re-reading the text.
 */
// LocalDate, not LocalDateTime: the commands accept a date only, and storing a time we never ask
// for would be inventing information.
public class Deadline extends Task {

    /** The letter that marks a deadline in the save file. */
    public static final String TYPE_CODE = "D";

    /** The format the user types and the format written to the save file, e.g. {@code 2026-08-09}. */
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * The format shown to the user, e.g. {@code Aug 09 2026}.
     * {@link Locale#ENGLISH} is fixed explicitly so the month name does not change with whatever
     * locale the machine happens to be set to.
     */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** The date the task is due by. */
    private final LocalDate by;

    /**
     * Creates a deadline that is not yet done.
     *
     * @param description what needs to be done
     * @param by          the date it is due by, already parsed by {@link #parseBy(String)}
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        assert by != null : "Deadline date must not be null";
        this.by = by;
    }

    /**
     * Turns the text after {@code /by} into a date.
     * <p>
     * Kept here, next to the formats, so the command handler and the save-file loader agree on
     * exactly which dates are acceptable instead of each having their own copy of the rule.
     *
     * @param text the date as typed, expected in {@code yyyy-mm-dd} form.
     * @return the date it names.
     * @throws InvalidDateException if the text is not a date in that form.
     */
    public static LocalDate parseBy(String text) throws InvalidDateException {
        try {
            return LocalDate.parse(text.trim(), INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            // Translate java.time's parsing error into one of our own, so the command loop only
            // ever has to know about ShannonException.
            throw new InvalidDateException(text.trim());
        }
    }

    /** Renders as {@code [D][X] submit report (by: Aug 09 2026)}. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Renders as {@code D | 0 | submit report | 2026-08-09}.
     * The date is written in the input format, not the display format, so that a saved line can
     * be read back by the same {@link #parseBy(String)} the user's typing goes through.
     */
    @Override
    public String toFileFormat() {
        return encode(TYPE_CODE) + " | " + escape(by.format(INPUT_FORMAT));
    }
}
