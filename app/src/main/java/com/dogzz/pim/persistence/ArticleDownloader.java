package com.dogzz.pim.persistence;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import com.dogzz.pim.asynctask.DownloadTask;
import com.dogzz.pim.dataobject.ArticleHeader;

import java.io.IOException;

/**
 * Class for download and save article offline
 * Created by dogzz on 15.07.2016.
 */
public class ArticleDownloader {
    protected Activity mainActivity;
    private String downloadResult;
    private SQLiteDatabase db;

    public ArticleDownloader(Activity mainActivity, SQLiteDatabase db) {
        this.db = db;
        this.mainActivity = mainActivity;
    }

    public void saveArticleOffline(ArticleHeader header) {
        DownloadTask downloadTask = new DownloadArticleTask();
        downloadTask.execute(header.getArticleUrl());
    }

    private void saveArticleContent(Integer result) {
        if (result == 1) {
            try {
                String pureArticle = extractArticle(downloadResult);
            } catch (Exception e) {
                Toast.makeText(mainActivity, "Something went wrong with loaded data. ".concat(e.getMessage()),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mainActivity, downloadResult, Toast.LENGTH_SHORT).show();
        }
    }

    private String extractArticle(String downloadResult) {
        return null;
    }

    public class DownloadArticleTask extends DownloadTask {

        @Override
        protected Integer doInBackground(String... urls) {
            Integer result;
            // params comes from the execute() call: params[0] is the BASE_URL.
            try {
                result = downloadUrl(urls[0]);
            } catch (IOException e) {
                downloadResult = "Error: Unable to retrieve source data. The source is inaccessible.";
                result = 0;
            }
            return result;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Integer result) {
            downloadResult = resultMessage;
            saveArticleContent(result);
        }
    }
}
