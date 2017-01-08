package com.github.adminfaces.template.bean;

import com.github.adminfaces.template.util.Constants;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Created by rafael-pestano on 22/11/16.
 *
 */
@Named
@ApplicationScoped
public class AdminConfig implements Serializable {


    private String loginPage = Constants.DEFAULT_LOGIN_PAGE;
    private String indexPage = Constants.DEFAULT_INDEX_PAGE;
    private String errorPage = Constants.DEFAULT_ERROR_PAGE;
    private String dateFormat = Constants.DEFAULT_DATE_FORMAT;


    public String getLoginPage() {
        return loginPage;
    }

    public String getIndexPage() {
        return indexPage;
    }

    public String getErrorPage() {
        return errorPage;
    }


    public String getDateFormat() {
        return dateFormat;
    }


    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
    }

    public void setIndexPage(String indexPage) {
        this.indexPage = indexPage;
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

}
