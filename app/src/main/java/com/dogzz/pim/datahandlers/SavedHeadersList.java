/*
* @Author: dogzz
* @Created: 7/15/2016
*/

package com.dogzz.pim.datahandlers;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;
import com.dogzz.pim.asynctask.DownloadTask;
import com.dogzz.pim.dataobject.ArticleHeader;
import com.dogzz.pim.exception.SourceConnectException;
import com.dogzz.pim.persistence.DBHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.dogzz.pim.persistence.DBHelper.*;

public class SavedHeadersList extends HeadersList {

    private static final String LOG_TAG = "SavedHeadersList";
    private SQLiteDatabase db;
    private DBHelper mDBHelper;
    private Cursor cursor;
    private String[] columns = null;
    private String selection = null;
    private String[] selectionArgs = null;
    private String orderBy = null;
    private String limit = null;

    public SavedHeadersList(RecyclerView recyclerView, FragmentActivity activity, ConnectivityManager connectivityManager) {
        super(recyclerView, activity, connectivityManager);
    }

    @Override
    protected void loadArticlesListFromSource() throws SourceConnectException {
        Log.d("DDDD", this.toString());
        LoadArticlesListTask loadTask = new LoadArticlesListTask();
        String startFrom = String.valueOf((currentPageNumber - 1)*12);
        mDBHelper = new DBHelper(mainActivity, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        db = mDBHelper.getWritableDatabase();
        loadTask.execute(startFrom);
    }

    @Override
    protected List<ArticleHeader> extractArticlesHeaders(String result, SQLiteDatabase db) {
        List<ArticleHeader> headers = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ArticleHeader header = new ArticleHeader();
                    header.setFileName(cursor.getString(cursor.getColumnIndex(COLUMN_FILENAME)));
                    header.setArticleUrl(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
                    header.setArticleImageUrl(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL)));
                    header.setSubTitle(cursor.getString(cursor.getColumnIndex(COLUMN_SUB_TITLE)));
                    header.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                    header.setOffline(cursor.getInt(cursor.getColumnIndex(COLUMN_OFFLINE)) == 1);
                    header.setRead(cursor.getInt(cursor.getColumnIndex(COLUMN_READ)) == 1);
                    header.setLoadDate(cursor.getInt(cursor.getColumnIndex(COLUMN_LOAD_DATE)));
                    header.setType(cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE)));
                    headers.add(header);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (mDBHelper != null) mDBHelper.close();
        return headers;
    }

    @Override
    public void loadArticlesHeaders(int pageNumber, boolean updateFromSource) {
        currentPageNumber = pageNumber;
        try {
            loadArticlesListFromSource();
        } catch (SourceConnectException e) {
            downloadResult = "Error: Unable to connect to the source. Check your internet settings.";
            Toast.makeText(mainActivity, downloadResult, Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, downloadResult);
        }
    }

   private class LoadArticlesListTask extends DownloadTask {

        @Override
        protected Integer doInBackground(String... startFrom) {
            Integer result;
            // params comes from the execute() call: params[0] is the BASE_URL.
            try {
                result = loadRecordsFromDB(startFrom[0]);
            } catch (Exception e) {
                downloadResult = "Error: Unable to retrieve source data. The source is inaccessible.";
                Log.e(LOG_TAG, downloadResult);
                result = 0;
            }
            return result;
        }

       private Integer loadRecordsFromDB(String startFrom) {
           try {
               selection = COLUMN_OFFLINE.concat(" = ?");
               selectionArgs = new String[]{"1"};
               orderBy = COLUMN_LOAD_DATE.concat(" DESC");
               limit = startFrom.concat(", 12");
               cursor = db.query(DB_TABLE, columns, selection, selectionArgs, null, null,
                       orderBy, limit);
               return 1;
           } catch (Exception e) {
               Log.e("Network", e.getMessage());
               resultMessage = e.getMessage();
               return 0;
           }
      }

       // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Integer result) {
            downloadResult = resultMessage;
            populateData(result);
        }
    }
}
