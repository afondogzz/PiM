package com.dogzz.pim.screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.*;

import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import com.dogzz.pim.R;
import com.dogzz.pim.asynctask.DownloadTask;
import com.dogzz.pim.datahandlers.ArticleExtractor;
import com.dogzz.pim.datahandlers.HeadersList;
import com.dogzz.pim.persistence.ArticleDownloader;
import com.dogzz.pim.uihandlers.ProgressPosition;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.Arrays;

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
    private static final String IS_SAVED = "isSaved";
    private static final String LOG_TAG = "ArticleContentFragment";
    private static final String SHOW_VIDEO = "showVideo";
    private static final String VIDEO_WIDTH = "videoWidth";

    private String articleUrl;
    private boolean isArticleSaved;

    private OnFragmentInteractionListener mListener;
    private WebView webView;
    public String downloadResult = "";
    private boolean showVideo = false;
    private int videoWidth;

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
    public static ArticleContentFragment newInstance(String url, boolean isSaved, boolean showVideo, int videoWidth) {
        ArticleContentFragment fragment = new ArticleContentFragment();
        Bundle args = new Bundle();
        args.putString(ARTICLE_URL, url);
        args.putBoolean(IS_SAVED, isSaved);
        args.putBoolean(SHOW_VIDEO, showVideo);
        args.putInt(VIDEO_WIDTH, videoWidth);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            articleUrl = getArguments().getString(ARTICLE_URL);
            isArticleSaved = getArguments().getBoolean(IS_SAVED);
            showVideo = getArguments().getBoolean(SHOW_VIDEO);
            videoWidth = getArguments().getInt(VIDEO_WIDTH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_content, container, false);
        webView = (WebView) view.findViewById(R.id.webView);
        if (showVideo) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebChromeClient(new WebChromeClient() {
            });
        }
        if (!isArticleSaved) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            DownloadTask downloadTask = new DownloadArticleTask();
            if (networkInfo != null && networkInfo.isConnected()) {
                downloadTask.execute(articleUrl);
            } else {
                downloadResult = getResources().getString(R.string.ErrorNoNetwork);
                Toast.makeText(getActivity(), downloadResult, Toast.LENGTH_SHORT).show();
            }
        } else {
            loadDataFromFile(articleUrl);
        }
        return view;
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
        if (mListener != null) mListener.onJobFinished();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void loadDataFromFile(String fileName) {
        try {
            String pureArticle = readFile(fileName);
            webView.loadDataWithBaseURL(HeadersList.BASE_URL, pureArticle, "text/html", null, "");
            if (mListener != null) mListener.onJobFinished();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong with read data. ".concat(e.getMessage()),
                    Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Something went wrong with read data. ".concat(e.getMessage()));
        }
    }

    private String readFile(String fileName) throws IOException {
        String pureArticle = "";
        String path = getActivity().getFilesDir().getAbsolutePath().concat("/").concat(fileName);
        FileInputStream fin = new FileInputStream(path.concat("/")
                .concat(fileName.concat(ArticleDownloader.FILE_EXT)));
        BufferedReader br = new BufferedReader(new InputStreamReader(fin));
        String str = "";
        while ((str = br.readLine()) != null) {
            pureArticle = pureArticle.concat(str);
        }
        return pureArticle;
    }

    public void loadData(int result) {
        if (result == 1) {
            try {
                String pureArticle = extractArticle(downloadResult);
                webView.loadDataWithBaseURL(HeadersList.BASE_URL, pureArticle, "text/html", null, "");
                if (mListener != null) mListener.onJobFinished();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Something went wrong with loaded data. ".concat(Arrays.asList(e.getStackTrace()).toString()));
                Log.e(LOG_TAG, "Something went wrong with loaded data. ".concat(e.getMessage()));
                Toast.makeText(getActivity(), "Something went wrong with loaded data. ".concat(e.getMessage()),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), downloadResult, Toast.LENGTH_SHORT).show();
        }
    }

    private String extractArticle(String result) {
        return ArticleExtractor.extractArticle(result, showVideo, videoWidth);
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
                Log.e(LOG_TAG, downloadResult);
                result = 0;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            downloadResult = resultMessage;
            loadData(result);
        }
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
        void onJobStarted(ProgressPosition position);
        void onJobFinished();
    }
}
