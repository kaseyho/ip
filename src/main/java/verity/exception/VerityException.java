package verity.exception;

/**
 * Represents an error caused by invalid user input or corrupted task data.
 */
public class VerityException extends Exception {

    /**
     * Creates an exception with the specified user-facing message.
     *
     * @param message Description of the error.
     */
    public VerityException(String message) {
        super(message);
    }
}
