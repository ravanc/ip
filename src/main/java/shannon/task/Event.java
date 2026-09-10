package shannon.task;

/**
 * A task that starts at a specific date/time and ends at a specific date/time,
 * e.g. {@code team project meeting 2026-08-09 2pm to 4pm}.
 */
public class Event extends Task {

    /** The letter that marks an event in the save file. */
    public static final String TYPE_CODE = "E";

    /** When the event starts, kept as the text the user typed. */
    private final String from;

    /** When the event ends, kept as the text the user typed. */
    private final String to;

    /**
     * Creates an event that is not yet done.
     *
     * @param description what the event is
     * @param from        when it starts, as typed
     * @param to          when it ends, as typed
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Renders as {@code [E][X] team meeting (from: 2pm to: 4pm)}. */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    /** Renders as {@code E | 0 | team meeting | 2026-08-09 2pm | 4pm}. */
    @Override
    public String toFileFormat() {
        return encode(TYPE_CODE) + " | " + escape(from) + " | " + escape(to);
    }
}
