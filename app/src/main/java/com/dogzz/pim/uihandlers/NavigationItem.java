package com.dogzz.pim.uihandlers;

/*
* @Author: dogzz
* @Created: 7/13/2016
*/

public enum NavigationItem {
    ARTICLES (0),
    NEWS (1),
    SAVED (2);

    int itemNo;

    NavigationItem(int itemNo) {
        this.itemNo = itemNo;
    }

    public int getItemNo() {
        return itemNo;
    }

     public static NavigationItem fromNumber(int itemNo) {
        for (NavigationItem item : NavigationItem.values()) {
            if (item.getItemNo() == itemNo) return item;
        }
         return ARTICLES;
     }
}
