package modbot.app;

/**
 * Main is the entry point to the Modbot application
 *
 * <p>Supported args: (No args starts the Discord Bot)</p>
 * <p>No arguments starts the bot.</p>
 * <ul>
 *     <li>--version: Prints the current app version</li>
 * </ul>
 */
public final class Main {
    public static final String APP_VERSION = "1.0.0-SNAPSHOT";

    public static void main(String[] args) {
        if (args.length == 0) {
            // TODO(#10) Start the bot here
            return;
        }

        switch (args[0]) {
            case "--version" -> printVersion();
            default -> System.exit(unknownFlag(args[0]));
        }

    }

    private static void printVersion() {
        System.out.println("Modbot Version: " + APP_VERSION);
    }

    private static int unknownFlag(String flag) {
        System.err.println("Unknown flag: " + flag);
        return 1;
    }
}
