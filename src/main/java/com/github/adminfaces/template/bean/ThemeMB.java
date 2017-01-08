package com.github.adminfaces.template.bean;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Created by rmpestano on 07/01/17.
 */
@Named
@SessionScoped
public class ThemeMB implements Serializable{

    private String theme = "skin-blue";


    public void changeTheme(String theme){
        this.theme = theme;
    }


    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
