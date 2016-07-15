package com.dogzz.pim.screens;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;

import android.webkit.WebView;
import android.widget.Toast;
import com.dogzz.pim.R;
import com.dogzz.pim.asynctask.DownloadTask;
import com.dogzz.pim.datahandlers.ArticleExtractor;
import com.dogzz.pim.datahandlers.HeadersList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;

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
    public String downloadResult = "";

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
//        setHasOptionsMenu(true);
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
        DownloadTask downloadTask = new DownloadArticleTask();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.article_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get item selected and deal with it
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                //called when the up affordance/carat in actionbar is pressed
//                getActivity().onBackPressed();
//                return true;
//        }
        return true;
    }

    public void loadData(int result) {
        if (result == 1) {
            try {
                String pureArticle = extractArticle(downloadResult);
                webView.loadDataWithBaseURL(HeadersList.BASE_URL, pureArticle, "text/html", null, "");
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Something went wrong with loaded data. ".concat(e.getMessage()),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), downloadResult, Toast.LENGTH_SHORT).show();
        }
    }

    private String extractArticle(String result) {
        return ArticleExtractor.extractArticle(result);
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
            loadData(result);
        }

    }
}
