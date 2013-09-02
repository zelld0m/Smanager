package com.search.properties.manager.exception;

/**
 * <p>
 * Should be thrown when the file path specified is not a directory
 * </p>
 * <p><b>Note:</b>&nbsp;This class is already available in <i>java.nio</i> package, and
 * should be removed when the project is upgraded to Java 7</p>
 *
 * @author Philip Mark Gutierrez
 * @since August 30, 2013
 * @version 1.0
 */
public class NotDirectoryException extends RuntimeException {

    private static final long serialVersionUID = 3476671777390249377L;

    /**
     * Creates a {@link NotDirectoryException} exception when a store cannot be found
     *
     * @param message the message to show when the exception occurs
     */
    public NotDirectoryException(String message) {
        super(message);
    }
}
