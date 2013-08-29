package com.search.properties.manager.exception;

/**
 * This exception is thrown when store-properties.xml cannot be loaded
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
public class StorePropertiesXmlNotLoadedException extends RuntimeException {

    private static final long serialVersionUID = -7670144597690240329L;

    /**
     * Creates a {@link StorePropertiesXmlNotLoadedException} when store-properties.xml
     * cannot be loaded
     *
     * @param message the message to show when the exception occurs
     */
    public StorePropertiesXmlNotLoadedException(String message) {
        super(message);
    }
}
