package com.dogzz.pim.screens;

import android.support.test.espresso.ViewInteraction;
import android.view.View;
import com.dogzz.pim.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by afon on 27.07.2016.
 */
public class UIElement {
    public static ViewInteraction navDrawer = onView(withId(R.id.drawer_layout));
    public static ViewInteraction articlesMenu = onView(allOf(withText(R.string.articles), withId(R.id.design_menu_item_text)));
    public static ViewInteraction newsMenu = onView(allOf(withText(R.string.news), withId(R.id.design_menu_item_text)));
    public static ViewInteraction savedMenu = onView(allOf(withText(R.string.saved), withId(R.id.design_menu_item_text)));
    public static ViewInteraction downloadAction = onView(withId(R.id.action_download));
    public static ViewInteraction deleteAction = onView(withId(R.id.action_delete));
    public static ViewInteraction refreshAction = onView(withId(R.id.action_refresh));
    public static ViewInteraction articlesList = onView(withId(R.id.my_recycler_view));
}
