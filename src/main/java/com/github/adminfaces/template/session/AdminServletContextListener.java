package com.github.adminfaces.template.session;


import static com.github.adminfaces.template.util.Assert.has;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rmpestano on 28/04/17.
 */
public class AdminServletContextListener implements ServletContextListener {
    private static final Logger log = Logger.getLogger(AdminServletContextListener.class.getName());


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            ResourceBundle adminBundle = ResourceBundle.getBundle("admin");
            ResourceBundle adminPersistenceBundle = null;
            try {
                adminPersistenceBundle = ResourceBundle.getBundle("admin-persistence");
            }catch (MissingResourceException mre) {
                //intentional
            }
            boolean isLegacyTemplate = has(adminBundle.getString("admin.legacy")) && adminBundle.getString("admin.legacy").equals("true");
            StringBuilder sb = new StringBuilder("Using Admin Template ")
                    .append(ResourceBundle.getBundle("admin").getString("admin.version"))
                    .append(isLegacyTemplate ? " (legacy)":"")
                    .append(" and Admin Theme ").append(ResourceBundle.getBundle("admin-theme").getString("theme.version"));         
            if(has(adminPersistenceBundle)) {
                sb.append(" and Admin Persistence ").append(adminPersistenceBundle.getString("admin-persistence.version"));
            }
            log.log(Level.INFO,sb.toString());
        }catch (Exception e) {
            log.log(Level.WARNING,"Could not get AdminFaces version.",e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
