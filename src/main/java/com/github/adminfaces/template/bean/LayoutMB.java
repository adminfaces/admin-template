package com.github.adminfaces.template.bean;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Logger;
import org.omnifaces.util.Faces;

/**
 * Created by rmpestano on 07/05/17.
 */
@Named
@SessionScoped
public class LayoutMB implements Serializable {

    private static final Logger LOG = Logger.getLogger(LayoutMB.class.getName());
    private static final String DEFAULT_TEMPLATE = "/admin.xhtml"; //template bundled in admin-template 
    private static final String APP_TEMPLATE_PATH = "/WEB-INF/templates/template.xhtml"; // application template (left menu)
    private static final String APP_TEMPLATE_TOP_PATH = "/WEB-INF/templates/template-top.xhtml"; //application template (top menu)

    private String template;
    private Boolean appTemplateExists;

    private boolean defaultTemplateSelected;

    @PostConstruct
    public void init() {
        setDefaultTemplate();
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplateTop() {
         if (appTemplateExists()) {
            template = APP_TEMPLATE_TOP_PATH;
        } else {
            template = DEFAULT_TEMPLATE;
        }
    }

    public void setDefaultTemplate() {
        if (appTemplateExists()) {
            template = APP_TEMPLATE_PATH;
        } else {
            template = DEFAULT_TEMPLATE;
        }
    }

    public void toggleTemplate() {
        if (isDefaultTemplate()) {
            setTemplateTop();
        } else {
            setDefaultTemplate();
        }
    }

    public boolean isDefaultTemplate() {
        return template != null && (template.endsWith("template.xhtml") || template.equals("admin.xhtml"));
    }

    public boolean isDefaultTemplateSelected() {
        return isDefaultTemplate();
    }

    public void setDefaultTemplateSelected(boolean defaultTemplateSelected) {
        this.defaultTemplateSelected = defaultTemplateSelected;
    }

    private boolean appTemplateExists() {
        if (appTemplateExists != null) {
            return appTemplateExists;
        }
        try {
            appTemplateExists = Faces.getExternalContext().getResourceAsStream(APP_TEMPLATE_PATH) != null;
        } catch (Exception e) {
            LOG.warning("Could not find application defined template (), failing back to default admin template.");
            appTemplateExists = false;
        }

        return appTemplateExists;
    }

}
