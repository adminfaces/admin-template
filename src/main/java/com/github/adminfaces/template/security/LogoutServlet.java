package com.github.adminfaces.template.security;

import com.github.adminfaces.template.config.AdminConfig;
import com.github.adminfaces.template.util.Constants;
import java.io.IOException;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.omnifaces.util.Faces;

@WebServlet(name = "adminLogoutServlet", urlPatterns = "/admin-logout")
public class LogoutServlet extends HttpServlet {

    @Inject
    AdminConfig adminConfig;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Faces.getSession().invalidate();
        ExternalContext ec = Faces.getExternalContext();
        ec.redirect(ec.getRequestContextPath() + getLoginPage());
    }

    private String getLoginPage() {
        String loginPage = adminConfig.getLoginPage();
        if (loginPage == null || "".equals(loginPage)) {
            loginPage = Constants.DEFAULT_LOGIN_PAGE;
        }
        if (!loginPage.startsWith("/")) {
            loginPage = "/" + loginPage;
        }
        return loginPage;
    }
}