package com.github.adminfaces.template.security;

import com.github.adminfaces.template.config.AdminConfig;
import com.github.adminfaces.template.util.Constants;
import org.omnifaces.util.Faces;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.IOException;

/**
 * Created by rmpestano on 03/02/17.
 */
@Named
@RequestScoped
public class LogoutMB {

    @Inject
    AdminConfig adminConfig;


    public void doLogout() throws IOException {
        String loginPage = adminConfig.getLoginPage();
        if (loginPage == null || "".equals(loginPage)) {
            loginPage = Constants.DEFAULT_LOGIN_PAGE;
        }
        if (!loginPage.startsWith("/")) {
            loginPage = "/" + loginPage;
        }
        Faces.getSession().invalidate();
        ExternalContext ec = Faces.getExternalContext();
        ec.redirect(ec.getRequestContextPath() + loginPage);
    }

}
