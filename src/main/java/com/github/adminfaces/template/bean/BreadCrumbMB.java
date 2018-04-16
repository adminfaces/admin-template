package com.github.adminfaces.template.bean;

import com.github.adminfaces.template.config.AdminConfig;
import com.github.adminfaces.template.model.BreadCrumb;

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
    public void init() {
        maxSize = adminConfig.getBreadCrumbMaxSize();
    }

    public void add(String link, String title, Boolean clear) {
        if (clear != null && clear) {
            breadCrumbs.clear();
        }
        if (title != null && !title.isEmpty()) {
            add(new BreadCrumb(link, title));
        }
    }

    public void add(String link, String title, Boolean clear, Boolean shouldAdd) {
        if (shouldAdd != null && shouldAdd) {
            this.add(link, title, clear);
        }
    }

    public void add(BreadCrumb breadCrumb) {
        if (hasCleared.get() != null) {
            //when clicking on home breadcrumb it calls add two times
            hasCleared.remove();
            return;
        }

        String link = breadCrumb.getLink();
        if (!has(link)) {
            String pageUrl = FacesContext.getCurrentInstance().getViewRoot().getViewId();
            link = pageUrl.replaceAll(pageUrl.substring(pageUrl.lastIndexOf('.') + 1), adminConfig.getPageSufix());
        }

        if(link != null && adminConfig.isExtensionLessUrls()) {
            link = link.substring(0, link.lastIndexOf("."));
        } else if(link != null && !link.contains(".")) {
            link = link + adminConfig.getPageSufix();
        }
        breadCrumb.setLink(link);

        if (breadCrumbs.contains(breadCrumb)) {
            breadCrumbs.remove(breadCrumb);
        }

        if (breadCrumbs.size() == maxSize) {
            breadCrumbs.remove(0);
        }
        breadCrumbs.add(breadCrumb);
    }

    public void remove(BreadCrumb breadCrumb) {
        breadCrumbs.remove(breadCrumb);
    }

    public void clear() {
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
