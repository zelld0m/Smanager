package com.search.properties.manager.exception;

/**
 * This exception is thrown when a group cannot be found in a {@link Module} object
 *
 * @author Philip Mark Gutierrez
 * @since September 06, 2013
 * @version 1.0
 */
public class GroupNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 3146689456253257907L;

    public GroupNotFoundException(String message) {
        super(message);
    }
}
