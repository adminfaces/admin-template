package com.github.adminfaces.template.security;

import com.github.adminfaces.template.config.AdminConfig;
import com.github.adminfaces.template.util.Constants;
import org.omnifaces.util.Faces;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;
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
