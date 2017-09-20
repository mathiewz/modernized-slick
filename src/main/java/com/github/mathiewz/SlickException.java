package com.github.mathiewz;

/**
 * A generic exception thrown by everything in the library
 *
 * @author kevin
 */
public class SlickException extends RuntimeException {
    /**
     * Create a new exception with a detail message
     *
     * @param message
     *            The message describing the cause of this exception
     */
    public SlickException(String message) {
        super(message);
    }

    /**
     * Create a new exception with a detail message
     *
     * @param message
     *            The message describing the cause of this exception
     * @param e
     *            The exception causing this exception to be thrown
     */
    public SlickException(String message, Throwable e) {
        super(message, e);
    }
    
    /**
     * Create a new exception with a detail message
     *
     * @param e
     *            The exception causing this exception to be thrown
     */
    public SlickException(Exception e) {
        this(e.getMessage(), e);
    }
}
