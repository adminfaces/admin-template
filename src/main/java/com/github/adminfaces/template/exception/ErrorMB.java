/*
 * The MIT License
 *
 * Copyright 2019 rmpestano.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.adminfaces.template.exception;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
 *
 * @author rmpestano
 */
@Named("errorMB")
@SessionScoped
public class ErrorMB implements Serializable {

    private String exceptionType;
    private String errorMessage;
    private Throwable stacktrace;
    private String userAgent;
    private String requestedUri;
    private String userIp;

    @Produces
    @Named("errorMessage")
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Produces
    @Named("exceptionType")
    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Produces
    @Named("stacktrace")
    public Throwable getStacktrace() {
        return stacktrace;
    }

    @Produces
    @Named("userAgent")
    public String getUserAgent() {
        return userAgent;
    }

    @Produces
    @Named("userIp")
    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public void setStacktrace(Throwable stacktrace) {
        this.stacktrace = stacktrace;
    }

    @Produces
    @Named("requestedUri")
    public String getRequestedUri() {
        return requestedUri;
    }

    public void setRequestedUri(String requestedUri) {
        this.requestedUri = requestedUri;
    }

    
    
    
}