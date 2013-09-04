package com.search.properties.manager.exception;

/**
 * This exception is thrown when a store property cannot be found in a store specific
 * properties file
 *
 * @author Philip Mark Gutierrez
 * @since September 04, 2013
 * @version 1.0
 */
public class StorePropertyNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -6883589197801589614L;

    public StorePropertyNotFoundException(String message) {
        super(message);
    }
}
