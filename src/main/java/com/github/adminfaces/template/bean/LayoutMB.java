package com.github.adminfaces.template.bean;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Created by rmpestano on 07/05/17.
 */
@Named
@SessionScoped
public class LayoutMB implements Serializable {

    private String template;

    private boolean defaultTemplateSelected;

    @PostConstruct
    public void init() {
        setDefaultTemplate();
    }
    
    public String getTemplate() {
        return template;
    }

    public void setTemplateTop() {
        template = "/WEB-INF/templates/template-top.xhtml";
    }

    public void setDefaultTemplate() {
        template = "/WEB-INF/templates/template.xhtml";
    }

    public void toggleTemplate() {
        if(isDefaultTemplate()) {
            setTemplateTop();
        } else {
            setDefaultTemplate();
        }
    }

    public boolean isDefaultTemplate() {
        return template != null && template.endsWith("template.xhtml");
    }

    public boolean isDefaultTemplateSelected() {
        return isDefaultTemplate();
    }

    public void setDefaultTemplateSelected(boolean defaultTemplateSelected) {
        this.defaultTemplateSelected = defaultTemplateSelected;
    }
    
}
