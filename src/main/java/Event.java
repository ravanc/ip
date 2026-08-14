/**
 * A task that starts at a specific date/time and ends at a specific date/time,
 * e.g. {@code team project meeting 2/10/2019 2pm to 4pm}.
 */
public class Event extends Task {

    protected String from;
    protected String to;

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
}
