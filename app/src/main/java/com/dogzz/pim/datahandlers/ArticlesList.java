package com.dogzz.pim.datahandlers;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;
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
public class ArticlesList implements Serializable{

    private String downloadResult = "";
    private List<ArticleHeader> articlesHeaders = new ArrayList<>();
    private int currentPageNumber = 0;
    public static final String BASE_URL = "http://petrimazepa.com";
    private RecyclerView recyclerView;
    private MyRecyclerAdapter adapter;
    private Activity mainActivity;
    private ConnectivityManager connectivityManager;
//    public static final String TMP_FILE_PATH = "/temp";
//    public static final String SAVED_FILE_PATH = "/saved";

    public ArticlesList(String contentAsString) {

    }

    public ArticlesList() {

    }

    public ArticlesList(RecyclerView recyclerView, Activity activity, ConnectivityManager connectivityManager) {
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

    private void loadArticlesListFromSource() throws SourceConnectException {
            DownloadArticlesListTask downloadTask = new DownloadArticlesListTask();
            String url = currentPageNumber == 1 ? BASE_URL.concat("/") : BASE_URL.concat("/?page=").concat(String.valueOf(currentPageNumber));
            downloadTask.execute(url);
    }

    public void loadNextPage(boolean updateFromSource) {
        currentPageNumber++;
        loadArticlesHeaders(currentPageNumber, updateFromSource);
    }

    private void populateData(Integer result) {
        if (result == 1) {
            try {
                articlesHeaders.addAll(extractArticlesHeaders(downloadResult));
                adapter = new MyRecyclerAdapter(mainActivity, articlesHeaders);
                recyclerView.setAdapter(adapter);
            } catch (Exception e) {
                Toast.makeText(mainActivity, "Something went wrong with loaded data. ".concat(e.getMessage()),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mainActivity, downloadResult, Toast.LENGTH_SHORT).show();
        }
    }

    private List<ArticleHeader> extractArticlesHeaders(String result) {
        List<ArticleHeader> headers = new ArrayList<>();
        Document doc = Jsoup.parse(result);
        Elements rawHeaders = doc.select("div[class~=effect.*article]");
        Collections.reverse(rawHeaders);
        for (Element rawHeader : rawHeaders) {
            ArticleHeader header = new ArticleHeader();
            header.setTitle(rawHeader.select("h4").text());
            header.setSubTitle(rawHeader.select("p").text());
            header.setArticleUrl(BASE_URL.concat("/").concat(rawHeader.select("a").attr("href").replace("/", "")));
            header.setArticleImageUrl(BASE_URL.concat("/").concat(rawHeader.select("img").attr("data-original").trim()));
            header.setLoadDate(System.currentTimeMillis());
            headers.add(header);
        }
        Collections.reverse(headers);
        return headers;
    }

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
