package com.ifchan.reader.entity;

/**
 * Created by daily on 12/1/17.
 */

public class Index {
    private String title;
    private String link;

    public Index() {
    }

    public Index(String title, String link) {
        this.title = title;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
