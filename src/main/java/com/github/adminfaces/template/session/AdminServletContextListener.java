package com.github.adminfaces.template.session;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.util.logging.Logger;

/**
 * Created by rmpestano on 28/04/17.
 */
public class AdminServletContextListener implements ServletContextListener {

    private static final Logger log = Logger.getLogger(AdminServletContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //only here for backyard compatibility
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
