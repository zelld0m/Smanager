package com.search.properties.manager.exception;

/**
 * This exception is thrown when a property cannot be found in a {@link Module} object
 *
 * @author Philip Mark Gutierrez
 * @since September 05, 2013
 * @version 1.0
 */
public class PropertyNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1578837776108623084L;

    public PropertyNotFoundException(String message) {
        super(message);
    }
}
