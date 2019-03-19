package com.github.adminfaces.template.exception;

import com.github.adminfaces.template.util.Constants;
import com.github.adminfaces.template.util.WebXml;
import org.omnifaces.util.Exceptions;
import org.omnifaces.util.Messages;

import javax.ejb.EJBException;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.PhaseId;
import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.adminfaces.template.util.Assert.has;
import java.io.IOException;

/**
 * Based on:
 * https://github.com/conventions/core/blob/master/src/main/java/org/conventionsframework/exception/ConventionsExceptionHandler.java
 * This handler adds FacesMessages when BusinessExceptions are thrown OR sends user to error page when unexpected
 * exception are raised.
 *
 * @author rafael-pestano
 */
public class CustomExceptionHandler extends ExceptionHandlerWrapper {

    private static final Logger logger = Logger.getLogger(CustomExceptionHandler.class.getName());
    private ExceptionHandler wrapped;

    public CustomExceptionHandler(ExceptionHandler exceptionHandler) {
        this.wrapped = exceptionHandler;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

    @Override
    public void handle() throws FacesException {
        FacesContext context = FacesContext.getCurrentInstance();
        findErrorMessages(context);
        handleException(context);
        wrapped.handle();
    }

    /**
     * @param context
     * @throws Throwable
     */
    private void handleException(FacesContext context) {
        Iterator<ExceptionQueuedEvent> unhandledExceptionQueuedEvents = getUnhandledExceptionQueuedEvents().iterator();

        if (unhandledExceptionQueuedEvents.hasNext()) {
            Throwable exception = unhandledExceptionQueuedEvents.next().getContext().getException();
            unhandledExceptionQueuedEvents.remove();

            Throwable rootCause = Exceptions.unwrap(exception);

            if (rootCause instanceof BusinessException) {
                handleBusinessException(context, (BusinessException) rootCause);
                return;
            }

            //send user to error page when unexpected exceptions are raised
            goToErrorPage(context, rootCause);
        }

    }

    /**
     * @param context
     * @param e
     * @throws Throwable
     */
    private void goToErrorPage(FacesContext context, Throwable e) {
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            throw new FacesException(e);
        }

        if (e instanceof FileNotFoundException) {
            logger.log(Level.WARNING, "File not found", e);
            throw new FacesException(e);
        }

        ErrorMB errorMB = context.getApplication().evaluateExpressionGet(context, "#{errorMB}", ErrorMB.class);

        String requestedUri = request.getHeader("Referer");
        errorMB.setUserAgent(request.getHeader("user-agent"));
        errorMB.setRequestedUri(requestedUri);
        errorMB.setStacktrace(e);
        errorMB.setExceptionType(e != null ? e.getClass().getName() : null);
        errorMB.setErrorMessage(e != null ? e.getMessage() : "");
        String userIp = request.getHeader("x-forwarded-for") != null ? request.getHeader("x-forwarded-for").split(",")[0] : request.getRemoteAddr();
        errorMB.setUserIp(userIp);

        String errorPage = findErrorPage(e);
        if (!has(errorPage)) {
            String errorPageParam = context.getExternalContext().getInitParameter(Constants.InitialParams.ERROR_PAGE);
            if (!has(errorPageParam)) {
                errorPage = Constants.DEFAULT_ERROR_PAGE;
            }
        }
        try {
            context.getExternalContext().redirect(context.getExternalContext().getRequestContextPath() + errorPage);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Could not redirect user to error page: " + context.getExternalContext().getRequestContextPath() + errorPage, ex);
        }
    }

    /**
     * Find error page in web.xml
     *
     * @param exception
     * @return
     */
    private String findErrorPage(Throwable exception) {
        if (exception instanceof EJBException && exception.getCause() != null) {
            exception = exception.getCause();
        }
        String errorPage = WebXml.INSTANCE.findErrorPageLocation(exception);

        return errorPage;
    }

    /**
     * @param context
     * @param e application business exception
     */
    private void handleBusinessException(FacesContext context, BusinessException e) {
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            throw new FacesException(e);
        }
        if (has(e.getExceptionList())) {
            for (BusinessException be : e.getExceptionList()) {
                addFacesMessage(be);
            }
        } else { //Single exception
            addFacesMessage(e);
        }
        validationFailed();
        context.renderResponse();
    }

    private void addFacesMessage(BusinessException be) {
        FacesMessage facesMessage = new FacesMessage();
        if (has(be.getSummary())) {
            facesMessage.setSummary(be.getSummary());
        }
        if (has(be.getDetail())) {
            facesMessage.setDetail(be.getDetail());
        }
        if (has(be.getSeverity())) {
            facesMessage.setSeverity(be.getSeverity());
        } else {
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
        }
        Messages.add(be.getFieldId(), facesMessage);
    }

    /**
     * Set primefaces validationFailled callback param
     */
    private void validationFailed() {
        if (isRequestContextOnClasspath()) {
            org.primefaces.context.RequestContext context = org.primefaces.context.RequestContext.getCurrentInstance();
            if (context != null) {
                context.addCallbackParam("validationFailed", true);
            }
        } else {
            org.primefaces.PrimeFaces pf = org.primefaces.PrimeFaces.current();
            if (pf != null) {
                pf.ajax().addCallbackParam("validationFailed", true);
            }
        }
    }

    /**
     * Older versions of PrimeFaces (6.1) doesn't have new PrimeFaces.current() so we must use RequestContext
     *
     * @return
     */
    private boolean isRequestContextOnClasspath() {
        try {
            Class.forName("org.primefaces.context.RequestContext");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * If there is any faces message queued add PrimeFaces validation failed
     *
     * @param context
     */
    private void findErrorMessages(FacesContext context) {
        if (context.getMessageList().isEmpty() || context.isValidationFailed()) {
            return;
        }
        for (FacesMessage msg : context.getMessageList()) {
            if (msg.getSeverity().equals(FacesMessage.SEVERITY_ERROR) || msg.getSeverity().equals(FacesMessage.SEVERITY_FATAL)) {
                validationFailed();
                break;
            }
        }
    }

}
