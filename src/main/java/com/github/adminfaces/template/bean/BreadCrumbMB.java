package com.github.adminfaces.template.bean;

import com.github.adminfaces.template.model.BreadCrumb;
import org.omnifaces.util.Faces;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.github.adminfaces.template.util.Assert.has;

/**
 * Created by rafael-pestano on 30/11/16.
 */
@SessionScoped
public class BreadCrumbMB implements Serializable {

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


    public void add(BreadCrumb breadCrumb){
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
    }


    @Produces
    @Named("breadCrumbs")
    public List<BreadCrumb> getBreadCrumbs() {
        return breadCrumbs;
    }

    public void setBreadCrumbs(List<BreadCrumb> breadCrumbs) {
        this.breadCrumbs = breadCrumbs;
    }

}
