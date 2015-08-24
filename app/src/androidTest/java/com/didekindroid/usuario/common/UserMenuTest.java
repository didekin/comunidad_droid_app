package com.didekindroid.usuario.common;

import android.app.Activity;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import com.didekindroid.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.didekindroid.common.ui.ViewsIDs.COMUNIDADES_USER;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 10/08/15
 * Time: 15:09
 */
public enum UserMenuTest {

    USER_DATA_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.user_data_ac_mn)).check(doesNotExist());
            openActionBarOverflowOrOptionsMenu(activity);

            onView(withText(R.string.user_data_ac_mn)).check(matches(isDisplayed())).perform(click());
            ViewInteraction toastViewInteraction = onView(withText(
                    containsString(activity.getResources().getText(R.string.user_without_signedUp).toString())));
            toastViewInteraction.inRoot(withDecorView(not(activity.getWindow().getDecorView())))
                    .check(matches(isDisplayed()));

            Thread.sleep(4000);
        }

        @Override
        public void checkMenuItem_WTk(Activity activity)
        {
            openActionBarOverflowOrOptionsMenu(activity);
            onView(withText(R.string.user_data_ac_mn)).check(matches(isDisplayed())).perform(click());

            // Show the data in modifiable state.
            onView(withId(R.id.reg_usuario_layout)).check(matches(isDisplayed()));

            // User clean up.
            assertThat(ServOne.deleteUser(), is(true));
        }
    },

    COMU_BY_USER_LIST_AC {
        @Override
        public void checkMenuItem_NTk(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.usercomu_list_ac_mn)).check(doesNotExist());

            openActionBarOverflowOrOptionsMenu(activity);
            onView(withText(R.string.usercomu_list_ac_mn)).check(matches(isDisplayed())).perform(click());
            ViewInteraction toastViewInteraction = onView(withText(
                    containsString(activity.getResources().getText(R.string.user_without_signedUp).toString())));
            toastViewInteraction.inRoot(withDecorView(not(activity.getWindow().getDecorView())))
                    .check(matches(isDisplayed()));

            Thread.sleep(4000);
        }

        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            onView(withText(R.string.usercomu_list_ac_mn)).check(doesNotExist());

            openActionBarOverflowOrOptionsMenu(activity);
            onView(withText(R.string.usercomu_list_ac_mn)).check(matches(isDisplayed())).perform(ViewActions.click());

            // No muestra toast de error.
            ViewInteraction toastViewInteraction = onView(withText(
                    containsString(activity.getResources().getText(R.string.user_without_signedUp).toString())));
            toastViewInteraction.inRoot(withDecorView(not(activity.getWindow().getDecorView())))
                    .check(doesNotExist());

            onView(withId(COMUNIDADES_USER.idView)).check(matches(isDisplayed()));
            // User clean up.
            assertThat(ServOne.deleteUser(), is(true));

            Thread.sleep(4000);
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
            onView(withText(R.string.reg_comu_user_usercomu_ac_mn)).check(matches(isDisplayed())).perform(ViewActions.click());
            onView(withId(R.id.reg_comu_usuariocomu_layout)).check(matches(isDisplayed()));

            // User clean up.
            assertThat(ServOne.deleteUser(), is(true));
        }
    },

    ;

    public abstract void checkMenuItem_NTk(Activity activity) throws InterruptedException;

    public abstract void checkMenuItem_WTk(Activity activity) throws InterruptedException;

}