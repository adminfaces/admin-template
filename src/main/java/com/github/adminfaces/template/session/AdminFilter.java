package com.github.adminfaces.template.session;

import static com.github.adminfaces.template.util.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.adminfaces.template.config.AdminConfig;
import com.github.adminfaces.template.util.Constants;

/**
 * Based on https://github.com/conventions/core/blob/master/src/main/java/org/conventionsframework/filter/ConventionsFilter.java
 * Created by rafael-pestano on 07/01/17.
 *
 * This filter controls when user must be redirected to logon or index page
 * and saves current url to redirect back when session expires
 */
@WebFilter(urlPatterns = {"/*"})
public class AdminFilter implements Filter {

    private static final String FACES_RESOURCES = "/javax.faces.resource";
    private static final Logger log = Logger.getLogger(AdminFilter.class.getName());

    private boolean disableFilter;
    private String loginPage;
    private String indexPage;
    private String redirectPrefix;

    @Inject
    AdminSession adminSession;

    @Inject
    AdminConfig adminConfig;

    private final List<String> ignoredResources = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) {
        if(adminConfig == null) {
            initBeans();
        }
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
                String errorPage = filterConfig.getServletContext().getInitParameter(Constants.InitialParams.ERROR_PAGE);
                if (!has(errorPage)) {
                    errorPage = Constants.DEFAULT_ERROR_PAGE;
                }
                indexPage = filterConfig.getServletContext().getInitParameter(Constants.InitialParams.INDEX_PAGE);
                if (!has(indexPage)) {
                    indexPage = has(adminConfig) ? adminConfig.getIndexPage() : Constants.DEFAULT_INDEX_PAGE;
                }

                //removes leading '/'
                errorPage = errorPage.startsWith("/") ? errorPage.substring(1) : errorPage;
                loginPage = loginPage.startsWith("/") ? loginPage.substring(1) : loginPage;
                indexPage = indexPage.startsWith("/") ? indexPage.substring(1) : indexPage;

                ignoredResources.add("/" + loginPage.substring(0, loginPage.lastIndexOf(".")));//we need leading slash for ignoredResources
                ignoredResources.add("/" + errorPage.substring(0, errorPage.lastIndexOf(".")));

                String configuredResouces = adminConfig.getIgnoredResources();
                if (has(configuredResouces)) {
                    this.ignoredResources.addAll(Arrays.asList(configuredResouces.split(",")));
                    for (String ignoredResource : ignoredResources) {
                        if (!ignoredResource.startsWith("/")) { //we need leading slash for ignoredResources beucase getServletPath (in this#skipResource) returns a string with leading slash
                            ignoredResources.set(ignoredResources.indexOf(ignoredResource), "/" + ignoredResource);
                        }
                    }
                }

            } catch (Exception e) {
                log.log(Level.SEVERE, "problem initializing admin filter", e);
            }
        }

    }

    /**
     * Workaround for open web beans on tomcat, see https://stackoverflow.com/questions/45205472/apache-openwebbeanscdi-servlet-injection-not-working
     */
    private void initBeans() {
        try {
            Class.forName("javax.enterprise.inject.spi.CDI");
            adminConfig = CDI.current().select(AdminConfig.class).get();
            adminSession = CDI.current().select(AdminSession.class).get();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not initialize beans via lookup.", e);
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        if (disableFilter) {
            chain.doFilter(req, resp);
            return;
        }
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;


        if (request.getRequestURI().equals(request.getContextPath() + "/")
                || (adminSession.isLoggedIn() && request.getRequestURI().endsWith(loginPage))) {
            response.sendRedirect(getRedirectPrefix(request) + request.getContextPath() + "/" + indexPage);
            return;
        }

        if (request.getRequestURI().contains(request.getContextPath() + "/public/")) {
            chain.doFilter(req, resp);
            return;
        }

        if (skipResource(request, response) || adminSession.isLoggedIn()) {
            if (!adminSession.isUserRedirected() && adminSession.isLoggedIn() && has(request.getHeader("Referer")) && request.getHeader("Referer").contains("?page=")) {
                adminSession.setUserRedirected(true);
                String pageFromURL = request.getContextPath() + extractPageFromURL(request.getHeader("Referer"));
                log.info("Redirecting user back to " + pageFromURL);
                response.sendRedirect(getRedirectPrefix(request) + pageFromURL);
                return;
            }
            try {
                chain.doFilter(req, resp);
            } catch (FileNotFoundException e) {
                log.log(Level.WARNING, "File not found", e);
                response.sendError(404);
            }
        } else { //resource not skipped (e.g a page that is not logon page) AND user not logged in
            redirectToLogon(request, (HttpServletResponse) resp);
            return;
        }

    }

	private String extractPageFromURL(String referer) {
		try {
			URL url = new URL(referer);
			String[] params = url.getQuery().split("&");
			for (String param : params) {
				String[] split = param.split("=");
				if ("page".equals(split[0])) {
					return URLDecoder.decode(split[1], "UTF-8");
				}
			}
		} catch (MalformedURLException | UnsupportedEncodingException e) {
            log.log(Level.WARNING, "Could not extract page from url", e);
		}
		return indexPage;
	}

    @Override
    public void destroy() {

    }

    /**
     * skips faces-resources, index, error or logon pages
     *
     * @param request
     * @return true if resource must be skipped by the filter false otherwise
     */
    private boolean skipResource(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getServletPath();
        if (path.contains(".")) {
            path = path.substring(0, path.lastIndexOf("."));
        }
        boolean skip = path.startsWith(FACES_RESOURCES) || shouldIgnoreResource(path) || response.getStatus() == HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        return skip;
    }

    private void redirectToLogon(HttpServletRequest request, HttpServletResponse response) {
        try {
            String referer = request.getHeader("Referer");
            String recoveryUrlParams;
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
                if (requestedPage.startsWith(request.getContextPath())) {
                    requestedPage = requestedPage.replaceFirst(request.getContextPath(), "");
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
                    isValidRecoveryUrl(recoveryUrl) ? "?page=" + URLEncoder.encode(recoveryUrl.toString(), "UTF-8") : "");
            if ("partial/ajax".equals(request.getHeader("Faces-Request"))) {
                //redirect on ajax request: //http://stackoverflow.com/questions/13366936/jsf-filter-not-redirecting-after-initial-redirect
                response.setContentType("text/xml");
                response.getWriter()
                        .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                        .printf("<partial-response><redirect url=\"%s\"></redirect></partial-response>", redirectUrl);
            } else {//normal redirect
                response.sendRedirect(getRedirectPrefix(request) + redirectUrl);
            }

        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not redirect to " + loginPage, e);
        }

    }

    /**
     * Skip error pages, login and index page as recovery url because it doesn't make sense redirecting user to such pages
     *
     * @param recoveryUrl
     * @return
     */
    private boolean isValidRecoveryUrl(StringBuilder recoveryUrl) {
        String pageSuffix = adminConfig.getPageSufix();
        return !recoveryUrl.toString().contains(Constants.DEFAULT_INDEX_PAGE.replace("xhtml", pageSuffix)) && !recoveryUrl.toString().contains(Constants.DEFAULT_ACCESS_DENIED_PAGE.replace("xhtml", adminConfig.getPageSufix()))
                && !recoveryUrl.toString().contains(Constants.DEFAULT_EXPIRED_PAGE.replace("xhtml", pageSuffix)) && !recoveryUrl.toString().contains(Constants.DEFAULT_OPTIMISTIC_PAGE.replace("xhtml", adminConfig.getPageSufix()))
                && !recoveryUrl.toString().contains(Constants.DEFAULT_LOGIN_PAGE.replace("xhtml", adminConfig.getPageSufix()));
    }

    /**
     * @param path
     * @return true if requested path starts with a ignored resource (configured in admin-config.properties)
     */
    private boolean shouldIgnoreResource(String path) {
        for (String ignoredResource : ignoredResources) {
            if (path.startsWith(ignoredResource)) {
                return true;
            }
        }
        return false;
    }

    private String getRedirectPrefix(HttpServletRequest request) {
        if(redirectPrefix == null) {
            String url = request.getRequestURL().toString();
            String uri = request.getRequestURI();
            int offset = url.indexOf(uri);
            redirectPrefix = url.substring(0, offset);
            if(useHttps(request)) {
                log.log(Level.WARNING,"Changing request scheme to https.");
                redirectPrefix = redirectPrefix.replace("http:","https:");
            }
        }
        return redirectPrefix;
    }

    private static boolean useHttps(HttpServletRequest request) {
        String protocolProperty = System.getProperty("admin.protocol", System.getenv("admin.protocol"));

        String protoHeader = request.getHeader("X-Forwarded-Proto");
        return request.isSecure() || (protoHeader != null && protoHeader.toLowerCase().equals("https"))
            || (protocolProperty != null && protocolProperty.toLowerCase().equals("https"));
    }
}
