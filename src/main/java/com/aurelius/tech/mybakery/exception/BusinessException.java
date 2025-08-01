package com.aurelius.tech.mybakery.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown for business logic errors.
 */
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;

    /**
     * Constructs a new business exception with the specified detail message, error code, and HTTP status.
     *
     * @param message the detail message
     * @param errorCode the error code
     * @param status the HTTP status
     */
    public BusinessException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    /**
     * Constructs a new business exception with the specified detail message, error code, HTTP status, and cause.
     *
     * @param message the detail message
     * @param errorCode the error code
     * @param status the HTTP status
     * @param cause the cause
     */
    public BusinessException(String message, String errorCode, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.status = status;
    }

    /**
     * Returns the error code.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Returns the HTTP status.
     *
     * @return the HTTP status
     */
    public HttpStatus getStatus() {
        return status;
    }
}