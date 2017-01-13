package com.didekindroid.comunidad.testutil;

import android.app.Activity;
import android.support.test.espresso.matcher.ViewMatchers;

import com.didekindroid.R;
import com.didekindroid.testutil.MenuTestUtilIf;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.REGISTERED_USER;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 12:30
 */

public enum  ComuMenuTestUtil implements MenuTestUtilIf {

    COMU_DATA_AC {

        @Override
        public void checkMenuItem_NTk(Activity activity)
        {
            throw new UnsupportedOperationException(COMU_DATA_AC.name() + REGISTERED_USER);
        }

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            onView(ViewMatchers.withText(R.string.comu_data_ac_mn)).check(doesNotExist());
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
            onView(ViewMatchers.withText(R.string.comu_data_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(ViewMatchers.withId(R.id.comu_data_ac_layout)).check(matches(isDisplayed()));
        }
    },

    COMU_SEARCH_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity) throws InterruptedException
        {
            checkMenuItem_WTk(activity);
        }

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            onView(ViewMatchers.withText(R.string.comu_search_ac_mn)).check(doesNotExist());
            Thread.sleep(2000);
            openActionBarOverflowOrOptionsMenu(activity);
            onView(ViewMatchers.withText(R.string.comu_search_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(ViewMatchers.withId(R.id.comu_search_ac_linearlayout)).check(matches(isDisplayed()));
        }
    },
    ;
}
