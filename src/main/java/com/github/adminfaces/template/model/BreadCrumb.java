package com.github.adminfaces.template.model;

import java.io.Serializable;

/**
 * Created by rafael-pestano on 30/11/16.
 */
public class BreadCrumb implements Serializable {

    private String link;
    private String title;

    public BreadCrumb(String link, String title) {
        this.link = link;
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BreadCrumb that = (BreadCrumb) o;

        return !(link != null ? !link.equals(that.link) : that.link != null);

    }

    @Override
    public int hashCode() {
        return link != null ? link.hashCode() : 0;
    }
}
