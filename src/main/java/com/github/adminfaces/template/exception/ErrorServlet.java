package com.github.adminfaces.template.exception;

import com.github.adminfaces.template.config.AdminConfig;
import com.github.adminfaces.template.session.AdminFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by rmpestano on 04/03/17.
 */
@WebServlet(urlPatterns = "/adminError")
public class ErrorServlet extends HttpServlet {

    @Inject
    AdminConfig adminConfig;

    private static final Logger log = LoggerFactory.getLogger(AdminFilter.class.getCanonicalName());


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String errorCode = req.getAttribute("javax.servlet.error.status_code") != null ? req.getAttribute("javax.servlet.error.status_code").toString() : "";
        if ("500".equals(errorCode)) {
            redirect(req, resp, adminConfig.getErrorPage());
        } else if ("404".equals(errorCode)) {
            redirect(req, resp, adminConfig.getNotFoundPage());
        } else if ("403".equals(errorCode)) {
            redirect(req, resp, adminConfig.getAccessDeniedPage());
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response, String page) {
        try {
            String contextPath = request.getContextPath() + "/";
            if ("partial/ajax".equals(request.getHeader("Faces-Request"))) {
                //redirect on ajax request: //http://stackoverflow.com/questions/13366936/jsf-filter-not-redirecting-after-initial-redirect
                response.setContentType("text/xml");
                response.getWriter()
                        .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                        .printf("<partial-response><redirect url=\"%s\"></redirect></partial-response>", contextPath + page);
            } else {
                response.sendRedirect(contextPath + page);
            }
        } catch (IOException e) {
            log.error("Could not redirect to page " + page, e);
        }
    }
}
