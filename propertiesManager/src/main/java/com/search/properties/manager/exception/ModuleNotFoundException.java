package com.search.properties.manager.exception;

/**
 * This exception is thrown when a module cannot be found in a {@link Store} object
 *
 * @author Philip Mark Gutierrez
 * @since September 04, 2013
 * @version 1.0
 */
public class ModuleNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 2002583669494656865L;

    public ModuleNotFoundException(String message) {
        super(message);
    }
}
