package com.didekindroid.comunidad.testutil;

import android.app.Activity;

import com.didekindroid.R;
import com.didekindroid.lib_one.testutil.MenuTestUtilIf;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static java.lang.Thread.sleep;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 12:30
 */

public enum ComuMenuTestUtil implements MenuTestUtilIf {

    COMU_DATA_AC {

        @Override
        public void checkItem(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.comu_data_ac_mn)).check(doesNotExist());
            openActionBarOverflowOrOptionsMenu(activity);
            sleep(1000);
            onView(withText(R.string.comu_data_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(withId(R.id.comu_data_ac_layout)).check(matches(isDisplayed()));
        }
    },

    COMU_SEARCH_AC {

        @Override
        public void checkItem(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.comu_search_ac_mn)).check(doesNotExist());
            sleep(1000);
            openActionBarOverflowOrOptionsMenu(activity);
            onView(withText(R.string.comu_search_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(withId(R.id.comu_search_ac_linearlayout)).check(matches(isDisplayed()));
        }
    },;
}
