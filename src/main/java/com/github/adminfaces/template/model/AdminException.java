package com.github.adminfaces.template.model;

/**
 * Created by rafael-pestano on 10/03/17.
 */
public class AdminException {

    private String message;
    private Throwable exception;
    private String exceptionType;
    private String requestedUri;
    private String statusCode;

    public AdminException() {
    }

    public AdminException(Throwable exception, String requestedUri, String statusCode) {
        this.message = exception.getMessage();
        this.exception = exception;
        this.exceptionType = exception.getClass().getName();
        this.requestedUri = requestedUri;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getException() {
        return exception;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public String getRequestedUri() {
        return requestedUri;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
