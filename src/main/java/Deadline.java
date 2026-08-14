/**
 * A task that must be done before a specific date/time,
 * e.g. {@code submit report by 11/10/2019 5pm}.
 */
public class Deadline extends Task {

    protected String by;

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /** Renders as {@code [D][X] submit report (by: 11/10/2019 5pm)}. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
