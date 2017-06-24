package com.github.adminfaces.template.exception;

import javax.ejb.ApplicationException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.github.adminfaces.template.util.Assert.has;

/**
 * Based on https://github.com/conventions/core/blob/master/src/main/java/org/conventionsframework/exception/BusinessException.java
 *
 * Application exception used to show faces messages when business exception is thrown.
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
     * @param detail exception detail
     */
    public BusinessException(String detail) {
        super(detail);
        this.detail = detail;
    }
    
    /**
     * @param summary exception summary
     * @param detail exception detail
     */
    public BusinessException(String summary, String detail) {
        super(summary);
        this.detail = detail;
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

    public BusinessException setDetail(String detail) {
        this.detail = detail;
        return this;
    }

    public String getSummary() {
        return summary;
    }

    public BusinessException setSummary(String summary) {
        this.summary = summary;
        return this;
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

    public BusinessException setFieldId(String fieldId) {
        this.fieldId = fieldId;
        return this;
    }

    public BusinessException setSeverity(FacesMessage.Severity severity) {
        this.severity = severity;
        return this;
    }

    public List<BusinessException> getExceptionList() {
        return exceptionList;
    }

    public BusinessException setExceptionList(List<BusinessException> exceptionList) {
        this.exceptionList = exceptionList;
        return this;
    }

    public BusinessException addException(BusinessException be){
        exceptionList.add(be);
        return this;
    }

    public void build() {
        if(has(summary) || has(detail) || has(exceptionList)) {
            throw this;
        }
    }
}
