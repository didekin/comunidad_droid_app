package com.didekindroid.usuariocomunidad.testutil;

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
import static com.didekindroid.testutil.ActivityTestUtils.checkNoToastInTest;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.REGISTERED_USER;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.REQUIRES_USER_NO_TOKEN;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 12:31
 */

public enum UserComuMenuTestUtil implements MenuTestUtilIf {

    REG_COMU_USERCOMU_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity)
        {
            throw new UnsupportedOperationException(REGISTERED_USER);
        }

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
            onView(ViewMatchers.withText(R.string.reg_nueva_comunidad_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(ViewMatchers.withId(R.id.reg_comu_and_usercomu_layout)).check(matches(isDisplayed()));
        }
    },

    REG_COMU_USER_USERCOMU_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity) throws InterruptedException
        {
            onView(ViewMatchers.withText(R.string.reg_nueva_comunidad_ac_mn)).check(doesNotExist());
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
            onView(ViewMatchers.withText(R.string.reg_nueva_comunidad_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(ViewMatchers.withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));
        }

        @Override
        public void checkMenuItem_WTk(Activity activity)
        {
            throw new UnsupportedOperationException(REQUIRES_USER_NO_TOKEN);
        }
    },

    SEE_USERCOMU_BY_COMU_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity)
        {
            throw new UnsupportedOperationException(SEE_USERCOMU_BY_COMU_AC.name() + REGISTERED_USER);
        }

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            onView(ViewMatchers.withText(R.string.see_usercomu_by_comu_ac_mn)).check(doesNotExist());
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
            onView(ViewMatchers.withText(R.string.see_usercomu_by_comu_ac_mn)).check(matches(isDisplayed())).perform(click());
            onView(ViewMatchers.withId(R.id.see_usercomu_by_comu_frg)).check(matches(isDisplayed()));
        }
    },

    SEE_USERCOMU_BY_USER_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity) throws InterruptedException
        {
            onView(ViewMatchers.withText(R.string.see_usercomu_by_user_ac_mn)).check(doesNotExist());
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
            onView(ViewMatchers.withText(R.string.see_usercomu_by_user_ac_mn)).check(doesNotExist());
        }

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            onView(ViewMatchers.withText(R.string.see_usercomu_by_user_ac_mn)).check(doesNotExist());
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
            onView(ViewMatchers.withText(R.string.see_usercomu_by_user_ac_mn)).check(matches(isDisplayed())).perform(click());

            // No muestra toast de error.
            checkNoToastInTest(R.string.user_without_signedUp, activity);
            onView(ViewMatchers.withId(R.id.see_usercomu_by_user_frg)).check(matches(isDisplayed()));
        }
    },
    ;
}
