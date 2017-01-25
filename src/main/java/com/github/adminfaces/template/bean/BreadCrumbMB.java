package com.github.adminfaces.template.bean;

import com.github.adminfaces.template.config.AdminConfig;
import com.github.adminfaces.template.model.BreadCrumb;
import com.github.adminfaces.template.util.Constants;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.github.adminfaces.template.util.Assert.has;

/**
 * Created by rafael-pestano on 30/11/16.
 */
@SessionScoped
@Named
public class BreadCrumbMB implements Serializable {

    private ThreadLocal<Boolean> hasCleared = new ThreadLocal<>();

    @Inject
    protected AdminConfig adminConfig;


    private int maxSize = 5;

    private List<BreadCrumb> breadCrumbs = new ArrayList<>();

    @PostConstruct
    public void init(){
          maxSize = adminConfig.getBreadCrumbMaxSize();
    }

    public void add(String link, String title, Boolean clear){
        if(clear != null && clear){
            breadCrumbs.clear();
        }
        add(new BreadCrumb(link,title));
    }

    public void add(BreadCrumb breadCrumb){
        if(hasCleared.get() != null) {
            //when clicking on home breadcrumb it calls add two times
            hasCleared.remove();
            return;
        }

        if(!has(breadCrumb.getLink())){
            breadCrumb.setLink(FacesContext.getCurrentInstance().getViewRoot().getViewId());
        }

        if(breadCrumb.getLink() != null && !breadCrumb.getLink().contains(".")){
            breadCrumb.setLink(breadCrumb.getLink()+"."+ Constants.DEFAULT_PAGE_FORMAT);
        }
        if(breadCrumbs.contains(breadCrumb)){
            breadCrumbs.remove(breadCrumb);
        }

        if(breadCrumbs.size() == maxSize) {
            breadCrumbs.remove(0);
        }
        breadCrumbs.add(breadCrumb);
    }

    public void remove(BreadCrumb breadCrumb){
        breadCrumbs.remove(breadCrumb);
    }

    public void clear(){
        breadCrumbs.clear();
        hasCleared.set(true);
    }


    public List<BreadCrumb> getBreadCrumbs() {
        return breadCrumbs;
    }

    public void setBreadCrumbs(List<BreadCrumb> breadCrumbs) {
        this.breadCrumbs = breadCrumbs;
    }

}
