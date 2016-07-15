package com.dogzz.pim;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Toast;

import com.dogzz.pim.persistence.ArticleDownloader;
import com.dogzz.pim.persistence.DBHelper;
import com.dogzz.pim.dataobject.ArticleHeader;
import com.dogzz.pim.screens.ArticleContentFragment;
import com.dogzz.pim.screens.ArticlesListFragment;
import com.dogzz.pim.uihandlers.NavigationItem;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ArticlesListFragment.OnFragmentInteractionListener,
        ArticleContentFragment.OnFragmentInteractionListener {

    private static final String PAGES_DISPLAYED = "pagesDisplayed";
    private static final String LOG_TAG = "MainActivity";
    private ArticlesListFragment articlesListFragment;
    private ArticleContentFragment articleContentFragment;
    private FragmentTransaction fTrans;
    private ActionBarDrawerToggle toggle;
    private Menu mainMenu;
    private ArticleHeader selectedArticleHeader;
    private boolean isContextBarVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            articlesListFragment = ArticlesListFragment.newInstance(NavigationItem.ARTICLES);
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.add(R.id.frgmContainer, articlesListFragment);
            fTrans.commit();
//        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        ConnectivityManager connMgr =  (ConnectivityManager)
//                getSystemService(Context.CONNECTIVITY_SERVICE);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
////                webView.loadUrl(BASE_URL);
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_articles);
        // Navigation back icon listener
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!isContextBarVisible) {
                super.onBackPressed();
            }
            articlesListFragment.unselectAllItems();
            isContextBarVisible = false;
            switchActionBarToggle(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.setGroupVisible(R.id.menu_group1, true);
        menu.setGroupVisible(R.id.menu_group2, false);
        this.mainMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            return true;
        } else if (id == R.id.action_download) {
            Toast.makeText(this, selectedArticleHeader + " is downloading", Toast.LENGTH_SHORT).show();
            ArticleDownloader downloader = new ArticleDownloader(this);

            downloader.saveArticleOffline(selectedArticleHeader);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_articles) {
            Toast.makeText(this, "Articles is selected", Toast.LENGTH_SHORT).show();
            articlesListFragment.setNavigationItem(NavigationItem.ARTICLES);
        } else if (id == R.id.nav_news) {
            articlesListFragment.setNavigationItem(NavigationItem.NEWS);
            Toast.makeText(this, "News is selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_saved) {
            articlesListFragment.setNavigationItem(NavigationItem.SAVED);
            Toast.makeText(this, "Saved is selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_about) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
//        savedInstanceState.putInt(PAGES_DISPLAYED, HeadersList.getCurrentPageNumber());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onArticleClicked(ArticleHeader header) {
        isContextBarVisible = false;
        switchActionBarToggle(false);
        selectedArticleHeader = header;
        articleContentFragment = ArticleContentFragment.newInstance(header.getArticleUrl());
        fTrans = getSupportFragmentManager().beginTransaction();
//        fTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fTrans.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fTrans.replace(R.id.frgmContainer, articleContentFragment);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }

    @Override
    public void onArticleLongClicked(ArticleHeader header) {
        isContextBarVisible = true;
        switchActionBarToggle(false);
        selectedArticleHeader = header;
    }

    private void switchActionBarToggle(boolean toNavigationDraw) {
        mainMenu.setGroupVisible(R.id.menu_group1, toNavigationDraw);
        mainMenu.setGroupVisible(R.id.menu_group2, !toNavigationDraw);
        toggle.setDrawerIndicatorEnabled(toNavigationDraw);
        getSupportActionBar().setDisplayHomeAsUpEnabled(!toNavigationDraw);
        toggle.syncState();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
