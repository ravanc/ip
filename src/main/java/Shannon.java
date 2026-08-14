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
     * Stores an already-built task, or reports that the list is full.
     * Every {@code todo}/{@code deadline}/{@code event} command funnels through here so the
     * confirmation message and the full-list check live in exactly one place.
     */
    private static void addTask(Task task) {
        if (taskCount == MAX_TASKS) {
            System.out.println("Sorry, my list is full! I can only remember " + MAX_TASKS + " tasks.");
            return;
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
     */
    private static void addTodo(String argument) {
        String description = argument.trim();
        if (description.isEmpty()) {
            System.out.println("A todo needs a description, for example: todo visit new theme park");
            return;
        }
        addTask(new Todo(description));
    }

    /**
     * Handles {@code deadline <description> /by <when>}.
     *
     * @param argument text after the command word
     */
    private static void addDeadline(String argument) {
        String[] parts = argument.split(" /by ", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            System.out.println("A deadline needs a /by, for example: deadline submit report /by 11/10/2019 5pm");
            return;
        }
        addTask(new Deadline(parts[0].trim(), parts[1].trim()));
    }

    /**
     * Handles {@code event <description> /from <start> /to <end>}.
     *
     * @param argument text after the command word
     */
    private static void addEvent(String argument) {
        String[] parts = argument.split(" /from ", 2);
        String[] times = parts.length < 2 ? new String[0] : parts[1].split(" /to ", 2);
        if (times.length < 2 || parts[0].trim().isEmpty()
                || times[0].trim().isEmpty() || times[1].trim().isEmpty()) {
            System.out.println("An event needs a /from and a /to, for example: "
                    + "event team meeting /from 2/10/2019 2pm /to 4pm");
            return;
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
     */
    private static void markTask(String argument, boolean isDone) {
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument.trim());
        } catch (NumberFormatException e) {
            System.out.println("Please give me a task number, for example: mark 2");
            return;
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            System.out.println("I don't have a task numbered " + taskNumber + "!");
            return;
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

        String exitString = "bye";
        String listCommand = "list";
        String markPrefix = "mark ";
        String unmarkPrefix = "unmark ";
        String todoPrefix = "todo ";
        String deadlinePrefix = "deadline ";
        String eventPrefix = "event ";
        String input = scanner.nextLine();

        while (!exitString.equals(input)) {
            printHorizontalLine();
            if (listCommand.equals(input)) {
                printTasks();
            } else if (input.startsWith(markPrefix)) {
                markTask(input.substring(markPrefix.length()), true);
            } else if (input.startsWith(unmarkPrefix)) {
                markTask(input.substring(unmarkPrefix.length()), false);
            } else if (input.startsWith(todoPrefix)) {
                addTodo(input.substring(todoPrefix.length()));
            } else if (input.startsWith(deadlinePrefix)) {
                addDeadline(input.substring(deadlinePrefix.length()));
            } else if (input.startsWith(eventPrefix)) {
                addEvent(input.substring(eventPrefix.length()));
            } else {
                // Now that tasks have types, a bare line is no longer enough to build one.
                System.out.println("Sorry, I don't understand that. Try: todo, deadline, event, list, mark, "
                        + "unmark or bye.");
            }
            printHorizontalLine();
            input = scanner.nextLine();
        }

        printHorizontalLine();
        System.out.println("Bye. Hope to see you again soon!");
        printHorizontalLine();
    }
}
