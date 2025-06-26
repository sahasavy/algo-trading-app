package com.algo.trading.auth.exception;

/**
 * A generic exception for any authentication-related failures
 * in the auth module.
 */
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
