package com.dogzz.pim.datahandlers;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;
import com.dogzz.pim.database.DBHelper;
import com.dogzz.pim.dataobject.ArticleHeader;
import com.dogzz.pim.exception.SourceConnectException;
import com.dogzz.pim.uihandlers.MyRecyclerAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * Class responsible for loading list of articles from source site and getting text data to view
 */
public abstract class HeadersList implements Serializable{

    protected String downloadResult = "";
    protected List<ArticleHeader> articlesHeaders = new ArrayList<>();
    protected int currentPageNumber = 0;
    public static final String BASE_URL = "http://petrimazepa.com";
    public static final String PATH_URL = "/ajax/articles/%d/12";
    public static final int DAY_IN_MILLISECONDS = 86400000;
    protected RecyclerView recyclerView;
    protected MyRecyclerAdapter adapter;
    protected Activity mainActivity;
    protected ConnectivityManager connectivityManager;
//    public static final String TMP_FILE_PATH = "/temp";
//    public static final String SAVED_FILE_PATH = "/saved";

    public HeadersList(String contentAsString) {

    }

    public HeadersList() {

    }

    public HeadersList(RecyclerView recyclerView, Activity activity, ConnectivityManager connectivityManager) {
        this.recyclerView = recyclerView;
        this.mainActivity = activity;
        this.connectivityManager = connectivityManager;
    }

    public void loadArticlesHeaders(int pageNumber, boolean updateFromSource) {
        if (updateFromSource || articlesHeaders.isEmpty()) {
            try {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    currentPageNumber = pageNumber;
                    loadArticlesListFromSource();
                } else {
                    downloadResult = "Error: The network is not available. You can read Offline articles.";
                }
            } catch (SourceConnectException e) {
                downloadResult = "Error: Unable to connect to the source. Check your internet settings.";
            }
        }
    }

    protected abstract void loadArticlesListFromSource() throws SourceConnectException;

    public void loadNextPage(boolean updateFromSource) {
        currentPageNumber++;
        loadArticlesHeaders(currentPageNumber, updateFromSource);
    }

    private void populateData(Integer result) {
        if (result == 1) {
            try {
                if (adapter == null) {
                    adapter = new MyRecyclerAdapter(mainActivity, articlesHeaders);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                int startCount = articlesHeaders.size();
                DBHelper mDBHelper = new DBHelper(mainActivity, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
                SQLiteDatabase mDB = mDBHelper.getWritableDatabase();
                articlesHeaders.addAll(extractArticlesHeaders(downloadResult, mDB));
                if (mDBHelper!=null) mDBHelper.close();
                int endCount = articlesHeaders.size();
                adapter.notifyItemRangeInserted(startCount, endCount - startCount);
            } catch (Exception e) {
                Toast.makeText(mainActivity, "Something went wrong with loaded data. ".concat(e.getMessage()),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mainActivity, downloadResult, Toast.LENGTH_SHORT).show();
        }
    }

    public void markHeaderAsRead(int position) {
        DBHelper mDBHelper = new DBHelper(mainActivity, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        SQLiteDatabase mDB = mDBHelper.getWritableDatabase();
        articlesHeaders.get(position).markArticleAsRead(mDB);
        if (mDBHelper!=null) mDBHelper.close();
        adapter.notifyItemChanged(position);
    }

    protected abstract List<ArticleHeader> extractArticlesHeaders(String result, SQLiteDatabase db);

    public List<ArticleHeader> getArticlesHeaders() {
        return articlesHeaders;
    }


    public int getCurrentPageNumber() {
        return currentPageNumber;
    }




    public class DownloadArticlesListTask extends AsyncTask<String, Void, Integer> {

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
            populateData(result);
        }

        private Integer downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 50000;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("Network", "The response is: " + response);
                InputStream it = new BufferedInputStream(conn.getInputStream());
                InputStreamReader read = new InputStreamReader(it);
                BufferedReader buff = new BufferedReader(read);
                StringBuilder dta = new StringBuilder();
                String chunks;
                while ((chunks = buff.readLine()) != null) {
                    dta.append(chunks);
                }
//            is = conn.getInputStream();

                // Convert the InputStream into a string
                downloadResult = dta.toString();
                return 1;
            } catch (Exception e) {
                downloadResult = "Error: ".concat(e.getMessage());
                return 0;
                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        Log.e("Network", e.getMessage());
                    }
                }
            }
        }
    }
}
