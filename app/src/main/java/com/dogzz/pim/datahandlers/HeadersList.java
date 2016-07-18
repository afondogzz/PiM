package com.dogzz.pim.datahandlers;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import com.dogzz.pim.asynctask.DownloadTask;
import com.dogzz.pim.persistence.DBHelper;
import com.dogzz.pim.dataobject.ArticleHeader;
import com.dogzz.pim.exception.SourceConnectException;
import com.dogzz.pim.screens.ArticlesListFragment;
import com.dogzz.pim.uihandlers.MyRecyclerAdapter;
import com.dogzz.pim.uihandlers.ProgressPosition;

import java.io.*;
import java.util.ArrayList;
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
    public static final int DAY_IN_SECONDS = 86400;
    protected RecyclerView recyclerView;
    protected MyRecyclerAdapter adapter;
    protected Activity mainActivity;
    protected ConnectivityManager connectivityManager;
    protected ArticlesListFragment.OnFragmentInteractionListener mListener;
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
        if (mainActivity instanceof ArticlesListFragment.OnFragmentInteractionListener) {
            mListener = ((ArticlesListFragment.OnFragmentInteractionListener) mainActivity);
        }
    }

    public void loadArticlesHeaders(int pageNumber, boolean updateFromSource) {
        if (updateFromSource || articlesHeaders.isEmpty()) {
            try {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    currentPageNumber = pageNumber;
                    mListener.onJobStarted(currentPageNumber == 1 ? ProgressPosition.CENTER : ProgressPosition.BOTTOM);
                    loadArticlesListFromSource();
                    return;
                } else {
                    downloadResult = "Error: The network is not available. You can read Offline articles.";
                    Toast.makeText(mainActivity, downloadResult, Toast.LENGTH_LONG).show();
                }
            } catch (SourceConnectException e) {
                downloadResult = "Error: Unable to connect to the source. Check your internet settings.";
                Toast.makeText(mainActivity, downloadResult, Toast.LENGTH_LONG).show();
            }
            articlesHeaders.clear();
            if (adapter == null) {
                adapter = new MyRecyclerAdapter(mainActivity, articlesHeaders);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    protected abstract void loadArticlesListFromSource() throws SourceConnectException;

    public void loadNextPage(boolean updateFromSource) {
        currentPageNumber++;
        loadArticlesHeaders(currentPageNumber, updateFromSource);
    }

    public void refreshContent() {
//        int itemsCount = articlesHeaders.size();
        articlesHeaders.clear();
//        adapter.notifyItemRangeRemoved(0, itemsCount);
        adapter = null;
        loadArticlesHeaders(1, true);
    }

    protected void populateData(Integer result) {
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
                mListener.onJobFinished();
            } catch (Exception e) {
                Toast.makeText(mainActivity, "Something went wrong with loaded data. ".concat(e.getMessage()),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mainActivity, downloadResult, Toast.LENGTH_SHORT).show();
            mListener.onJobFinished();
        }
    }

    public void markHeaderAsRead(int position) {
        DBHelper mDBHelper = new DBHelper(mainActivity, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        SQLiteDatabase mDB = mDBHelper.getWritableDatabase();
        articlesHeaders.get(position).markArticleAsRead(mDB);
        if (mDBHelper!=null) mDBHelper.close();
        adapter.notifyItemChanged(position);
    }

    public void markHeaderAsSelected(int position) {
        adapter.selectItem(position);
    }

    public void unselectAllItems() {
        if (adapter != null)
            adapter.unselectAllItems();
    }

    public void notifyHeaderIsChanged(ArticleHeader header) {
        if (adapter != null)
            adapter.notifyItemChanged(articlesHeaders.indexOf(header));
    }

    public void notifyDataSetIsChanged() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    protected abstract List<ArticleHeader> extractArticlesHeaders(String result, SQLiteDatabase db);

    public List<ArticleHeader> getArticlesHeaders() {
        return articlesHeaders;
    }


    public int getCurrentPageNumber() {
        return currentPageNumber;
    }




    public class DownloadArticlesListTask extends DownloadTask {

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
            populateData(result);
        }
    }
}
