package org.newdawn.slick.util;

import org.newdawn.slick.SlickException;

/**
 * Thrown to indicate that a limited implementation of a class can not
 * support the operation requested.
 *
 * @author kevin
 */
public class OperationNotSupportedException extends SlickException {
    /**
     * Create a new exception
     *
     * @param msg
     *            The message describing the limitation
     */
    public OperationNotSupportedException(String msg) {
        super(msg);
    }
}
