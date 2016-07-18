package com.dogzz.pim.uihandlers;

/*
* @Author: dogzz
* @Created: 7/13/2016
*/

import com.dogzz.pim.R;

public enum NavigationItem {
    ARTICLES (0, R.string.articles),
    NEWS (1, R.string.news),
    SAVED (2, R.string.saved),
    SETTINGS (3, R.string.settings),
    FEEDBACK (4, R.string.feedback),
    ABOUT (5, R.string.about),
    CONTENT (6, -1);

    int itemNo;
    int stringId;

    NavigationItem(int itemNo, int stringId) {
        this.itemNo = itemNo;
        this.stringId = stringId;
    }

    public int getItemNo() {
        return itemNo;
    }

    public int getStringId() {
        return stringId;
    }

     public static NavigationItem fromNumber(int itemNo) {
        for (NavigationItem item : NavigationItem.values()) {
            if (item.getItemNo() == itemNo) return item;
        }
         return ARTICLES;
     }
}
