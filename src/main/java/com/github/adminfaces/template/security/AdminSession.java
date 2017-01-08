package com.github.adminfaces.template.security;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Created by rafael-pestano on 07/01/17.
 *
 * Just control if user is logged in or not. You MUST be override isLoggedIn() (using CDI bean specialization)
 *
 * and use your security logic.
 *
 * You can also inject AdminSession in your security store and call sessionBean.setLoggedIn(true) after logon.
 *
 * By default user is NOT logged in
 */
@Named
@SessionScoped
public class AdminSession implements Serializable{

    private boolean loggedIn;

    @PostConstruct
    public void init(){
        loggedIn = false;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
