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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ResourceBundle;

import static com.github.adminfaces.template.util.Assert.has;

/**
 * Based on https://github.com/conventions/core/blob/master/src/main/java/org/conventionsframework/filter/ConventionsFilter.java
 * Created by rafael-pestano on 07/01/17.
 *
 * This filter controls when user must be redirected to logon or index page
 * and saves current url to redirect back when session expires
 */
@WebFilter(urlPatterns = {"/*"})
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
        log.info("Using AdminFaces " + ResourceBundle.getBundle("admin").getString("admin.version"));
        String disableAdminFilter = filterConfig.getServletContext().getInitParameter(Constants.InitialParams.DISABLE_FILTER);
        if (adminConfig.isDisableFilter() || has(disableAdminFilter) && Boolean.valueOf(disableAdminFilter)) {
            disableFilter = true;
        }
        if (!disableFilter) {
            try {
                loginPage = filterConfig.getServletContext().getInitParameter(Constants.InitialParams.LOGIN_PAGE);
                if (!has(loginPage)) {
                    loginPage = has(adminConfig) ? adminConfig.getLoginPage() : Constants.DEFAULT_LOGIN_PAGE;
                }
                errorPage = filterConfig.getServletContext().getInitParameter(Constants.InitialParams.ERROR_PAGE);
                if (!has(errorPage)) {
                    errorPage = has(adminConfig) ? adminConfig.getErrorPage() : Constants.DEFAULT_ERROR_PAGE;
                }
                indexPage = filterConfig.getServletContext().getInitParameter(Constants.InitialParams.INDEX_PAGE);
                if (!has(indexPage)) {
                    indexPage = has(adminConfig) ? adminConfig.getIndexPage() : Constants.DEFAULT_INDEX_PAGE;
                }

                //removes leading '/'
                errorPage = errorPage.startsWith("/") ? errorPage.substring(1) : errorPage;
                loginPage = loginPage.startsWith("/") ? loginPage.substring(1) : loginPage;
                indexPage = indexPage.startsWith("/") ? indexPage.substring(1) : indexPage;
            } catch (Exception e) {
                log.error("problem initializing admin filter", e);
            }
        }

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        if (disableFilter) {
            return;
        }
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;


        if (request.getRequestURI().equals(request.getContextPath() + "/")) {
            response.sendRedirect(request.getContextPath() + "/" + indexPage);
            return;
        }
        if (skipResource(request) || adminSession.isLoggedIn()) {
            if (!adminSession.isUserRedirected() && adminSession.isLoggedIn() && has(request.getHeader("Referer")) && request.getHeader("Referer").contains("?page=")) {
                adminSession.setUserRedirected(true);
                response.sendRedirect(request.getContextPath() + extractPageFromURL(request.getHeader("Referer")));
                return;
            }
            try {
                chain.doFilter(req, resp);
            } catch (FileNotFoundException e) {
                log.warn("File not found", e);
                response.sendError(404);
            }
        } else { //resource not skipped (e.g a page that is not logon page) AND user not logged in
            redirectToLogon(request, (HttpServletResponse) resp);
            return;
        }

    }

    private String extractPageFromURL(String referer) {
        String page = referer.substring(referer.indexOf("page=") + 5);
        try {
            return URLDecoder.decode(page, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.warn("Could not extract page from url", e);
            return indexPage;
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * skips faces-resources, index or logon pages
     *
     * @param request
     * @return true if resource must be skipped by the filter false otherwise
     */
    private boolean skipResource(HttpServletRequest request) {
        String path = request.getServletPath().replaceAll("/", "");
        //log.warning("path to skip:"+path);
        boolean skip = path.startsWith(FACES_RESOURCES) || path.equalsIgnoreCase(loginPage) || path.equalsIgnoreCase(errorPage);
        //log.warning("skip result:"+skip);
        return skip;
    }

    private void redirectToLogon(HttpServletRequest request, HttpServletResponse response) {
        try {
            String referer = request.getHeader("Referer");
            String recoveryUrlParams = "";
            //get request parameters
            if (has(referer) && referer.contains("?")) {
                recoveryUrlParams = referer.substring(referer.lastIndexOf("?") + 1);
            } else {
                recoveryUrlParams = request.getQueryString();
            }
            //saves page where user were
            String requestedPage = request.getRequestURI();
            StringBuilder recoveryUrl = null;
            if (!loginPage.equals(requestedPage) && requestedPage.contains(".")) {
                if (requestedPage.contains(request.getContextPath())) {
                    requestedPage = requestedPage.replace(request.getContextPath(), "");
                }
                recoveryUrl = new StringBuilder(requestedPage);
                if (has(recoveryUrlParams)) {
                    recoveryUrl.append("?").append(recoveryUrlParams);
                }
            }
            /*
             if saved page is not null and is not index page then send user to logon page and save
            / previous page in url param named 'page'
            */
            String redirectUrl = request.getContextPath() + "/" + loginPage + (has(recoveryUrl) &&
                    !recoveryUrl.toString().contains(indexPage) ? "?page=" + URLEncoder.encode(recoveryUrl.toString(), "UTF-8") : "");
            if ("partial/ajax".equals(request.getHeader("Faces-Request"))) {
                //redirect on ajax request: //http://stackoverflow.com/questions/13366936/jsf-filter-not-redirecting-after-initial-redirect
                response.setContentType("text/xml");
                response.getWriter()
                        .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                        .printf("<partial-response><redirect url=\"%s\"></redirect></partial-response>", redirectUrl);
            } else {//normal redirect
                response.sendRedirect(redirectUrl);
            }

        } catch (Exception e) {
            log.error("Could not redirect to " + loginPage, e);
        }

    }

}