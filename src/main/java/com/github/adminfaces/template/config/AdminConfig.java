package com.github.adminfaces.template.config;


import static com.github.adminfaces.template.util.Assert.has;
import com.github.adminfaces.template.util.Constants;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Holds global application configuration 
 *
 * Created by rafael-pestano on 22/11/16.
 */
@Named
@ApplicationScoped
public class AdminConfig implements Serializable {

    private static final long serialVersionUID = 834212776758014169L;
    private static final Logger log = Logger.getLogger(AdminConfig.class.getName());

    private Properties adminConfigFile;//default config
    private Properties userConfigFile;//user defined properties
    private String loginPage;
    private String indexPage;
    private String dateFormat;
    private String templatePath;
    private Integer breadCrumbMaxSize;
    private boolean renderMessages;
    private boolean renderAjaxStatus;
    private boolean disableFilter;
    private boolean enableRipple;
    private boolean renderBreadCrumb;
    private boolean extensionLessUrls;
    private boolean enableSlideMenu;
    private String rippleElements;
    private String skin;
    private boolean autoShowNavbar;
    private String ignoredResources;//comma separated resources (pages or urls) to be ignored in AdminFilter
    private String loadingImage;
    private boolean renderControlSidebar;
    private boolean leftMenuTemplate;
    private boolean renderMenuSearch;
    private boolean renderFormAsterisks;
    private boolean closableLoading;
    private boolean enableMobileHeader;
    //controlsidebar
    private ControlSidebarConfig controlSidebar;
    private String pageSuffix;
    private boolean rippleMobileOnly;
    private String messagesHideTimeout;
    private boolean autoHideMessages;
    private boolean iconsEffect;

    @PostConstruct
    public void init() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        adminConfigFile = new Properties();
        userConfigFile = new Properties();
        try (InputStream is = cl.getResourceAsStream(("admin-config.properties"))) {
            if (is != null) {
                userConfigFile.load(is);
            }
        } catch (IOException ex) {
            log.log(Level.WARNING,"Could not load user defined admin template properties. Falling back to default properties.");
        }

        try (InputStream isDefault = cl.getResourceAsStream(("config/admin-config.properties"))) {
            adminConfigFile.load(isDefault);
        } catch (IOException ex) {
            log.log(Level.SEVERE,"Could not load admin template default properties.", ex);
        }

