/**
 * Entry point of the Shannon chatbot.
 * <p>
 * The class name must match the file name ({@code Shannon.java}) because the
 * class is {@code public} - this is a Java requirement, not just a convention.
 */
public class Shannon {
  
    private static void printHorizontalLine() {
        System.out.println("____________________________________________________________");
    }

    public static void main(String[] args) {
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
        System.out.println("Bye. Hope to see you again soon!");
        printHorizontalLine();
    }
}
