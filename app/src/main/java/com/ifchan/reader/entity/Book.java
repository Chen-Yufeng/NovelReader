package com.ifchan.reader.entity;

import java.io.Serializable;

/**
 * Created by daily on 11/23/17.
 */

public class Book implements Serializable {
    private String id;
    private String title;
    private String author;
    private String shortIntro;
    private String cover;
    private String coverPath;
    private String site;
    private int banned;
    private int latelyFollower;
    private String retentionRatio;
    private String majorCate;

    public Book(String id, String title, String author, String shortIntro, String cover, String
            site, int banned, int latelyFollower, String retentionRatio, String majorCate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.shortIntro = shortIntro;
        this.cover = cover;
        this.site = site;
        this.banned = banned;
        this.latelyFollower = latelyFollower;
        this.retentionRatio = retentionRatio;
        this.majorCate = majorCate;
    }

    public Book(String id, String title, String author, String shortIntro, String cover, String
            coverPath, String site, int latelyFollower, String retentionRatio, String majorCate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.shortIntro = shortIntro;
        this.cover = cover;
        this.coverPath = coverPath;
        this.site = site;
        this.latelyFollower = latelyFollower;
        this.retentionRatio = retentionRatio;
        this.majorCate = majorCate;
    }

    public Book() {
    }

    public String getMajorCate() {
        return majorCate;
    }

    public void setMajorCate(String majorCate) {
        this.majorCate = majorCate;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getShortIntro() {
        return shortIntro;
    }

    public void setShortIntro(String shortIntro) {
        this.shortIntro = shortIntro;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getBanned() {
        return banned;
    }

    public void setBanned(int banned) {
        this.banned = banned;
    }

    public int getLatelyFollower() {
        return latelyFollower;
    }

    public void setLatelyFollower(int latelyFollower) {
        this.latelyFollower = latelyFollower;
    }

    public String getRetentionRatio() {
        return retentionRatio;
    }

    public void setRetentionRatio(String retentionRatio) {
        this.retentionRatio = retentionRatio;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", shortIntro='" + shortIntro + '\'' +
                ", cover='" + cover + '\'' +
                ", site='" + site + '\'' +
                ", banned=" + banned +
                ", latelyFollower=" + latelyFollower +
                ", retentionRatio='" + retentionRatio + '\'' +
                '}';
    }
}
