package com.didekindroid.usuario.testutil;

import android.app.Activity;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;

import com.didekindroid.R;
import com.didekindroid.testutil.MenuTestUtilIf;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

/**
 * User: pedro@didekin
 * Date: 10/08/15
 * Time: 15:09
 */
public enum UserItemMenuTestUtils implements MenuTestUtilIf {

    DELETE_ME_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity)
        {
            throw new UnsupportedOperationException(DELETE_ME_AC.name() + REGISTERED_USER);
        }

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            onView(ViewMatchers.withText(R.string.delete_me_ac_mn)).check(ViewAssertions.doesNotExist());
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
            onView(ViewMatchers.withText(R.string.delete_me_ac_mn)).check(matches(isDisplayed())).perform(ViewActions.click());
            onView(ViewMatchers.withId(R.id.delete_me_ac_layout)).check(matches(isDisplayed()));
        }
    },

    LOGIN_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity)
        {
            onView(ViewMatchers.withText(R.string.login_ac_mn)).check(matches(isDisplayed())).perform(ViewActions.click());
            onView(ViewMatchers.withId(R.id.login_ac_layout)).check(matches(isDisplayed()));
        }

        @Override
        public void checkMenuItem_WTk(Activity activity)
        {
            checkMenuItem_NTk(activity);
        }
    },

    PASSWORD_CHANGE_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity)
        {
            throw new UnsupportedOperationException(PASSWORD_CHANGE_AC.name() + REGISTERED_USER);
        }

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            onView(ViewMatchers.withText(R.string.password_change_ac_mn)).check(ViewAssertions.doesNotExist());
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
            onView(ViewMatchers.withText(R.string.password_change_ac_mn)).check(matches(isDisplayed())).perform(ViewActions.click());
            onView(ViewMatchers.withId(R.id.password_change_ac_layout)).check(matches(isDisplayed()));
        }
    },

    USER_DATA_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity) throws InterruptedException
        {
            onView(ViewMatchers.withText(R.string.user_data_ac_mn)).check(ViewAssertions.doesNotExist());
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
            onView(ViewMatchers.withText(R.string.user_data_ac_mn)).check(doesNotExist());
        }

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
            onView(ViewMatchers.withText(R.string.user_data_ac_mn)).check(matches(isDisplayed())).perform(ViewActions.click());
            // Show the data in modifiable state.
            onView(ViewMatchers.withId(R.id.user_data_ac_layout)).check(matches(isDisplayed()));
        }
    },;

    public static final String REGISTERED_USER = "requires registered user";
    public static final String REQUIRES_USER_NO_TOKEN = "requires user without token";
}