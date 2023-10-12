package com.madirex.exceptions;

/**
 * Excepción base
 */
public abstract class FunkoException extends Exception {
    /**
     * Constructor
     *
     * @param message mensaje de error
     */
    public FunkoException(String message) {
        super(message);
    }
}