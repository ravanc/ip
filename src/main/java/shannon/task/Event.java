package shannon.task;

/**
 * A task that starts at a specific date/time and ends at a specific date/time,
 * e.g. {@code team project meeting 2026-08-09 2pm to 4pm}.
 */
public class Event extends Task {

    protected String from;
    protected String to;

    /**
     * Creates an event that is not done yet.
     *
     * @param description what the event is, as the user described it.
     * @param from        when the event starts, as the user typed it.
     * @param to          when the event ends, as the user typed it.
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
        return encode("E") + " | " + escape(from) + " | " + escape(to);
    }
}
