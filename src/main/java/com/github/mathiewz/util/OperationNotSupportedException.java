package com.github.mathiewz.util;

import com.github.mathiewz.SlickException;

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
