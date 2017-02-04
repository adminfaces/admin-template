package com.github.adminfaces.template.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import static com.github.adminfaces.template.util.Assert.has;

/**
 * Created by rafael-pestano on 22/11/16.
 */
@Named
@ApplicationScoped
public class AdminConfig implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(AdminConfig.class);

    private Properties adminConfigFile;//default config
    private Properties userConfigFile;//user defined properties
    private String loginPage;
    private String indexPage;
    private String errorPage;
    private String dateFormat;
    private String templatePath;
    private Integer breadCrumbMaxSize;
    private boolean renderMessages;
    private boolean disableFilter;


    @PostConstruct
    public void init() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        adminConfigFile = new Properties();
        userConfigFile = new Properties();
        try (InputStream is = cl.getResourceAsStream(("admin-config.properties"))) {
            userConfigFile.load(is);
        } catch (Exception ex) {
            log.error("Could not load user defined admin template properties.", ex);
        }

        try (InputStream isDefault = cl.getResourceAsStream(("config/admin-config.properties"))) {
            adminConfigFile.load(isDefault);
        } catch (Exception ex) {
            log.error("Could not load admin template default properties.", ex);
        }

        loadDefaults();
    }

    protected void loadDefaults() {
        loginPage = getProperty("admin.loginPage");
        errorPage = getProperty("admin.errorPage");
        indexPage = getProperty("admin.indexPage");
        dateFormat = getProperty("admin.dateFormat");
        templatePath = getProperty("admin.templatePath");
        breadCrumbMaxSize = Integer.parseInt(getProperty("admin.breadcrumbSize"));
        renderMessages = Boolean.parseBoolean(getProperty("admin.renderMessages"));
        disableFilter = Boolean.parseBoolean(getProperty("admin.disableFilter"));
    }

    private String getProperty(String property) {
        return has(userConfigFile.getProperty(property)) ? userConfigFile.getProperty(property) : adminConfigFile.getProperty(property);
    }


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

    public boolean isDisableFilter() {
        return disableFilter;
    }

    public void setDisableFilter(boolean disableFilter) {
        this.disableFilter = disableFilter;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public Integer getBreadCrumbMaxSize() {
        return breadCrumbMaxSize;
    }

    public void setBreadCrumbMaxSize(Integer breadCrumbMaxSize) {
        this.breadCrumbMaxSize = breadCrumbMaxSize;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public boolean isRenderMessages() {
        return renderMessages;
    }

    public void setRenderMessages(boolean renderMessages) {
        this.renderMessages = renderMessages;
    }
}
