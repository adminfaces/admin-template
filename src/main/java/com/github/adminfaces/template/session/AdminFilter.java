package com.github.adminfaces.template.session;

import com.github.adminfaces.template.config.AdminConfig;
import com.github.adminfaces.template.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ResourceBundle;

import static com.github.adminfaces.template.util.Assert.has;

/**
 * Based on https://github.com/conventions/core/blob/master/src/main/java/org/conventionsframework/filter/ConventionsFilter.java
 * Created by rafael-pestano on 07/01/17.
 *
 * This filter controls when user must be redirected to logon
 * and saves current url to redirect back when session expires
 *
 */
@WebFilter(urlPatterns={"/*"})
public class AdminFilter implements Filter {

    private static final String FACES_RESOURCES = "javax.faces.resource";
    private static final Logger log = LoggerFactory.getLogger(AdminFilter.class.getCanonicalName());


    private boolean disableFilter;
    private String loginPage;
    private String errorPage;
    private String indexPage;

    @Inject
    AdminSession adminSession;

    @Inject
    AdminConfig adminConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Using AdminFaces "+ ResourceBundle.getBundle("admin").getString("admin.version"));
        String disableAdminFilter = filterConfig.getServletContext().getInitParameter(Constants.InitialParams.DISABLE_FILTER);
        if(adminConfig.isDisableFilter() || has(disableAdminFilter) && Boolean.valueOf(disableAdminFilter)) {
            disableFilter = true;
        }
        if(!disableFilter){
            try{
                loginPage =  filterConfig.getServletContext().getInitParameter(Constants.InitialParams.LOGIN_PAGE);
                if(!has(loginPage)){
                     loginPage = has(adminConfig) ? adminConfig.getLoginPage():Constants.DEFAULT_LOGIN_PAGE;
                }
                errorPage =  filterConfig.getServletContext().getInitParameter(Constants.InitialParams.ERROR_PAGE);
                if(!has(errorPage)){
                    errorPage = has(adminConfig) ? adminConfig.getErrorPage():Constants.DEFAULT_ERROR_PAGE;
                }
                indexPage =  filterConfig.getServletContext().getInitParameter(Constants.InitialParams.INDEX_PAGE);
                if(!has(indexPage)){
                    indexPage = has(adminConfig) ? adminConfig.getIndexPage():Constants.DEFAULT_INDEX_PAGE;
                }

                //removes leading '/'
                errorPage =  errorPage.startsWith("/") ? errorPage.substring(1):errorPage;
                loginPage = loginPage.startsWith("/") ? loginPage.substring(1):loginPage;
                indexPage = indexPage.startsWith("/") ? indexPage.substring(1):indexPage;
            }catch (Exception e) {
                log.error("problem initializing admin filter",e);
            }
        }

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        if(disableFilter){
            return;
        }
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        HttpServletRequest request = (HttpServletRequest) req;
        if (skipResource(request) || adminSession.isLoggedIn()) {
            try {
                chain.doFilter(req, resp);
            } catch (FileNotFoundException e) {
                ((HttpServletResponse)resp).sendError(404);
            }
        } else { //resource not skipped AND user not logged in
            request.setAttribute("logoff", "true");//let CustomExceptionHandler redirect to logon when user is not logged in
            request.setAttribute("queryString", request.getQueryString());
            chain.doFilter(req,resp);
        }

    }

    @Override
    public void destroy() {

    }

    /**
     * skips faces-resources, index or logon pages
     * @param request
     * @return true if resource must be skipped by the filter false otherwise
     */
    private boolean skipResource(HttpServletRequest request) {
        String path = request.getServletPath().replaceAll("/", "");
        //log.warning("path to skip:"+path);
        boolean skip =  path.startsWith(FACES_RESOURCES) || path.equalsIgnoreCase(loginPage) || path.equalsIgnoreCase(indexPage) || path.equalsIgnoreCase(errorPage);
        //log.warning("skip result:"+skip);
        return skip;
    }

}