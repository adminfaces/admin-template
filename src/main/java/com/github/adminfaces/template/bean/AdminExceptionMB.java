package com.github.adminfaces.template.bean;

import com.github.adminfaces.template.model.AdminException;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Created by rafael-pestano on 10/03/17.
 */
@Named
@SessionScoped
public class AdminExceptionMB implements Serializable {

    private AdminException exception;

    public void clear() {
       exception = new AdminException();
    }

    public void create(AdminException exception) {
        this.exception = exception;
    }

    @Produces
    @Named("adminErrorMessage")
    private String message() {
        return exception.getMessage();
    }

    @Produces
    @Named("adminException")
    private Throwable exception() {
        return exception.getException();
    }

    @Produces
    @Named("adminExceptionType")
    private String exceptionType() {
        return exception.getExceptionType();
    }

    @Produces
    @Named("adminRequestedUri")
    private String requestedUri() {
        return exception.getRequestedUri();
    }

    @Produces
    @Named("adminStatus")
    private String status() {
        return exception.getStatusCode();
    }
}
