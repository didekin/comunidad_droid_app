package com.didekindroid.incidencia.activity.utils;

import android.app.Activity;

import com.didekindroid.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 15:16
 */
public enum IncidenciaMenuTestUtils {

    INCID_REG_AC {

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.incid_reg_ac_mn)).check(doesNotExist());
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
            onView(withText(R.string.incid_reg_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(withId(R.id.incid_reg_ac_layout)).check(matches(isDisplayed()));
        }
    },;

    public abstract void checkMenuItem_WTk(Activity activity) throws InterruptedException;
}
