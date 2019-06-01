package com.github.adminfaces.template.event;

import static com.github.adminfaces.template.util.Assert.has;

import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rmpestano on 28/04/17.
 */
public class AdminSystemEventListener implements SystemEventListener {

    private static final Logger log = Logger.getLogger(AdminSystemEventListener.class.getName());


    public boolean isListenerForSource(final Object source) {
        return true;
    }

    public void processEvent(final SystemEvent event) {
        try {
            ResourceBundle adminBundle = ResourceBundle.getBundle("admin");
            ResourceBundle adminPersistenceBundle = null;
            try {
                adminPersistenceBundle = ResourceBundle.getBundle("admin-persistence");
            } catch (MissingResourceException mre) {
                //intentional
            }
            boolean isLegacyTemplate = has(adminBundle.getString("admin.legacy")) && adminBundle.getString("admin.legacy").equals("true");
            StringBuilder sb = new StringBuilder("Using Admin Template ")
                    .append(adminBundle.getString("admin.version"))
                    .append(isLegacyTemplate ? " (legacy)" : "");
            if (has(adminPersistenceBundle)) {
                sb.append(", Admin Persistence ").append(adminPersistenceBundle.getString("admin-persistence.version"));
            }
            sb.append(" and Admin Theme ").append(ResourceBundle.getBundle("admin-theme").getString("theme.version"));
            log.log(Level.INFO, sb.toString());
        } catch (Exception e) {
            log.log(Level.WARNING, "Could not get AdminFaces version.", e);
        }
    }


}
