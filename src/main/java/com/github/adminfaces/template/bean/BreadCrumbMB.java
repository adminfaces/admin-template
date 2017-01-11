package com.github.adminfaces.template.bean;

import com.github.adminfaces.template.model.BreadCrumb;
import com.github.adminfaces.template.util.Constants;
import org.omnifaces.util.Faces;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
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

    private ThreadLocal<Boolean> hasClear = new ThreadLocal<>();

    private int maxSize = 5;

    private List<BreadCrumb> breadCrumbs = new ArrayList<>();

    @PostConstruct
    public void init(){
        String breadCrumbSizeParam = Faces.getInitParameter("com.github.adminfaces.BREAD_CRUMB_SIZE");
        if(has(breadCrumbSizeParam)) {
            try{
                maxSize = Integer.parseInt(breadCrumbSizeParam);
            }catch (NumberFormatException nfe){
                //no-op
            }
        }
    }

    public void add(String link, String title){
        add(new BreadCrumb(link,title));
    }

    public void add(BreadCrumb breadCrumb){
        if(hasClear.get() != null) {
            hasClear.remove();
            return;
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
        hasClear.set(true);
    }


    public List<BreadCrumb> getBreadCrumbs() {
        return breadCrumbs;
    }

    public void setBreadCrumbs(List<BreadCrumb> breadCrumbs) {
        this.breadCrumbs = breadCrumbs;
    }

}
