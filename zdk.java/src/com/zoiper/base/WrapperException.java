package com.zoiper.base;

/**
 * The main point of interception of errors generated in
 * native wrapper library. It's based on LIBRESULT definition
 * and return the actual result in the message.
 *
 * @author Ilian Georgiev
 */
public class WrapperException extends Exception {

    private static final long serialVersionUID = 1L;

    public WrapperException() {
    }

    public WrapperException(String msg) {
        super(msg);
    }
}