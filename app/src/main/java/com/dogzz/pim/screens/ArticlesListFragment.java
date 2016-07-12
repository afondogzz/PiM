package com.dogzz.pim.screens;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import com.dogzz.pim.MainActivity;
import com.dogzz.pim.R;
import com.dogzz.pim.datahandlers.ArticlesList;
import com.dogzz.pim.dataobject.ArticleHeader;
import com.dogzz.pim.uihandlers.RecyclerItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArticlesListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArticlesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticlesListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PAGE_NUMBER = "pageNumber";
    private static final String ARTICLE_URL = "url";

    private static final String PAGES_DISPLAYED = "pagesDisplayed";
    ArticlesList articlesList;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View view;

    // TODO: Rename and change types of parameters
    private int pageNumber;
    private String url;

    private OnFragmentInteractionListener mListener;

    public ArticlesListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pageNumber 1 if list of articles and 2 if content of article
     * @param url url of article if pageNumber is 2
     * @return A new instance of fragment ArticlesListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArticlesListFragment newInstance(int pageNumber, String url) {
        ArticlesListFragment fragment = new ArticlesListFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE_NUMBER, pageNumber);
        args.putString(ARTICLE_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageNumber = getArguments().getInt(PAGE_NUMBER);
            url = getArguments().getString(ARTICLE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (articlesList == null) {
        view = inflater.inflate(R.layout.fragment_articles_list, container, false);
            ConnectivityManager connMgr =  (ConnectivityManager)
                    getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            articlesList = new ArticlesList(mRecyclerView, getActivity(), connMgr);
            int pagesDisplayed = 1;
//        if (savedInstanceState != null) {
//            pagesDisplayed = savedInstanceState.getInt(PAGES_DISPLAYED);
//        }
            articlesList.loadArticlesHeaders(pagesDisplayed, true);
        }
            mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    ArticleHeader articleHeader = articlesList.getArticlesHeaders().get(position);
                    articleHeader.setRead(true);
//                Intent intent = new Intent(getBaseContext(), ArticleActivity.class);
//                intent.putExtra("URL", articleHeader.getArticleUrl());
//                startActivity(intent);
                    onArticleClicked(articleHeader);
                    Toast.makeText(getActivity(), articleHeader.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLongItemClick(View view, int position) {
                    ArticleHeader articleHeader = articlesList.getArticlesHeaders().get(position);
                    Toast.makeText(getActivity(), articleHeader.getTitle() + " is long pressed!", Toast.LENGTH_SHORT).show();
                }
            }));

//        final LinearLayoutManager mLayoutManager;
//        mLayoutManager = new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(mLayoutManager);

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Toast.makeText(getActivity(), "REFRESH", Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!recyclerView.canScrollVertically(-1)) {
//                    onScrolledToTop();
                    } else if (!recyclerView.canScrollVertically(1)) {
                        mSwipeRefreshLayout.setRefreshing(true);
                        Toast.makeText(getActivity(), "END", Toast.LENGTH_SHORT).show();
                        articlesList.loadNextPage(true);
                        mSwipeRefreshLayout.setRefreshing(false);
//                    onScrolledToBottom();
                    } else if (dy < 0) {
//                    onScrolledUp();
                    } else if (dy > 0) {
//                    onScrolledDown();
                    }
                }
            });
        return view;
    }

    public void onArticleClicked(ArticleHeader articleHeader) {
        if (mListener != null) {
            mListener.onArticleClicked(articleHeader.getArticleUrl());
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
        void onArticleClicked(String uri);
    }
}