        loadDefaults();
    }

    protected void loadDefaults() {
        loginPage = getProperty("admin.loginPage");
        indexPage = getProperty("admin.indexPage");
        dateFormat = getProperty("admin.dateFormat");
        if(!has(dateFormat)) {
            dateFormat =  ((SimpleDateFormat)DateFormat.getDateTimeInstance()).toLocalizedPattern();
        }
        templatePath = getProperty("admin.templatePath");
        breadCrumbMaxSize = Integer.parseInt(getProperty("admin.breadcrumbSize"));
        renderMessages = Boolean.parseBoolean(getProperty("admin.renderMessages"));
        renderAjaxStatus = Boolean.parseBoolean(getProperty("admin.renderAjaxStatus"));
        disableFilter = Boolean.parseBoolean(getProperty("admin.disableFilter"));
        enableRipple = Boolean.parseBoolean(getProperty("admin.enableRipple"));
        renderBreadCrumb = Boolean.parseBoolean(getProperty("admin.renderBreadCrumb"));
        extensionLessUrls = Boolean.parseBoolean(getProperty("admin.extensionLessUrls"));
        rippleElements = getProperty("admin.rippleElements");
        enableSlideMenu =  Boolean.parseBoolean(getProperty("admin.enableSlideMenu"));
        skin = getProperty("admin.skin");
        autoShowNavbar = Boolean.parseBoolean(getProperty("admin.autoShowNavbar"));
        autoHideMessages = Boolean.parseBoolean(getProperty("admin.autoHideMessages"));
        iconsEffect = Boolean.parseBoolean(getProperty("admin.iconsEffect"));
        ignoredResources =  getProperty("admin.ignoredResources");
        loadingImage =  getProperty("admin.loadingImage");
        renderControlSidebar =  Boolean.parseBoolean(getProperty("admin.renderControlSidebar"));
        rippleMobileOnly = Boolean.parseBoolean(getProperty("admin.rippleMobileOnly"));
        renderMenuSearch = Boolean.parseBoolean(getProperty("admin.renderMenuSearch"));
        renderFormAsterisks = Boolean.parseBoolean(getProperty("admin.renderFormAsterisks"));
        enableMobileHeader = Boolean.parseBoolean(getProperty("admin.enableMobileHeader"));
        closableLoading = Boolean.parseBoolean(getProperty("admin.closableLoading"));
        messagesHideTimeout = getProperty("admin.messagesHideTimeout");
        leftMenuTemplate = Boolean.parseBoolean(getProperty("admin.controlSidebar.leftMenuTemplate"));
        boolean controlSidebarShowOnMobile = Boolean.parseBoolean(getProperty("admin.controlSidebar.showOnMobile"));
        boolean fixedLayout = Boolean.parseBoolean(getProperty("admin.controlSidebar.fixedLayout"));
        boolean boxedLayout = Boolean.parseBoolean(getProperty("admin.controlSidebar.boxedLayout"));
        boolean expandOnHover = Boolean.parseBoolean(getProperty("admin.controlSidebar.expandOnHover"));
        boolean sidebarCollapsed = Boolean.parseBoolean(getProperty("admin.controlSidebar.sidebarCollapsed"));
        boolean fixedControlSidebar = Boolean.parseBoolean(getProperty("admin.controlSidebar.fixed"));
        boolean darkControlSidebarSkin = Boolean.parseBoolean(getProperty("admin.controlSidebar.darkSkin"));
        controlSidebar = new ControlSidebarConfig(controlSidebarShowOnMobile,fixedLayout, boxedLayout, expandOnHover, sidebarCollapsed, fixedControlSidebar, darkControlSidebarSkin);
    }

    /**
     * First tries to load the property from java system properties
     * secondly looks for the property into user defined admin-config.properties then if
     * not found load defaults from admin-config.properties provided within admin-template
     *
     * @param property name
     * @return the property value
     */
    private String getProperty(String property) {
        return has(System.getProperty(property)) ? System.getProperty(property)
                : has(userConfigFile.getProperty(property)) ? userConfigFile.getProperty(property)
                : adminConfigFile.getProperty(property);
    }

    /**
     * infer page suffix from index and login page configured in admin-config.properties
     *
     * If none is configured then use default suffix: 'xhtml'.
     */
    public String getPageSufix() {
        if(has(pageSuffix)) {
            return pageSuffix;
        }
        if(!has(indexPage) && !has(loginPage)) {
            pageSuffix = Constants.DEFAULT_PAGE_FORMAT;
        }
        if(has(indexPage)) {
            pageSuffix = indexPage.substring(indexPage.lastIndexOf('.')+1);
        } else {
            pageSuffix = indexPage.substring(loginPage.lastIndexOf('.')+1);
        }
        return pageSuffix;
    }

    public void restoreDefaults() {
        loadDefaults();
    }


    public String getLoginPage() {
        return loginPage;
    }

    public String getIndexPage() {
        return indexPage;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public boolean isIconsEffect() {
        return iconsEffect;
    }

    public void setIconsEffect(boolean iconsEffect) {
        this.iconsEffect = iconsEffect;
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

    public boolean isDisableFilter() {
        return disableFilter;
    }

    public void setDisableFilter(boolean disableFilter) {
        this.disableFilter = disableFilter;
    }

    public boolean isLeftMenuTemplate() {
        return leftMenuTemplate;
    }

    public boolean isRenderMenuSearch() {
        return renderMenuSearch;
    }

    public void setRenderMenuSearch(boolean renderMenuSearch) {
        this.renderMenuSearch = renderMenuSearch;
    }

    public boolean isAutoHideMessages() {
        return autoHideMessages;
    }

    public void setAutoHideMessages(boolean autoHideMessages) {
        this.autoHideMessages = autoHideMessages;
    }

    public boolean isRenderFormAsterisks() {
        return renderFormAsterisks;
    }

    public void setRenderFormAsterisks(boolean renderFormAsterisks) {
        this.renderFormAsterisks = renderFormAsterisks;
    }

    public String getMessagesHideTimeout() {
        return messagesHideTimeout;
    }

    public void setMessagesHideTimeout(String messagesHideTimeout) {
        this.messagesHideTimeout = messagesHideTimeout;
    }

    public void setLeftMenuTemplate(boolean leftMenuTemplate) {
        this.leftMenuTemplate = leftMenuTemplate;
    }

    public ControlSidebarConfig getControlSidebar() {
        return controlSidebar;
    }

    public void setControlSidebar(ControlSidebarConfig controlSidebarConfig) {
        this.controlSidebar = controlSidebarConfig;
    }

    public boolean isEnableMobileHeader() {
        return enableMobileHeader;
    }

    public void setEnableMobileHeader(boolean enableMobileHeader) {
        this.enableMobileHeader = enableMobileHeader;
    }

    public boolean isRippleMobileOnly() {
        return rippleMobileOnly;
    }

    public void setRippleMobileOnly(boolean rippleEffectMobileOnly) {
        this.rippleMobileOnly = rippleEffectMobileOnly;
    }

    @Deprecated
    /**
     * @deprecated use LayoutMB#template
     */
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

    public boolean isRenderAjaxStatus() {
        return renderAjaxStatus;
    }

    public void setRenderAjaxStatus(boolean renderAjaxStatus) {
        this.renderAjaxStatus = renderAjaxStatus;
    }

    public boolean isEnableRipple() {
        return enableRipple;
    }

    public void setEnableRipple(boolean enableRipple) {
        this.enableRipple = enableRipple;
    }

    public boolean isRenderBreadCrumb() {
        return renderBreadCrumb;
    }

    public void setRenderBreadCrumb(boolean renderBreadCrumb) {
        this.renderBreadCrumb = renderBreadCrumb;
    }

    public boolean isExtensionLessUrls() {
        return extensionLessUrls;
    }

    public void setExtensionLessUrls(boolean extensionLessUrls) {
        this.extensionLessUrls = extensionLessUrls;
    }

    public String getRippleElements() {
        return rippleElements;
    }

    public void setRippleElements(String rippleElements) {
        this.rippleElements = rippleElements;
    }

    public boolean isEnableSlideMenu() {
        return enableSlideMenu;
    }

    public void setEnableSlideMenu(boolean enableSlideMenu) {
        this.enableSlideMenu = enableSlideMenu;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public boolean isAutoShowNavbar() {
        return autoShowNavbar;
    }

    public void setAutoShowNavbar(boolean autoShowNavbar) {
        this.autoShowNavbar = autoShowNavbar;
    }

    public String getIgnoredResources() {
        return ignoredResources;
    }

    public void setIgnoredResources(String ignoredResources) {
        this.ignoredResources = ignoredResources;
    }

    public String getLoadingImage() {
        return loadingImage;
    }

    public void setLoadingImage(String loadingImage) {
        this.loadingImage = loadingImage;
    }

    public boolean isRenderControlSidebar() {
        return renderControlSidebar;
    }

    public void setRenderControlSidebar(boolean renderControlSidebar) {
        this.renderControlSidebar = renderControlSidebar;
    }

    public boolean isClosableLoading() {
        return closableLoading;
    }

    public void setClosableLoading(boolean closableLoading) {
        this.closableLoading = closableLoading;
    }
}