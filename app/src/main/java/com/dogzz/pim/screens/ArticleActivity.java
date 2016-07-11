package com.dogzz.pim.screens;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;
import com.dogzz.pim.R;
import com.dogzz.pim.datahandlers.ArticlesList;
import com.dogzz.pim.uihandlers.MyRecyclerAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ArticleActivity extends AppCompatActivity {

    private WebView webView;
    private String downloadResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webView = (WebView) findViewById(R.id.webView);
        String url = getIntent().getStringExtra("URL");
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        ArticleActivity.DownloadArticleListTask downloadTask = new ArticleActivity.DownloadArticleListTask();
        if (networkInfo != null && networkInfo.isConnected()) {
            downloadTask.execute(url);
        } else {
            downloadResult = "Error: The network is not available. You can read Offline articles.";
            Toast.makeText(this, downloadResult, Toast.LENGTH_SHORT).show();
        }



    }

    public void loadData(int result) {
        if (result == 1) {
            try {
                String pureArticle = extractArticle(downloadResult);
                webView.loadDataWithBaseURL(ArticlesList.BASE_URL, pureArticle, "text/html", null, "");
            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong with loaded data. ".concat(e.getMessage()),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, downloadResult, Toast.LENGTH_SHORT).show();
        }
    }

    private String extractArticle(String result) {
        String resultHtml;
        Document doc = Jsoup.parse(result);
        Elements heading = doc.select("div[class=heading]");
        heading.select("div[class*=Breadcrumb]").first().text(""); //remove breadcrumbs
        heading.select("img").attr("width", "99%");
        resultHtml = heading.html().concat(doc.select("div[class=mainContent]").html());
        return resultHtml;
    }

    public class DownloadArticleListTask extends AsyncTask<String, Void, Integer> {

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
            loadData(result);
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
