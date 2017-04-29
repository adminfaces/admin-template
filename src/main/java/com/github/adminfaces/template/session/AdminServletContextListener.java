package com.github.adminfaces.template.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.ResourceBundle;

/**
 * Created by rmpestano on 28/04/17.
 */
public class AdminServletContextListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(AdminServletContextListener.class.getCanonicalName());


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        log.info("Using Admin Template " + ResourceBundle.getBundle("admin").getString("admin.version"));
        log.info("Using Admin Theme " + ResourceBundle.getBundle("admin-theme").getString("theme.version"));
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
