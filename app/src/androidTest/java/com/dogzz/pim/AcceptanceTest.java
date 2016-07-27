package com.dogzz.pim;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.dogzz.pim.persistence.DBHelper;
import com.dogzz.pim.support.RecyclerViewAssertion;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;
import static android.support.test.espresso.Espresso.*;
import static com.dogzz.pim.screens.UIElement.*;

/**
 * Set of acceptance tests
 * Created by afon on 26.07.2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AcceptanceTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @BeforeClass
    public static void clearAll() {
        Context cnt = InstrumentationRegistry.getTargetContext();
        cnt.deleteDatabase(DBHelper.DB_NAME);
    }

    @Before
    public void setUp() {

    }

    @Test
    public void articlesAreDisplayed() {
        navDrawer.perform(DrawerActions.open());
        articlesMenu.perform(click());
        articlesList.check(RecyclerViewAssertion.hasItemsCount(12));
//        onView(withId(R.id.my_recycler_view)).perform(
//                RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @Test
    public void newsAreDisplayed() {
        navDrawer.perform(DrawerActions.open());
        newsMenu.perform(click());
        articlesList.check(RecyclerViewAssertion.hasItemsCount(12));
    }

    @Test
    public void emptySavedIsDisplayed() {
        navDrawer.perform(DrawerActions.open());
        savedMenu.perform(click());
        articlesList.check(RecyclerViewAssertion.hasItemsCount(0));
    }

    @Test
    public void downloadRefreshDeleteButtonsSwitch() {
        navDrawer.perform(DrawerActions.open());
        articlesMenu.perform(click());
        refreshAction.check(matches(isDisplayed()));
        downloadAction.check(doesNotExist());
        deleteAction.check(doesNotExist());
        articlesList.perform(
                RecyclerViewActions.actionOnItemAtPosition(1, longClick()));
        refreshAction.check(doesNotExist());
        downloadAction.check(matches(isDisplayed()));
        deleteAction.check(doesNotExist());
        downloadAction.perform(click());
        refreshAction.check(matches(isDisplayed()));
        downloadAction.check(doesNotExist());
        deleteAction.check(doesNotExist());
        articlesList.perform(
                RecyclerViewActions.actionOnItemAtPosition(1, longClick()));
        refreshAction.check(doesNotExist());
        downloadAction.check(doesNotExist());
        deleteAction.check(matches(isDisplayed()));
        articlesList.perform(
                RecyclerViewActions.actionOnItemAtPosition(1, longClick()));
        deleteAction.perform(click());
    }

    @Test
    public void savedArticleIsDisplayed() {
        navDrawer.perform(DrawerActions.open());
        articlesMenu.perform(click());
        articlesList.perform(
                RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
        downloadAction.perform(click());
        navDrawer.perform(DrawerActions.open());
        savedMenu.perform(click());
        articlesList.check(RecyclerViewAssertion.hasItemsCount(1));
        articlesList.perform(
                RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
        deleteAction.perform(click());
    }
}
