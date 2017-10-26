package com.didekindroid.usuario.testutil;

import android.app.Activity;
import android.support.test.espresso.NoMatchingViewException;

import com.didekindroid.R;
import com.didekindroid.testutil.MenuTestUtilIf;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.testutil.ActivityTestUtils.isTextIdNonExist;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayedAndPerform;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;

/**
 * User: pedro@didekin
 * Date: 10/08/15
 * Time: 15:09
 */
public enum UserItemMenuTestUtils implements MenuTestUtilIf {

    DELETE_ME_AC {
        @Override
        public void checkItemNoRegisterUser(Activity activity)
        {
            throw new UnsupportedOperationException(DELETE_ME_AC.name() + REGISTERED_USER);
        }

        @Override
        public void checkItemRegisterUser(Activity activity)
        {
            checkItemExists(activity, R.string.delete_me_ac_mn, R.id.delete_me_ac_layout);
        }
    },

    LOGIN_AC {
        @Override
        public void checkItemNoRegisterUser(Activity activity)
        {
            checkItemExists(activity, R.string.login_ac_mn, R.id.login_ac_layout);
        }

        @Override
        public void checkItemRegisterUser(Activity activity)
        {
            checkItemNotExists(activity, R.string.login_ac_mn);
        }
    },

    PASSWORD_CHANGE_AC {
        @Override
        public void checkItemNoRegisterUser(Activity activity)
        {
            throw new UnsupportedOperationException(PASSWORD_CHANGE_AC.name() + REGISTERED_USER);
        }

        @Override
        public void checkItemRegisterUser(Activity activity)
        {
            checkItemExists(activity, R.string.password_change_ac_mn, R.id.password_change_ac_layout);
        }
    },

    USER_DATA_AC {
        @Override
        public void checkItemNoRegisterUser(Activity activity)
        {
            checkItemNotExists(activity, R.string.user_data_ac_mn);
        }

        @Override
        public void checkItemRegisterUser(Activity activity)
        {
            checkItemExists(activity, R.string.user_data_ac_mn, R.id.user_data_ac_layout);
        }
    },;

    public static final String REGISTERED_USER = "requires registered user";
    public static final String REQUIRES_USER_NO_TOKEN = "requires user without token";

    static void checkItemExists(Activity activity, int menuResourceId, int nextLayoutId)
    {
        onView(withText(menuResourceId)).check(doesNotExist());
        openActionBarOverflowOrOptionsMenu(activity);
        waitAtMost(4, SECONDS).until(isViewDisplayedAndPerform(withText(menuResourceId), click()));
        onView(withId(nextLayoutId)).check(matches(isDisplayed()));
    }

    static void checkItemNotExists(Activity activity, int menuResourceId)
    {
        onView(withText(menuResourceId)).check(doesNotExist());
        try {
            openActionBarOverflowOrOptionsMenu(activity);
        } catch (NoMatchingViewException e) {
        }
        waitAtMost(4, SECONDS).until(isTextIdNonExist(menuResourceId));
    }
}