package com.github.adminfaces.template.bean;

import com.github.adminfaces.template.config.AdminConfig;
import com.github.adminfaces.template.config.ControlSidebarConfig;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.omnifaces.util.Faces;

/**
 * Created by rmpestano on 07/05/17.
 */
@Named
@SessionScoped
public class LayoutMB implements Serializable {

    private static final Logger LOG = Logger.getLogger(LayoutMB.class.getName());
    private static final String DEFAULT_TEMPLATE = "/admin.xhtml"; //template bundled in admin-template 
    private static final String TEMPLATE_TOP = "/admin-top.xhtml"; //template bundled in admin-template 
    private static final String APP_TEMPLATE_PATH = "/templates/template.xhtml"; // application template (left menu)
    private static final String APP_TEMPLATE_TOP_PATH = "/templates/template-top.xhtml"; //application template (top menu)
    private static final String RESOURCES_PREFFIX = ""; // template resources preffix path
    private static final String WEBAPP_PREFFIX = "/WEB-INF"; // template webapp preffix path

    private String template;
	private String templatePath;
	private String templateTopPath;
    private Boolean leftMenuTemplate; 
    private Boolean fixedLayout;
    private Boolean boxedLayout;
    private Boolean expandOnHover;
    private Boolean sidebarCollapsed;
    private Boolean fixedControlSidebar;
    private Boolean darkControlSidebarSkin;

    @Inject
    AdminConfig adminConfig;
    
    @Inject
    SkinMB skinMB;

    @PostConstruct
    public void init() {
		this.templatePath = findTemplate(APP_TEMPLATE_PATH, DEFAULT_TEMPLATE);
		this.templateTopPath = findTemplate(APP_TEMPLATE_TOP_PATH, TEMPLATE_TOP);

		if (adminConfig.isLeftMenuTemplate()) {
            setDefaultTemplate();
        } else {
            setTemplateTop();
        }
        
        ControlSidebarConfig controlSidebarConfig = adminConfig.getControlSidebar();
        this.fixedLayout = controlSidebarConfig.getFixedLayout();
        this.boxedLayout = controlSidebarConfig.getBoxedLayout();
        this.expandOnHover = controlSidebarConfig.getExpandOnHover();
        this.sidebarCollapsed =  controlSidebarConfig.getSidebarCollapsed();
        this.fixedControlSidebar = controlSidebarConfig.getFixed();
        this.darkControlSidebarSkin = controlSidebarConfig.getDarkSkin();
    }
    
    public void restoreDefaults() {
    	skinMB.init();
    	this.init();
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public void setTemplateTop() {
		template = templateTopPath;
        leftMenuTemplate = false;
	}
	
    public void setDefaultTemplate() {
		template = templatePath;
        leftMenuTemplate = true;
    }

    public Boolean getLeftMenuTemplate() {
        return leftMenuTemplate;
    }

    public void setLeftMenuTemplate(Boolean leftMenuTemplate) {
        this.leftMenuTemplate = leftMenuTemplate;
    }

    public Boolean getFixedLayout() {
        return fixedLayout;
    }

    public void setFixedLayout(Boolean fixedLayout) {
        this.fixedLayout = fixedLayout;
    }

    public Boolean getBoxedLayout() {
        return boxedLayout;
    }

    public void setBoxedLayout(Boolean boxedLayout) {
        this.boxedLayout = boxedLayout;
    }

    public Boolean getExpandOnHover() {
        return expandOnHover;
    }

    public void setExpandOnHover(Boolean expandOnHover) {
        this.expandOnHover = expandOnHover;
    }

    public Boolean getSidebarCollapsed() {
        return sidebarCollapsed;
    }

    public void setSidebarCollapsed(Boolean sidebarCollapsed) {
        this.sidebarCollapsed = sidebarCollapsed;
    }

    public Boolean getFixedControlSidebar() {
        return fixedControlSidebar;
    }

    public void setFixedControlSidebar(Boolean fixedControlSidebar) {
        this.fixedControlSidebar = fixedControlSidebar;
    }

    public Boolean getDarkControlSidebarSkin() {
        return darkControlSidebarSkin;
    }

    public void setDarkControlSidebarSkin(Boolean darkControlSidebarSkin) {
        this.darkControlSidebarSkin = darkControlSidebarSkin;
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

	private boolean templateExists(String templateName) {
        try {
            return Faces.getExternalContext().getResourceAsStream(templateName) != null;
        } catch (Exception e) {
            LOG.warning(String.format("Could not find application defined template in path '%s' due to following error: %s. Falling back to default admin template. See application template documentation for more details: https://github.com/adminfaces/admin-template#application-template", APP_TEMPLATE_PATH, e.getMessage()));
            return false;
        }		
	}
	
    private String findTemplate(String appTemplatePath, String bundledPath) {
		String result;
		if (templateExists(WEBAPP_PREFFIX + appTemplatePath)) {
			result = WEBAPP_PREFFIX + appTemplatePath;
		} else if (templateExists(RESOURCES_PREFFIX + appTemplatePath)) {
			result = RESOURCES_PREFFIX + appTemplatePath;
		} else {
			result = bundledPath;
		}
        return result;
    }

}
