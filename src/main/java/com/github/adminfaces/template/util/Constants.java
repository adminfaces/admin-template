package com.github.adminfaces.template.util;

/**
 * Created by rmpestano on 07/01/17.
 */
public interface Constants {


    String DEFAULT_INDEX_PAGE = "index.xhtml";
    String DEFAULT_LOGIN_PAGE = "login.xhtml";
    String DEFAULT_ERROR_PAGE = "500.xhtml";
    String DEFAULT_ACCESS_DENIED_PAGE = "403.xhtml";
    String DEFAULT_EXPIRED_PAGE = "expired.xhtml";
    String DEFAULT_OPTIMISTIC_PAGE = "optimistic.xhtml";
    String DEFAULT_DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
    String DEFAULT_PAGE_FORMAT = "xhtml";

    interface InitialParams {
        String DISABLE_FILTER = "com.github.adminfaces.DISABLE_FILTER";
        String LOGIN_PAGE = "com.github.adminfaces.LOGIN_PAGE";
        String ERROR_PAGE = "com.github.adminfaces.ERROR_PAGE";
        String INDEX_PAGE = "com.github.adminfaces.INDEX_PAGE";
    }
}
