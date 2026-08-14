/**
 * Entry point of the Shannon chatbot.
 * <p>
 * The class name must match the file name ({@code Shannon.java}) because the
 * class is {@code public} - this is a Java requirement, not just a convention.
 */

import java.util.Scanner;

public class Shannon {

    /** Maximum number of items the chatbot can remember. */
    private static final int MAX_ITEMS = 100;

    /**
     * Stores the items added by the user.
     * Only the first {@code itemCount} slots hold real data; the rest are {@code null}.
     */
    private static final String[] items = new String[MAX_ITEMS];

    /** Number of items currently stored in {@link #items}. */
    private static int itemCount = 0;

    private static void printHorizontalLine() {
        System.out.println("____________________________________________________________");
    }

    /**
     * Stores an item and confirms it to the user.
     * Ignores the item (with a message) once the array is full, since a plain
     * array cannot grow.
     *
     * @param item text entered by the user
     */
    private static void addItem(String item) {
        if (itemCount == MAX_ITEMS) {
            System.out.println("Sorry, my list is full! I can only remember " + MAX_ITEMS + " items.");
            return;
        }
        items[itemCount] = item;
        itemCount++;
        System.out.println("added: " + item);
    }

    /**
     * Prints the stored items in the order they were added, numbered from 1.
     */
    private static void printItems() {
        if (itemCount == 0) {
            System.out.println("Your list is empty!");
            return;
        }
        for (int i = 0; i < itemCount; i++) {
            System.out.println((i + 1) + ". " + items[i]);
        }
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
        String input = scanner.nextLine();

        while (!exitString.equals(input)) {
            printHorizontalLine();
            if (listCommand.equals(input)) {
                printItems();
            } else {
                addItem(input);
            }
            printHorizontalLine();
            input = scanner.nextLine();
        }

        printHorizontalLine();
        System.out.println("Bye. Hope to see you again soon!");
        printHorizontalLine();
    }
}
