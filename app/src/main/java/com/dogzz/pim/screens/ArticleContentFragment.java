package com.dogzz.pim.screens;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebView;
import android.widget.Toast;
import com.dogzz.pim.R;
import com.dogzz.pim.datahandlers.ArticlesList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArticleContentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArticleContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticleContentFragment extends Fragment {
    private static final String ARTICLE_URL = "articleUrl";

    private String articleUrl;

    private OnFragmentInteractionListener mListener;
    private WebView webView;
    private String downloadResult = "";

    public ArticleContentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param url Parameter 1.
     * @return A new instance of fragment ArticleContentFragment.
     */
    public static ArticleContentFragment newInstance(String url) {
        ArticleContentFragment fragment = new ArticleContentFragment();
        Bundle args = new Bundle();
        args.putString(ARTICLE_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            articleUrl = getArguments().getString(ARTICLE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_content, container, false);
        webView = (WebView) view.findViewById(R.id.webView);
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        DownloadArticleTask downloadTask = new DownloadArticleTask();
        if (networkInfo != null && networkInfo.isConnected()) {
            downloadTask.execute(articleUrl);
        } else {
            downloadResult = "Error: The network is not available. You can read Offline articles.";
            Toast.makeText(getActivity(), downloadResult, Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void loadData(int result) {
        if (result == 1) {
            try {
                String pureArticle = extractArticle(downloadResult);
                webView.loadDataWithBaseURL(ArticlesList.BASE_URL, pureArticle, "text/html", null, "");
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Something went wrong with loaded data. ".concat(e.getMessage()),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), downloadResult, Toast.LENGTH_SHORT).show();
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

    public class DownloadArticleTask extends AsyncTask<String, Void, Integer> {

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
