package com.github.adminfaces.template.bean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by rmpestano on 07/01/17.
 *
 * Handles layout date format (used in exception.xhtml)
 * To override it just extend this bean using @Specializes and ovverride getDateFormat()
 */
@Named
@ApplicationScoped
public class DateFormatMB {


    private String dateFormat;

    @Inject
    AdminConfig adminConfig;


    public String getDateFormat() {
        if(dateFormat == null){
            dateFormat = adminConfig.getDateFormat();
        }
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
