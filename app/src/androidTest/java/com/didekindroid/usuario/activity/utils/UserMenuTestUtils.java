package com.didekindroid.usuario.activity.utils;

import android.app.Activity;
import com.didekindroid.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.didekindroid.uiutils.ViewsIDs.SEE_COMU_AND_USER_COMU_BY_USER;
import static com.didekindroid.usuario.UsuarioTestUtils.checkNoToastInTest;
import static com.didekindroid.usuario.UsuarioTestUtils.checkToastInTest;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 10/08/15
 * Time: 15:09
 */
public enum UserMenuTestUtils {

    COMU_SEARCH_AC{
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
            onView(withText(R.string.comu_search_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(withId(R.id.comu_search_ac_layout)).check(matches(isDisplayed()));
        }
    },

    REG_COMU_USER_USERCOMU_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity)
        {
            onView(withText(R.string.reg_comu_user_usercomu_ac_mn)).check(doesNotExist());

            openActionBarOverflowOrOptionsMenu(activity);
            onView(withText(R.string.reg_comu_user_usercomu_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));
        }

        @Override
        public void checkMenuItem_WTk(Activity activity)
        {
            openActionBarOverflowOrOptionsMenu(activity);
            onView(withText(R.string.reg_comu_user_usercomu_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(withId(R.id.reg_comu_and_usercomu_layout)).check(matches(isDisplayed()));
        }
    },

    REG_USER_AND_USERCOMU_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.reg_user_and_usercomu_ac_mn)).check(doesNotExist());

            openActionBarOverflowOrOptionsMenu(activity);
            onView(withText(R.string.reg_user_and_usercomu_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(withId(R.id.reg_user_and_usercomu_ac_layout)).check(matches(isDisplayed()));
        }

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.reg_user_and_usercomu_ac_mn)).check(doesNotExist());

            openActionBarOverflowOrOptionsMenu(activity);
            onView(withText(R.string.reg_user_and_usercomu_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(withId(R.id.reg_usercomu_ac_layout)).check(matches(isDisplayed()));
        }
    },

    SEE_USERCOMU_BY_USER_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.see_usercomu_by_user_ac_mn)).check(doesNotExist());

            openActionBarOverflowOrOptionsMenu(activity);
            onView(withText(R.string.see_usercomu_by_user_ac_mn)).check(matches(isDisplayed())).perform(click());
            checkToastInTest(R.string.user_without_signedUp, activity);

            Thread.sleep(4000);
        }

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.see_usercomu_by_user_ac_mn)).check(doesNotExist());

            openActionBarOverflowOrOptionsMenu(activity);
            onView(withText(R.string.see_usercomu_by_user_ac_mn)).check(matches(isDisplayed())).perform(click());

            // No muestra toast de error.
            checkNoToastInTest(R.string.user_without_signedUp, activity);
            onView(withId(SEE_COMU_AND_USER_COMU_BY_USER.idView)).check(matches(isDisplayed()));

            Thread.sleep(4000);
        }
    },

    USER_DATA_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.user_data_ac_mn)).check(doesNotExist());
            openActionBarOverflowOrOptionsMenu(activity);

            onView(withText(R.string.user_data_ac_mn)).check(matches(isDisplayed())).perform(click());
            checkToastInTest(R.string.user_without_signedUp, activity);

            Thread.sleep(4000);
        }

        @Override
        public void checkMenuItem_WTk(Activity activity)
        {
            openActionBarOverflowOrOptionsMenu(activity);
            onView(withText(R.string.user_data_ac_mn)).check(matches(isDisplayed())).perform(click());

            // Show the data in modifiable state.
            onView(withId(R.id.reg_usuario_layout)).check(matches(isDisplayed()));
        }
    },

    ;

    public abstract void checkMenuItem_NTk(Activity activity) throws InterruptedException;

    public abstract void checkMenuItem_WTk(Activity activity) throws InterruptedException;

}