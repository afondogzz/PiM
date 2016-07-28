package com.dogzz.pim.screens;

import android.content.Context;
import android.support.test.espresso.ViewInteraction;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;
import com.dogzz.pim.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by afon on 28.07.2016.
 */
public class UIObjects {
    public UiDevice mDevice;
    public Context context;
    public static final String MAIN_PACKAGE = "com.dogzz.pim";
    public static final String ID = MAIN_PACKAGE + ":id/";

    public static ViewInteraction articlesMenu = onView(allOf(withText(R.string.articles), withId(R.id.design_menu_item_text)));
    public static ViewInteraction newsMenu = onView(allOf(withText(R.string.news), withId(R.id.design_menu_item_text)));
    public static ViewInteraction savedMenu = onView(allOf(withText(R.string.saved), withId(R.id.design_menu_item_text)));
    public static ViewInteraction downloadAction = onView(withId(R.id.action_download));
    public static ViewInteraction deleteAction = onView(withId(R.id.action_delete));
    public static ViewInteraction refreshAction = onView(withId(R.id.action_refresh));
    public static ViewInteraction articlesList = onView(withId(R.id.my_recycler_view));

    public UIObjects(UiDevice mDevice, Context context) {
        this.mDevice = mDevice;
        this.context = context;
    }

    private String getId(int resId) {
        return context.getResources().getResourceName(resId);
    }

    private String getString(int stringId) {
        return context.getResources().getString(stringId);
    }

    public UiObject navDrawerButton() {
        return mDevice.findObject(new UiSelector().resourceId(getId(R.id.toolbar)).childSelector(
                new UiSelector().className("android.widget.ImageButton")));
    }

    public UiObject articlesMenu() {
        return mDevice.findObject(new UiSelector().resourceId(getId(R.id.design_menu_item_text)).text(getString(R.string.articles)));
    }

    public UiObject headerByIndex(int index) {
        return mDevice.findObject(new UiSelector()
                .resourceId(getId(R.id.articletitle))
                .instance(index));
    }




}
