package com.didekindroid.usuario.activity.utils;

import android.app.Activity;

import com.didekindroid.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.checkNoToastInTest;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.checkToastInTest;
import static com.didekindroid.common.utils.ViewsIDs.SEE_USER_COMU_BY_USER;

/**
 * User: pedro@didekin
 * Date: 10/08/15
 * Time: 15:09
 */
public enum UserMenuTestUtils {

    COMU_DATA_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity) throws InterruptedException
        {
            throw new UnsupportedOperationException(COMU_DATA_AC.name() + REGISTERED_USER);
        }

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.comu_data_ac_mn)).check(doesNotExist());
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
            onView(withText(R.string.comu_data_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(withId(R.id.comu_data_ac_layout)).check(matches(isDisplayed()));
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
            onView(withText(R.string.comu_search_ac_mn)).check(doesNotExist());
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(2000);
            onView(withText(R.string.comu_search_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(withId(R.id.comu_search_ac_layout)).check(matches(isDisplayed()));
        }
    },

    DELETE_ME_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity) throws InterruptedException
        {
            throw new UnsupportedOperationException(DELETE_ME_AC.name() + REGISTERED_USER);
        }

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.delete_me_ac_mn)).check(doesNotExist());
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
            onView(withText(R.string.delete_me_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(withId(R.id.delete_me_ac_layout)).check(matches(isDisplayed()));
        }
    },

    LOGIN_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.login_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(withId(R.id.login_ac_layout)).check(matches(isDisplayed()));
        }

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            throw new UnsupportedOperationException(LOGIN_AC.name() + REQUIRES_USER_NO_TOKEN);
        }
    },

    PASSWORD_CHANGE_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity) throws InterruptedException
        {
            throw new UnsupportedOperationException(PASSWORD_CHANGE_AC.name() + REGISTERED_USER);
        }

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.password_change_ac_mn)).check(doesNotExist());
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
            onView(withText(R.string.password_change_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(withId(R.id.password_change_ac_layout)).check(matches(isDisplayed()));
        }
    },

    REG_COMU_USER_USERCOMU_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.reg_comu_user_usercomu_ac_mn)).check(doesNotExist());

            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
            onView(withText(R.string.reg_comu_user_usercomu_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));
        }

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
            onView(withText(R.string.reg_comu_user_usercomu_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(withId(R.id.reg_comu_and_usercomu_layout)).check(matches(isDisplayed()));
        }
    },

    SEE_USERCOMU_BY_COMU_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity) throws InterruptedException
        {
            throw new UnsupportedOperationException(SEE_USERCOMU_BY_COMU_AC.name() + REGISTERED_USER);
        }

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.see_usercomu_by_comu_ac_mn)).check(doesNotExist());
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
            onView(withText(R.string.see_usercomu_by_comu_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(withId(R.id.see_usercomu_by_comu_ac_frg_container)).check(matches(isDisplayed()));
        }
    },

    SEE_USERCOMU_BY_USER_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.see_usercomu_by_user_ac_mn)).check(doesNotExist());

            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(3000);
            onView(withText(R.string.see_usercomu_by_user_ac_mn)).check(matches(isDisplayed())).perform(click());
            checkToastInTest(R.string.user_without_signedUp, activity);
        }

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.see_usercomu_by_user_ac_mn)).check(doesNotExist());

            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(2000);
            onView(withText(R.string.see_usercomu_by_user_ac_mn)).check(matches(isDisplayed())).perform(click());

            // No muestra toast de error.
            checkNoToastInTest(R.string.user_without_signedUp, activity);
            onView(withId(SEE_USER_COMU_BY_USER.idView)).check(matches(isDisplayed()));
        }
    },

    USER_DATA_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.user_data_ac_mn)).check(doesNotExist());
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(2000);
            onView(withText(R.string.user_data_ac_mn)).check(matches(isDisplayed())).perform(click());
            checkToastInTest(R.string.user_without_signedUp, activity);
        }

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
            onView(withText(R.string.user_data_ac_mn)).check(matches(isDisplayed())).perform(click());

            // Show the data in modifiable state.
            onView(withId(R.id.user_data_ac_layout)).check(matches(isDisplayed()));
        }
    },;

    public abstract void checkMenuItem_NTk(Activity activity) throws InterruptedException;

    public abstract void checkMenuItem_WTk(Activity activity) throws InterruptedException;

    public static final String REGISTERED_USER = "requires registered user";
    public static final String REQUIRES_USER_NO_TOKEN = "requires user without token";
}