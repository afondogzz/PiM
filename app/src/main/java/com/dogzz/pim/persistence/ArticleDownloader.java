package com.dogzz.pim.persistence;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;
import com.dogzz.pim.asynctask.DownloadTask;
import com.dogzz.pim.datahandlers.ArticleExtractor;
import com.dogzz.pim.datahandlers.HeadersList;
import com.dogzz.pim.dataobject.ArticleHeader;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static android.content.Context.MODE_PRIVATE;
import static android.provider.Telephony.Mms.Part.FILENAME;

/**
 * Class for download and save article offline
 * Created by dogzz on 15.07.2016.
 */
public class ArticleDownloader {
    private static final String LOG_TAG = "ArticleDownloader";
    private static final String FILE_EXT = ".html";
    protected Activity mainActivity;
    private String downloadResult;
    private SQLiteDatabase db;
    private DBHelper mDBHelper;
    private ArticleHeader header;

    public ArticleDownloader(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void saveArticleOffline(ArticleHeader header) {
        this.header = header;
        mDBHelper = new DBHelper(mainActivity, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        db = mDBHelper.getWritableDatabase();
        DownloadTask downloadTask = new DownloadArticleTask();
        downloadTask.execute(header.getArticleUrl());
    }



    public class DownloadArticleTask extends DownloadTask {

        @Override
        protected Integer doInBackground(String... urls) {
            Integer result;
            // params comes from the execute() call: params[0] is the BASE_URL.
            try {
                result = downloadUrl(urls[0]);
                result = saveArticleContent(result, resultMessage);

            } catch (IOException e) {
                downloadResult = "Error: Unable to retrieve source data. The source is inaccessible.";
                result = 0;
            }
            return result;
        }

        private int saveArticleContent(Integer result, String content) {
            if (result == 1) {
                try {
                    String pureArticle = extractArticle(content);
                    //save
                    String fileName = saveToFile(pureArticle);
                    saveResultToDB(fileName);
                    if (mDBHelper!=null) mDBHelper.close();
                    return 1;
                } catch (Exception e) {
                    resultMessage = "Something went wrong with loaded data. ".concat(e.getMessage());
                    return 0;
                }
            } else {
                return 0;
            }
        }

        private String saveToFile(String pureArticle) throws IOException{
            String fileName = header.getArticleUrl().replace(HeadersList.BASE_URL, "");
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf("."));
            writeFile(pureArticle, fileName);
            return fileName;
        }

        private void writeFile(String pureArticle, String fileName) throws IOException{
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                        mainActivity.openFileOutput(fileName.concat(FILE_EXT), MODE_PRIVATE)));
                bw.write(pureArticle);
                bw.close();
                Log.d(LOG_TAG, "File written");
        }

        private void saveResultToDB(String fileName) {
            header.markArticleAsSaved(db, fileName);
        }

        private String extractArticle(String downloadResult) {
            return ArticleExtractor.extractArticle(downloadResult);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Integer result) {
            downloadResult = resultMessage;
            if (result == 1) {
                Toast.makeText(mainActivity, "Download finished",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mainActivity, resultMessage,
                        Toast.LENGTH_LONG).show();
            }
//            saveArticleContent(result);
        }
    }
}
