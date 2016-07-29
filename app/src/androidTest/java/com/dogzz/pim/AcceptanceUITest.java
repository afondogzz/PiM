package com.dogzz.pim;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.web.webdriver.Locator;
import android.support.test.filters.SdkSuppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.*;
import com.dogzz.pim.persistence.DBHelper;
import com.dogzz.pim.screens.UIObjects;
import com.dogzz.pim.support.RecyclerViewAssertion;
import org.hamcrest.core.SubstringMatcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static android.support.test.espresso.web.sugar.Web.onWebView;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.getText;
import static com.dogzz.pim.screens.UIElement.*;
import static com.dogzz.pim.screens.UIObjects.*;
import static org.hamcrest.Matchers.*;

/**
 * Set of acceptance tests with UIAutomator
 * Created by afon on 28.07.2016.
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class AcceptanceUITest {

    private static final int TIMEOUT = 5000;
    private UIObjects elements;

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
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Context context = InstrumentationRegistry.getTargetContext();
        elements = new UIObjects(mDevice, context);
//        Intent intent = context.getPackageManager()
//                .getLaunchIntentForPackage(MAIN_PACKAGE);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        // Clear out any previous instances
//        context.startActivity(intent);
//        mDevice.wait(Until.hasObject(By.pkg(MAIN_PACKAGE).depth(0)), TIMEOUT);

    }

    @Test
    public void  articlesAreDisplayed() throws Exception {
        elements.navDrawerButton().click();
        elements.articlesMenu().click();
        UiCollection videos = new UiCollection(new UiSelector()
                .className("android.support.v7.widget.RecyclerView"));

        int count = videos.getChildCount(new UiSelector()
                .className("android.widget.FrameLayout"));
        articlesList.check(RecyclerViewAssertion.hasItemsCount(12));
    }

    @Test
    public void articleCanBeOpened() throws Exception {
//        elements.navDrawerButton().click();
//        elements.articlesMenu().click();
        navDrawer.perform(DrawerActions.open());
        articlesMenu.perform(click());
        String headerText = elements.headerByIndex(0).getText();
        headerText = headerText.substring(0, headerText.indexOf("("));
        onView(withId(R.id.my_recycler_view)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onWebView().withElement(findElement(Locator.TAG_NAME, "h1")).check(webMatches(getText(), containsString(headerText)));
    }

    @Test
    public void newsCanBeOpened() throws Exception {
        navDrawer.perform(DrawerActions.open());
        newsMenu.perform(click());
        String headerText = elements.headerByIndex(0).getText();
//        headerText = headerText.substring(0, headerText.indexOf("("));
        onView(withId(R.id.my_recycler_view)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onWebView().withElement(findElement(Locator.TAG_NAME, "h1")).check(webMatches(getText(), containsString(headerText)));
    }

    @Test
    public void savedArticleCanBeOpened() throws Exception {
        navDrawer.perform(DrawerActions.open());
        articlesMenu.perform(click());
        articlesList.perform(
                RecyclerViewActions.actionOnItemAtPosition(1, longClick()));
        downloadAction.perform(click());
        navDrawer.perform(DrawerActions.open());
        savedMenu.perform(click());
        String headerText = elements.headerByIndex(0).getText();
        headerText = headerText.substring(0, headerText.indexOf("("));
        onView(withId(R.id.my_recycler_view)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onWebView().withElement(findElement(Locator.TAG_NAME, "h1")).check(webMatches(getText(), containsString(headerText)));
        elements.mDevice.pressBack();
        articlesList.perform(
                RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
        deleteAction.perform(click());


    }

}
