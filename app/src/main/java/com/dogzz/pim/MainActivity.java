package com.dogzz.pim;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.*;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dogzz.pim.persistence.ArticleDownloader;
import com.dogzz.pim.persistence.DBHelper;
import com.dogzz.pim.dataobject.ArticleHeader;
import com.dogzz.pim.screens.ArticleContentFragment;
import com.dogzz.pim.screens.ArticlesListFragment;
import com.dogzz.pim.screens.SettingsFragment;
import com.dogzz.pim.uihandlers.NavigationItem;
import com.dogzz.pim.uihandlers.ProgressPosition;

import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ArticlesListFragment.OnFragmentInteractionListener,
        ArticleContentFragment.OnFragmentInteractionListener,
        ArticleDownloader.DownloadListener {

    private static final String PAGES_DISPLAYED = "pagesDisplayed";
    private static final String LOG_TAG = "MainActivity";
    private ArticlesListFragment articlesListFragment;
    private ArticleContentFragment articleContentFragment;
    private FragmentTransaction fTrans;
    private ActionBarDrawerToggle toggle;
    private Menu mainMenu;
    private ArticleHeader selectedArticleHeader;
    private boolean isContextBarVisible = false;
    private boolean isContentVisible = false;
    private ProgressBar progressBar;
    private NavigationItem selectedNavigationItem = NavigationItem.ARTICLES;
    private NavigationItem previousNavigationItem = null;
    private NavigationView navigationView;
    private boolean isNavDrawerDisplayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.fragment_settings, false);
        setLanguageToSelected();
        setContentView(R.layout.activity_main);
        ConnectivityManager connMgr =  (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            selectedNavigationItem = NavigationItem.SAVED;
        }
        articlesListFragment = ArticlesListFragment.newInstance(selectedNavigationItem);
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.add(R.id.frgmContainer, articlesListFragment);
        fTrans.commit();
//        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progress_spinner);
//        progressBar.setVisibility(View.VISIBLE);


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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        refreshUI();
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
                selectedNavigationItem = previousNavigationItem != null ? previousNavigationItem : selectedNavigationItem;
                isContentVisible = false;
            }
//                selectedNavigationItem = previousNavigationItem != null ? previousNavigationItem : selectedNavigationItem;
//                previousNavigationItem = null;
            switchActionBarToggle(true);
            isContextBarVisible = false;
            refreshUI();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.setGroupVisible(R.id.menu_group1, true);
        menu.setGroupVisible(R.id.menu_group2, false);
        menu.setGroupVisible(R.id.menu_group3, false);
        this.mainMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            articlesListFragment.refreshContent();
            return true;
        } else if (id == R.id.action_download) {
            Toast.makeText(this, selectedArticleHeader.getTitle() + " is downloading", Toast.LENGTH_SHORT).show();
            ArticleDownloader downloader = new ArticleDownloader(this);
            downloader.saveArticleOffline(selectedArticleHeader);
            return true;
        } else if (id == R.id.action_delete) {
            Toast.makeText(this, selectedArticleHeader.getTitle() + " is deleting", Toast.LENGTH_SHORT).show();
            ArticleDownloader downloader = new ArticleDownloader(this);
            downloader.removeArticle(selectedArticleHeader);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_articles) {
            selectNavigationItem(NavigationItem.ARTICLES);
        } else if (id == R.id.nav_news) {
            selectNavigationItem(NavigationItem.NEWS);
//            getSupportActionBar().setTitle(R.string.news);
        } else if (id == R.id.nav_saved) {
            selectNavigationItem(NavigationItem.SAVED);
//            getSupportActionBar().setTitle(R.string.saved);
        } else if (id == R.id.nav_settings) {
            selectNavigationItem(NavigationItem.SETTINGS);
        } else if (id == R.id.nav_about) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void selectNavigationItem(NavigationItem navigationItem) {
        if (selectedNavigationItem != navigationItem) {
            Toast.makeText(this, navigationItem.toString() + " is selected", Toast.LENGTH_SHORT).show();
//            getSupportActionBar().setTitle(navigationItem.getStringId());
            switch (navigationItem) {
                case ARTICLES:
                case NEWS:
                case SAVED:
                    articlesListFragment.setNavigationItem(navigationItem);
                    previousNavigationItem = null;
                    selectedNavigationItem = navigationItem;
                    break;
                case SETTINGS:
                    switchActionBarToggle(false);
                    mainMenu.setGroupVisible(R.id.menu_group2, false);
                    previousNavigationItem = selectedNavigationItem;
                    selectedNavigationItem = navigationItem;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frgmContainer, new SettingsFragment()).addToBackStack(null).commit();
                    break;
            }
            refreshUI();
        }
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
        isContentVisible = true;
        selectedArticleHeader = header;
        previousNavigationItem = null;
