/*
* @Author: dogzz
* @Created: 7/15/2016
*/

package com.dogzz.pim.datahandlers;

import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import com.dogzz.pim.dataobject.ArticleHeader;
import com.dogzz.pim.exception.SourceConnectException;

import java.util.List;

public class SavedHeadersList extends HeadersList {
    public SavedHeadersList(RecyclerView recyclerView, FragmentActivity activity, ConnectivityManager connectivityManager) {
        super(recyclerView, activity, connectivityManager);
    }

    @Override
    protected void loadArticlesListFromSource() throws SourceConnectException {

    }

    @Override
    protected List<ArticleHeader> extractArticlesHeaders(String result, SQLiteDatabase db) {
        return null;
    }
}
