package com.github.adminfaces.template.bean;

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
        String indexPage = adminConfig.getIndexPage();
        if (indexPage == null || "".equals(indexPage)) {
            indexPage = Constants.DEFAULT_INDEX_PAGE;
        }
        if (!indexPage.startsWith("/")) {
            indexPage = "/" + indexPage;
        }
        Faces.getSession().invalidate();
        ExternalContext ec = Faces.getExternalContext();
        ec.redirect(ec.getRequestContextPath() + indexPage);
    }

}
