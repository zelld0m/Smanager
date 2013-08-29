package com.search.properties.manager.exception;

/**
 * This exception is thrown when a store cannot be found in store-properties.xml
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
public class StoreNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 917929091775103802L;

    /**
     * Creates a {@link StoreNotFoundException} exception when a store cannot be found
     *
     * @param message the message to show when the exception occurs
     */
    public StoreNotFoundException(String message) {
        super(message);
    }
}