//        selectedNavigationItem = NavigationItem.CONTENT;
        switchActionBarToggle(false);
        if (selectedArticleHeader.isOffline()) {
            articleContentFragment = ArticleContentFragment.newInstance(header.getFileName(), true);
        } else {
            articleContentFragment = ArticleContentFragment.newInstance(header.getArticleUrl(), false);
        }
        onJobStarted(ProgressPosition.CENTER);
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fTrans.replace(R.id.frgmContainer, articleContentFragment);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }

    @Override
    public void onArticleLongClicked(ArticleHeader header) {
        isContextBarVisible = true;
        selectedArticleHeader = header;
        switchActionBarToggle(false);
    }

    @Override
    public void onJobStarted(ProgressPosition position) {
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(progressBar.getLayoutParams());
        switch (position) {
            case TOP:
                params.gravity = Gravity.TOP;
                break;
            case CENTER:
                params.gravity = Gravity.CENTER;
                break;
            case BOTTOM:
                params.gravity = Gravity.BOTTOM;
                break;
        }
        progressBar.setLayoutParams(params);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onJobFinished() {
        progressBar.setVisibility(View.GONE);
    }

    private void switchActionBarToggle(boolean toNavigationDraw) {
        mainMenu.setGroupVisible(R.id.menu_group1, toNavigationDraw);
        if (selectedArticleHeader != null && selectedArticleHeader.isOffline()) {
            mainMenu.setGroupVisible(R.id.menu_group2, false);
            mainMenu.setGroupVisible(R.id.menu_group3, !toNavigationDraw);
        } else {
            mainMenu.setGroupVisible(R.id.menu_group2, !toNavigationDraw);
            mainMenu.setGroupVisible(R.id.menu_group3, false);
        }
        toggle.setDrawerIndicatorEnabled(toNavigationDraw);
        getSupportActionBar().setDisplayHomeAsUpEnabled(!toNavigationDraw);
        toggle.syncState();
        isNavDrawerDisplayed = toNavigationDraw;
    }

    private void setLanguageToSelected() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("lang", "en");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    public void onSavedArticleTaskFinished(ArticleHeader header) {
        refreshUI();
    }

    private void refreshUI() {
        getSupportActionBar().setTitle(selectedNavigationItem.getStringId());
        if (selectedArticleHeader != null && isContentVisible) {
            mainMenu.setGroupVisible(R.id.menu_group2, !selectedArticleHeader.isOffline());
            mainMenu.setGroupVisible(R.id.menu_group3, selectedArticleHeader.isOffline());
        }
        if (selectedNavigationItem == NavigationItem.SAVED && selectedArticleHeader != null && !selectedArticleHeader.isOffline()) {
            articlesListFragment.refreshContent();
            if (isContentVisible) {
                mainMenu.setGroupVisible(R.id.menu_group3, false);
                mainMenu.setGroupVisible(R.id.menu_group2, false);
            }
        }
        if (!isContentVisible) selectedArticleHeader = null;
        articlesListFragment.unselectAllItems();
        articlesListFragment.notifyDataSetIsChanged();
        navigationView.setCheckedItem(selectedNavigationItem.getId());

        if (isContextBarVisible) {
            switchActionBarToggle(true);
            isContextBarVisible = false;
        }
    }
}
