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


    public boolean isLoggedIn(){
        return true;
    }

}
