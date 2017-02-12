package com.github.adminfaces.template.session;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 * Created by rmpestano on 04/02/17
 * Controls if user is logged in so AdminFilter can send user to login page when it is not logged in.
 *
 * By default it is always logged in so to have this feature one must @Specializes this bean and put
 * the security logic in IsLoggedIn method.
 */
@SessionScoped
public class AdminSession implements Serializable {

    private boolean isLoggedIn = true;

    //avoid multiple redirects when redirecting user back to previous page after session expiration
    private boolean userRedirected = false;

    public boolean isLoggedIn(){
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public boolean isUserRedirected() {
        return userRedirected;
    }

    public void setUserRedirected(boolean userRedirected) {
        this.userRedirected = userRedirected;
    }
}
