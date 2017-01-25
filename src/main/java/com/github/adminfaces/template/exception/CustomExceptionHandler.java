package com.github.adminfaces.template.exception;

import static com.github.adminfaces.template.util.Assert.has;
import static javax.servlet.RequestDispatcher.ERROR_EXCEPTION;
import static javax.servlet.RequestDispatcher.ERROR_EXCEPTION_TYPE;
import static javax.servlet.RequestDispatcher.ERROR_MESSAGE;
import static javax.servlet.RequestDispatcher.ERROR_REQUEST_URI;
import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;

import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.ejb.EJBException;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.PhaseId;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.adminfaces.template.config.AdminConfig;
import com.github.adminfaces.template.util.Constants;
import org.omnifaces.config.WebXml;
import org.omnifaces.util.Exceptions;
import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Based on: https://github.com/conventions/core/blob/master/src/main/java/org/conventionsframework/exception/ConventionsExceptionHandler.java
 * This handler adds FacesMessages when BusinessExceptions are thrown
 * OR sends user to error page when unexpected exception are raised.
 *
 * @author rafael-pestano
 */
public class CustomExceptionHandler extends ExceptionHandlerWrapper {

    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class.getCanonicalName());
    private ExceptionHandler wrapped;

    public CustomExceptionHandler(ExceptionHandler exceptionHandler) {
        this.wrapped = exceptionHandler;
    }

    @Inject
    AdminConfig adminConfig;

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

        final HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        if (request.getAttribute("logoff") != null && request.getAttribute("logoff").equals("true")) {
            redirectToLogon(request, context);
        }

    }

    private void redirectToLogon(HttpServletRequest request, FacesContext context) {
        ExternalContext externalContext = context.getExternalContext();
        String logonPage = externalContext.getInitParameter(Constants.InitialParams.LOGIN_PAGE);
        if (!has(logonPage)) {
            logonPage = adminConfig != null ? adminConfig.getLoginPage() : Constants.DEFAULT_LOGIN_PAGE;
        }
        if (!logonPage.startsWith("/")) {
            logonPage = "/" + logonPage;
        }
        try {
            String referer = request.getHeader("Referer");
            String recoveryUrlParams = "";
            if (has(referer)) {
                if (referer.contains("?")) {
                    recoveryUrlParams = referer.substring(referer.lastIndexOf("?") + 1);
                }
            } else { //try to get params from queryString which was put by AdminFilter
                recoveryUrlParams = (String) request.getAttribute("queryString");
            }
            StringBuilder recoveryUrl = new StringBuilder(context.getViewRoot().getViewId());
            if (!"".equals(recoveryUrlParams)) {
                recoveryUrl.append("?").append(recoveryUrlParams);
            }
            context.getExternalContext().redirect(externalContext.getRequestContextPath() + logonPage + "?page=" + URLEncoder.encode(recoveryUrl.toString(), "UTF-8"));

        } catch (Exception e) {
            logger.error("Could not redirect to " + logonPage, e);
        }
    }

    /**
     * @param context
     * @param e
     * @throws Throwable
     */
    private void goToErrorPage(FacesContext context, Throwable e) {
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            throw new FacesException(e);
        }

        if (e instanceof FileNotFoundException) {
            logger.warn("File not found", e);
            throw new FacesException(e);
        }

        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        request.setAttribute(ERROR_EXCEPTION + "_stacktrace", e);
        request.setAttribute(ERROR_EXCEPTION_TYPE, e.getClass());
        request.setAttribute(ERROR_MESSAGE, e.getMessage());
        request.setAttribute(ERROR_REQUEST_URI, request.getHeader("Referer"));
        request.setAttribute(ERROR_STATUS_CODE, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        String errorPage = findErrorPage(e);
        if (!has(errorPage)) {
            String errorPageParam = context.getExternalContext().getInitParameter(Constants.InitialParams.ERROR_PAGE);
            if (!has(errorPageParam)) {
                errorPage = Constants.DEFAULT_ERROR_PAGE;
            }
        }
        context.getApplication().getNavigationHandler().handleNavigation(context, null, errorPage);
        context.renderResponse();
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
     * @param e       application business exception
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
        RequestContext rc = RequestContext.getCurrentInstance();
        if (rc != null) {
            rc.addCallbackParam("validationFailed", true);
        }
    }


    /**
     * If there is any faces message queued add primefaces validation failed
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
