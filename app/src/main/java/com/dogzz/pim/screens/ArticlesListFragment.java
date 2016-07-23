package com.dogzz.pim.screens;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewTreeObserver;
import com.dogzz.pim.R;
import com.dogzz.pim.datahandlers.ArticlesHeadersList;
import com.dogzz.pim.datahandlers.HeadersList;
import com.dogzz.pim.datahandlers.NewsHeadersList;
import com.dogzz.pim.datahandlers.SavedHeadersList;
import com.dogzz.pim.dataobject.ArticleHeader;
import com.dogzz.pim.uihandlers.NavigationItem;
import com.dogzz.pim.uihandlers.ProgressPosition;
import com.dogzz.pim.uihandlers.RecyclerItemClickListener;
import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArticlesListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArticlesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticlesListFragment extends Fragment {
    private static final String NAVIGATION_ITEM = "navigationItem";
    private static final String ARTICLE_URL = "url";

    private static final String PAGES_DISPLAYED = "pagesDisplayed";
    HeadersList headersList;
    private RecyclerView mRecyclerView;
    private View view;

    private NavigationItem navigationItem = NavigationItem.ARTICLES;
    private String url;

    private OnFragmentInteractionListener mListener;
    private ConnectivityManager connMgr;
    private GridLayoutManager lManager;

    public ArticlesListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param navigationItem
     * @return A new instance of fragment ArticlesListFragment.
     */
    public static ArticlesListFragment newInstance(NavigationItem navigationItem) {
        ArticlesListFragment fragment = new ArticlesListFragment();
        Bundle args = new Bundle();
        args.putInt(NAVIGATION_ITEM, navigationItem.getItemNo());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            navigationItem = NavigationItem.fromNumber(getArguments().getInt(NAVIGATION_ITEM));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {

            view = inflater.inflate(R.layout.fragment_articles_list, container, false);
            connMgr = (ConnectivityManager)
                    getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
            lManager = new GridLayoutManager(getActivity(), 1);
            mRecyclerView.setLayoutManager(lManager);
            headersList = getHeadersListInstance(connMgr);
            int pagesDisplayed = 1;
            headersList.loadArticlesHeaders(pagesDisplayed, true);
            mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    ArticleHeader articleHeader = headersList.getArticlesHeaders().get(position);
                    headersList.markHeaderAsRead(position);
                    onArticleClicked(articleHeader);
                }

                @Override
                public void onLongItemClick(View view, int position) {
                    ArticleHeader articleHeader = headersList.getArticlesHeaders().get(position);
                    headersList.markHeaderAsSelected(position);
                    onArticleLongClicked(articleHeader);
                }
            }));

             mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!recyclerView.canScrollVertically(-1)) {
                    } else if (!recyclerView.canScrollVertically(1)) {
                        loadNextPageIntoView();
                    } else if (dy < 0) {//                    onScrolledUp();
                    } else if (dy > 0) {//                    onScrolledDown();
                    }
                    addAdditionalItemsIfNeeded();
                }
            });
        }

        repaintList();
        return view;
    }

    @NotNull
    private HeadersList getHeadersListInstance(ConnectivityManager connMgr) {
        switch (navigationItem) {
            case ARTICLES:
                return new ArticlesHeadersList(mRecyclerView, getActivity(), connMgr);
            case NEWS:
                return new NewsHeadersList(mRecyclerView, getActivity(), connMgr);
            case SAVED:
                return new SavedHeadersList(mRecyclerView, getActivity(), connMgr);
            default:
                return null;
        }
    }

    private void loadNextPageIntoView() {
        headersList.loadNextPage(true);
    }

    public void refreshContent() {
        if (headersList != null)
            headersList.refreshContent();
    }

    public void onArticleClicked(ArticleHeader articleHeader) {
        if (mListener != null) {
            mListener.onArticleClicked(articleHeader);
        }
    }

    public void onArticleLongClicked(ArticleHeader articleHeader) {
        if (mListener != null) {
            mListener.onArticleLongClicked(articleHeader);
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

    public void setNavigationItem (NavigationItem navigationItem) {
        if (this.navigationItem != navigationItem) {
            this.navigationItem = navigationItem;
            headersList = getHeadersListInstance(connMgr);
            int pagesDisplayed = 1;
            headersList.loadArticlesHeaders(pagesDisplayed, true);
        }
    }

    public void unselectAllItems() {
        if (headersList != null)
            headersList.unselectAllItems();
    }

    public void notifyHeaderIsChanged(ArticleHeader header) {
        headersList.notifyHeaderIsChanged(header);
    }

    public void notifyDataSetIsChanged() {
        if (headersList != null)
            headersList.notifyDataSetIsChanged();
    }

    public void repaintList() {
        final int columns = getResources().getInteger(R.integer.gallery_columns);
        int scrollPosition = lManager.findFirstCompletelyVisibleItemPosition();
        lManager.setSpanCount(columns);
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        mRecyclerView.setAdapter(null);
        mRecyclerView.setAdapter(adapter);
        notifyDataSetIsChanged();
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    public void addAdditionalItemsIfNeeded() {
        if (!mRecyclerView.canScrollVertically(-1) && !mRecyclerView.canScrollVertically(1)) {
            loadNextPageIntoView();
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
        void onArticleClicked(ArticleHeader header);
        void onArticleLongClicked(ArticleHeader header);
        void onJobStarted(ProgressPosition position);
        void onJobFinished();
    }
}
