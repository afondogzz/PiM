package com.dogzz.pim.uihandlers;

/*
* @Author: dogzz
* @Created: 7/13/2016
*/

import com.dogzz.pim.R;

public enum NavigationItem {
    ARTICLES (0, R.string.articles, R.id.nav_articles),
    NEWS (1, R.string.news, R.id.nav_news),
    SAVED (2, R.string.saved, R.id.nav_saved),
    SETTINGS (3, R.string.settings, R.id.nav_settings),
    FEEDBACK (4, R.string.feedback, -1),
    ABOUT (5, R.string.about, R.id.nav_about);

    int itemNo;
    int stringId;
    int id;

    NavigationItem(int itemNo, int stringId, int id) {
        this.itemNo = itemNo;
        this.stringId = stringId;
        this.id = id;
    }

    public int getItemNo() {
        return itemNo;
    }

    public int getStringId() {
        return stringId;
    }

    public int getId() {
        return id;
    }

     public static NavigationItem fromNumber(int itemNo) {
        for (NavigationItem item : NavigationItem.values()) {
            if (item.getItemNo() == itemNo) return item;
        }
         return ARTICLES;
     }
}
