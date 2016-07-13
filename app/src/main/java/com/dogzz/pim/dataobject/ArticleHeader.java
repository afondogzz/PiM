package com.dogzz.pim.dataobject;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Data Object for storing article headers
 * Created by afon on 06.07.2016.
 */
public class ArticleHeader {
    private String articleUrl = "";
    private String articleImageUrl = "";
    private String title = "";
    private String subTitle = "";
    private long loadDate;
    private boolean isRead = false;
    private boolean isOffline = false;
    private String fileName = "";
    private int type;// 0 for articles and 1 for news


    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getArticleImageUrl() {
        return articleImageUrl;
    }

    public void setArticleImageUrl(String articleImageUrl) {
        this.articleImageUrl = articleImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void setOffline(boolean offline) {
        isOffline = offline;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public long getLoadDate() {
        return loadDate;
    }

    public void setLoadDate(long loadDate) {
        this.loadDate = loadDate;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String tempFileName) {
        this.fileName = tempFileName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
