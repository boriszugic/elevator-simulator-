package src;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class implementing a Logger which writes details relevant to the elevator system actions
 * and corresponding information utilized for debugging.
 */
public class Logger {
    private final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final PrintWriter out;


    /**
     * Constructs a SimpleLogger that writes to the specified file.
     *
     * @param filePath Path to the log file.
     */
    public Logger(String filePath) {
        try {
            this.out = new PrintWriter(new FileWriter(filePath, false));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Logs a debug message to the file with a timestamp.
     *
     * @param message The message to log.
     */
    public void debug(String message) {
        log("DEBUG", message);
    }

    /**
     * Logs a warning message to the file with a timestamp.
     *
     * @param message The message to log.
     */
    public void warning(String message) {
        log("WARNING", message);
    }

    /**
     * Logs an error message to the file with a timestamp.
     *
     * @param message The message to log.
     */
    public void error(String message) {
        log("ERROR", message);
    }

    /**
     * Helper method to log messages with a severity level.
     */
    private void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(dtFormatter);
        out.println(timestamp + " " + level + ": " + message);
        out.flush(); // Ensure the message is immediately written to the file
    }
}