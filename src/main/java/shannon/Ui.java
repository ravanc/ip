package shannon;

import java.util.List;
import java.util.Scanner;

import shannon.exception.ShannonException;
import shannon.task.Task;

/**
 * Everything the user sees and types.
 * <p>
 * All reading from {@code System.in} and writing to {@code System.out} happens here, so the rest
 * of the program never has to know that this chatbot talks through a terminal at all. The payoff
 * is that the wording of a message can be changed, or the whole interface swapped for a window,
 * without touching the code that actually manipulates tasks.
 * <p>
 * The methods are named {@code show...} rather than {@code print...} on purpose: they promise to
 * make something visible to the user, not that they do it by printing.
 */
public class Ui {

    /** The line drawn above and below each reply, to separate one exchange from the next. */
    private static final String HORIZONTAL_LINE = "____________________________________________________________";

    /** Reads the user's typing. Kept as a field so the one scanner lasts the whole session. */
    private final Scanner scanner = new Scanner(System.in);

    /** Reads the next line the user types. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Draws the separator line. */
    public void showLine() {
        System.out.println(HORIZONTAL_LINE);
    }

    /** Prints the logo and greeting shown once at start-up. */
    public void showWelcome() {
        String banner = "   oo_    \\\\  //       \\\\\\  ///\\\\\\  ///   .-.   \\\\\\  ///\n"
                + "  /  _)-< (o)(o)   /)  ((O)(O))((O)(O)) c(O_O)c ((O)(O))\n"
                + "  \\__ `.  ||  || (o)(O) | \\ ||  | \\ || ,'.---.`, | \\ ||\n"
                + "     `. | |(__)|  //\\\\  ||\\\\||  ||\\\\||/ /|_|_|\\ \\||\\\\||\n"
                + "     _| | /.--.\\ |(__)| || \\ |  || \\ || \\_____/ ||| \\ |\n"
                + "  ,-'   |-'    `-/,-. | ||  ||  ||  ||'. `---' .`||  ||\n"
                + " (_..--'        -'   ''(_/  \\_)(_/  \\_) `-...-' (_/  \\_)\n";
        System.out.println(banner);
        System.out.println("Hello! I'm Shannon!");
        System.out.println("What can I do for you?");
    }

    /** Prints the parting message. */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    /**
     * Prints the explanation of something that went wrong.
     *
     * @param message the text to show, normally a {@link ShannonException}'s message
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /** Confirms that a task was added, and says how many tasks there are now. */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        showTaskCount(taskCount);
    }

    /** Confirms that a task was deleted, and says how many tasks are left. */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        showTaskCount(taskCount);
    }

    /**
     * Confirms that a task's done flag was changed.
     *
     * @param isDone {@code true} if it was just marked done, {@code false} if it was unmarked
     */
    public void showTaskMarked(Task task, boolean isDone) {
        System.out.println(isDone
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /** Prints the tasks in the order they were added, numbered from 1. */
    public void showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("Your list is empty!");
            return;
        }
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }

    /**
     * Prints the tasks that matched a {@code find}, or says that none did.
     * <p>
     * The numbers shown here count the matches, not the positions in the whole list, so
     * {@code delete 2} after a find does not delete the second task shown. Reusing
     * {@link #showTaskList} keeps one numbering style; only the empty case needs its own
     * wording, since "Your list is empty!" would be untrue when the list has tasks that simply
     * did not match.
     */
    public void showFoundTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("No matching tasks found!");
            return;
        }
        System.out.println("Here are the matching tasks in your list:");
        showTaskList(tasks);
    }

    /**
     * Reports how many tasks were restored from the save file. Says nothing when there were
     * none, so a first run is not cluttered with "I've loaded 0 tasks".
     */
    public void showLoaded(int taskCount) {
        if (taskCount > 0) {
            System.out.println("I've loaded " + taskCount + plural(taskCount, " task", " tasks")
                    + " from your last session.");
        }
    }

    /**
     * Warns that part of the save file could not be read, so the user is not left wondering
     * where those tasks went.
     *
     * @param skippedCount how many lines were skipped
     * @param filePath     the save file, named so the user can go and repair it
     */
    public void showSkippedLines(int skippedCount, String filePath) {
        if (skippedCount > 0) {
            System.out.println("I couldn't understand " + skippedCount
                    + plural(skippedCount, " line", " lines") + " in " + filePath
                    + ", so I've left " + plural(skippedCount, "it", "them") + " out.");
        }
    }

    /** Reports how many tasks are in the list, after one has been added or deleted. */
    private void showTaskCount(int taskCount) {
        System.out.println("Now you have " + taskCount + plural(taskCount, " task", " tasks")
                + " in the list.");
    }

    /**
     * Picks the singular or plural wording for a count.
     * A tiny helper, but it keeps the {@code count == 1 ? ... : ...} test out of five messages.
     */
    private static String plural(int count, String singular, String pluralForm) {
        return count == 1 ? singular : pluralForm;
    }
}
