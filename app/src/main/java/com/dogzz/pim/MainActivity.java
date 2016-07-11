package com.dogzz.pim;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.dogzz.pim.datahandlers.ArticlesList;
import com.dogzz.pim.dataobject.ArticleHeader;
import com.dogzz.pim.screens.ArticleActivity;
import com.dogzz.pim.uihandlers.RecyclerItemClickListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String PAGES_DISPLAYED = "pagesDisplayed";
    ArticlesList articlesList;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ConnectivityManager connMgr =  (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                webView.loadUrl(BASE_URL);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        articlesList = new ArticlesList(mRecyclerView, this, connMgr);
        int pagesDisplayed = 1;
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            pagesDisplayed = savedInstanceState.getInt(PAGES_DISPLAYED);
        }
        articlesList.loadArticlesHeaders(pagesDisplayed, true);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ArticleHeader articleHeader = articlesList.getArticlesHeaders().get(position);
                articleHeader.setRead(true);
                Intent intent = new Intent(getBaseContext(), ArticleActivity.class);
                intent.putExtra("URL", articleHeader.getArticleUrl());
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), articleHeader.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongItemClick(View view, int position) {
                ArticleHeader articleHeader = articlesList.getArticlesHeaders().get(position);
                Toast.makeText(getApplicationContext(), articleHeader.getTitle() + " is long pressed!", Toast.LENGTH_SHORT).show();
            }
        }));

//        final LinearLayoutManager mLayoutManager;
//        mLayoutManager = new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(mLayoutManager);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                     @Override
                                                     public void onRefresh() {
                                                         Toast.makeText(getApplicationContext(), "REFRESH", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "END", Toast.LENGTH_SHORT).show();
                    articlesList.loadNextPage(true);
                    mSwipeRefreshLayout.setRefreshing(false);
//                    onScrolledToBottom();
                } else if (dy < 0) {
//                    onScrolledUp();
                } else if (dy > 0) {
//                    onScrolledDown();
                }
//                visibleItemCount = mRecyclerView.getChildCount();
//                totalItemCount = mLayoutManager.getItemCount();
//                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
//
//                if (loading) {
//                    if (totalItemCount > previousTotal) {
//                        loading = false;
//                        previousTotal = totalItemCount;
//                    }
//                }
//                if (!loading && (totalItemCount - visibleItemCount)
//                        <= (firstVisibleItem + visibleThreshold)) {
//                    // End has been reached
//
//                    Toast.makeText(getApplicationContext(), "END", Toast.LENGTH_SHORT).show();
//
//                    // Do something
//
//                    loading = true;
//                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt(PAGES_DISPLAYED, articlesList.getCurrentPageNumber());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}
