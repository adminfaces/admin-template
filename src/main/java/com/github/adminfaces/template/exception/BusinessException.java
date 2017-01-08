package com.github.adminfaces.template.exception;

import javax.ejb.ApplicationException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Based on https://github.com/conventions/core/blob/master/src/main/java/org/conventionsframework/exception/BusinessException.java
 *
 * Application exception used to show faces messages when buisiness exception is thrown.
 *
 * @see CustomExceptionHandler#handleBusinessException(FacesContext, BusinessException)
 */
@ApplicationException(rollback = true)
public class BusinessException extends RuntimeException implements Serializable {

    private String summary;
    private String detail;
    private String fieldId;
    private FacesMessage.Severity severity;
    private List<BusinessException> exceptionList = new ArrayList<>();


    public BusinessException() {
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    /**
     *
     * @param summary exception summary
     */
    public BusinessException(String summary) {
        super(summary);
        this.summary = summary;
    }


    /**
     * @param summary exception summary
     * @param severity Faces message severity
     */
    public BusinessException(String summary, FacesMessage.Severity severity) {
        super(summary);
        this.summary = summary;
        this.severity = severity;
    }

    /**
     * @param summary exception summary
     * @param severity Faces message severity
     * @param idToFocus view component id to scroll to when exception occurs
     */
    public BusinessException(String summary, FacesMessage.Severity severity, String idToFocus) {
        super(summary);
        this.summary = summary;
        this.severity = severity;
        this.fieldId = idToFocus;
    }

    /**
     * @param summary exception summary
     * @param detail exception detail
     * @param idToFocus view component id to scroll to when exception occurs
     */
    public BusinessException(String summary, String detail, String idToFocus) {
        super(summary);
        this.detail = detail;
        this.summary = summary;
        this.fieldId = idToFocus;
    }

    /**
     * @param summary exception summary
     * @param detail exception detail
     * @param severity Faces message severity
     */
    public BusinessException(String summary, String detail, FacesMessage.Severity severity) {
        super(summary);
        this.detail = detail;
        this.summary = summary;
        this.severity = severity;
    }


    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public FacesMessage.Severity getSeverity() {
        if(severity == null){
            severity = FacesMessage.SEVERITY_ERROR;
        }
        return severity;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public void setSeverity(FacesMessage.Severity severity) {
        this.severity = severity;
    }

    public List<BusinessException> getExceptionList() {
        return exceptionList;
    }

    public void setExceptionList(List<BusinessException> exceptionList) {
        this.exceptionList = exceptionList;
    }

    public BusinessException addException(BusinessException be){
        exceptionList.add(be);
        return this;
    }
}
