/**
 * A task with no date or time attached, e.g. {@code visit new theme park}.
 * Adds nothing to {@link Task} except the {@code [T]} type marker.
 */
public class Todo extends Task {

    public Todo(String description) {
        super(description);
    }

    /** Renders as {@code [T][X] visit new theme park}. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
