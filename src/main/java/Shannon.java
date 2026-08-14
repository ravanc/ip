/** Entry point of the Shannon chatbot. */

import java.util.Scanner;

public class Shannon {

    private static final int MAX_TASKS = 100;
    private static final Task[] tasks = new Task[MAX_TASKS];
    private static int taskCount = 0;

    private static void printHorizontalLine() {
        System.out.println("____________________________________________________________");
    }

    /**
     * Stores an already-built task.
     * Every {@code todo}/{@code deadline}/{@code event} command funnels through here so the
     * confirmation message and the full-list check live in exactly one place.
     *
     * @throws TaskListFullException if the list already holds {@value #MAX_TASKS} tasks
     */
    private static void addTask(Task task) throws TaskListFullException {
        if (taskCount == MAX_TASKS) {
            throw new TaskListFullException(MAX_TASKS);
        }
        tasks[taskCount] = task;
        taskCount++;
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + (taskCount == 1 ? " task" : " tasks") + " in the list.");
    }

    /**
     * Handles {@code todo <description>}.
     *
     * @param argument text after the command word
     * @throws EmptyDescriptionException if no description was given
     * @throws TaskListFullException     if the list is full
     */
    private static void addTodo(String argument) throws ShannonException {
        String description = argument.trim();
        if (description.isEmpty()) {
            throw new EmptyDescriptionException("todo", "todo visit new theme park");
        }
        addTask(new Todo(description));
    }

    /**
     * Handles {@code deadline <description> /by <when>}.
     *
     * @param argument text after the command word
     * @throws MissingDeadlineByException if the {@code /by} part is missing or blank
     * @throws EmptyDescriptionException  if no description was given before the {@code /by}
     * @throws TaskListFullException      if the list is full
     */
    private static void addDeadline(String argument) throws ShannonException {
        // Split on the marker itself rather than " /by ", so that "deadline /by Friday"
        // is reported as a missing description rather than a missing /by.
        String[] parts = argument.split("/by", 2);
        // The two checks are separate so the user is told exactly which half is missing.
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new MissingDeadlineByException();
        }
        if (parts[0].trim().isEmpty()) {
            throw new EmptyDescriptionException("deadline", "deadline submit report /by 11/10/2019 5pm");
        }
        addTask(new Deadline(parts[0].trim(), parts[1].trim()));
    }

    /**
     * Handles {@code event <description> /from <start> /to <end>}.
     *
     * @param argument text after the command word
     * @throws MissingEventTimeException if the {@code /from} or {@code /to} part is missing or blank
     * @throws EmptyDescriptionException if no description was given before the {@code /from}
     * @throws TaskListFullException     if the list is full
     */
    private static void addEvent(String argument) throws ShannonException {
        // Split on the markers themselves (see addDeadline) so a missing description is
        // reported as such instead of looking like a missing /from.
        String[] parts = argument.split("/from", 2);
        String[] times = parts.length < 2 ? new String[0] : parts[1].split("/to", 2);
        if (times.length < 2 || times[0].trim().isEmpty() || times[1].trim().isEmpty()) {
            throw new MissingEventTimeException();
        }
        if (parts[0].trim().isEmpty()) {
            throw new EmptyDescriptionException("event", "event team meeting /from 2/10/2019 2pm /to 4pm");
        }
        addTask(new Event(parts[0].trim(), times[0].trim(), times[1].trim()));
    }

    /** Prints the tasks in the order they were added, numbered from 1. */
    private static void printTasks() {
        if (taskCount == 0) {
            System.out.println("Your list is empty!");
            return;
        }
        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + ". " + tasks[i]);
        }
    }

    /**
     * Handles both {@code mark} and {@code unmark}, which differ only in the flag they set.
     *
     * @param argument text after the command word, expected to be a task number counted from 1
     * @param isDone   {@code true} for {@code mark}, {@code false} for {@code unmark}
     * @throws InvalidTaskNumberException if the argument is not a whole number
     * @throws TaskNotFoundException      if the number does not match any task in the list
     */
    private static void markTask(String argument, boolean isDone) throws ShannonException {
        String command = isDone ? "mark" : "unmark";
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument.trim());
        } catch (NumberFormatException e) {
            // Translate Java's low-level parsing error into one of our own, so the command
            // loop only ever has to know about ShannonException.
            throw new InvalidTaskNumberException(command, argument.trim());
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new TaskNotFoundException(taskNumber, taskCount);
        }
        Task task = tasks[taskNumber - 1]; // account for one-indexing
        if (isDone) {
            task.markDone();
            System.out.println("Nice! I've marked this task as done:");
        } else {
            task.unmarkDone();
            System.out.println("OK, I've marked this task as not done yet:");
        }
        System.out.println("  " + task);
    }

    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in); 


        String banner = "   oo_    \\\\  //       \\\\\\  ///\\\\\\  ///   .-.   \\\\\\  ///\n"
                + "  /  _)-< (o)(o)   /)  ((O)(O))((O)(O)) c(O_O)c ((O)(O))\n"
                + "  \\__ `.  ||  || (o)(O) | \\ ||  | \\ || ,'.---.`, | \\ ||\n"
                + "     `. | |(__)|  //\\\\  ||\\\\||  ||\\\\||/ /|_|_|\\ \\||\\\\||\n"
                + "     _| | /.--.\\ |(__)| || \\ |  || \\ || \\_____/ ||| \\ |\n"
                + "  ,-'   |-'    `-/,-. | ||  ||  ||  ||'. `---' .`||  ||\n"
                + " (_..--'        -'   ''(_/  \\_)(_/  \\_) `-...-' (_/  \\_)\n";
        printHorizontalLine();
        System.out.println(banner);
        System.out.println("Hello! I'm Shannon!"); 
        System.out.println("What can I do for you?");
        printHorizontalLine();

        String input = scanner.nextLine();

        // Split into the command word and everything after it, so that a command typed on its
        // own (e.g. "todo") is still recognised and can report the right error.
        String[] words = input.trim().split("\\s+", 2);
        String command = words[0];
        String argument = words.length > 1 ? words[1] : "";

        while (!"bye".equals(command)) {
            printHorizontalLine();
            // Every handler reports problems by throwing a ShannonException, so all error
            // messages are printed here in one place instead of being scattered around.
            try {
                if ("list".equals(command)) {
                    printTasks();
                } else if ("mark".equals(command)) {
                    markTask(argument, true);
                } else if ("unmark".equals(command)) {
                    markTask(argument, false);
                } else if ("todo".equals(command)) {
                    addTodo(argument);
                } else if ("deadline".equals(command)) {
                    addDeadline(argument);
                } else if ("event".equals(command)) {
                    addEvent(argument);
                } else {
                    throw new UnknownCommandException(command);
                }
            } catch (ShannonException e) {
                System.out.println(e.getMessage());
            }
            printHorizontalLine();

            input = scanner.nextLine();
            words = input.trim().split("\\s+", 2);
            command = words[0];
            argument = words.length > 1 ? words[1] : "";
        }

        printHorizontalLine();
        System.out.println("Bye. Hope to see you again soon!");
        printHorizontalLine();
    }
}
