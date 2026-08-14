/** Entry point of the Shannon chatbot. */

import java.util.Scanner;

public class Shannon {

    private static final int MAX_TASKS = 100;
    private static final Task[] tasks = new Task[MAX_TASKS];
    private static int taskCount = 0;

    private static void printHorizontalLine() {
        System.out.println("____________________________________________________________");
    }

    /** Stores the user's text as a new {@link Task}, or reports that the list is full. */
    private static void addTask(String description) {
        if (taskCount == MAX_TASKS) {
            System.out.println("Sorry, my list is full! I can only remember " + MAX_TASKS + " tasks.");
            return;
        }
        tasks[taskCount] = new Task(description);
        taskCount++;
        System.out.println("added: " + description);
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
        String input = scanner.nextLine();

        while (!exitString.equals(input)) {
            printHorizontalLine();
            if (listCommand.equals(input)) {
                printTasks();
            } else if (input.startsWith(markPrefix)) {
                markTask(input.substring(markPrefix.length()), true);
            } else if (input.startsWith(unmarkPrefix)) {
                markTask(input.substring(unmarkPrefix.length()), false);
            } else {
                addTask(input);
            }
            printHorizontalLine();
            input = scanner.nextLine();
        }

        printHorizontalLine();
        System.out.println("Bye. Hope to see you again soon!");
        printHorizontalLine();
    }
}
