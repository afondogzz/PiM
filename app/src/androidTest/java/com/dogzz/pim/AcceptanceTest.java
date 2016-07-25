package com.dogzz.pim;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.dogzz.pim.support.RecyclerViewAssertion;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.*;
import static android.support.test.espresso.Espresso.*;

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

    @Test
    public void articlesAreDisplayed() {
        onView(withId(R.id.my_recycler_view)).check(new RecyclerViewAssertion(12));
        onView(withId(R.id.my_recycler_view)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

    }
}
