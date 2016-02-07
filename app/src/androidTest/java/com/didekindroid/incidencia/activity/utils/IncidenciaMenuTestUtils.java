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

    INCID_COMMENT_REG_AC {
        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            check(activity, R.string.incid_comment_reg_ac_mn, R.id.incid_comment_reg_ac_layout);
        }
    },

    INCID_COMMENTS_SEE_AC {
        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            check(activity, R.string.incid_comments_see_ac_mn, R.id.incid_comment_see_frg);
        }
    },

    INCID_REG_AC {
        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            check(activity, R.string.incid_reg_ac_mn, R.id.incid_reg_ac_layout);
        }
    },

    INCID_SEE_BY_COMU_AC {
        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            check(activity, R.string.incid_see_by_comu_ac_mn, R.id.incid_see_by_comu_ac);
        }
    },

    INCID_SEE_CLOSED_BY_USER_AC {
        @Override
        public void checkMenuItem_WTk(Activity activity) throws InterruptedException
        {
            check(activity, R.string.incid_closed_see_by_usercomu_ac_mn, R.id.incid_see_closed_by_comu_ac);
        }
    },;

    public abstract void checkMenuItem_WTk(Activity activity) throws InterruptedException;

//    ============================= HELPER METHODS ==========================

    private static void check(Activity activity, int menuResourceId, int actionResourceId) throws InterruptedException
    {
        onView(withText(menuResourceId)).check(doesNotExist());
        openActionBarOverflowOrOptionsMenu(activity);
        Thread.sleep(1000);
        onView(withText(menuResourceId)).check(matches(isDisplayed())).perform(click());
        onView(withId(actionResourceId)).check(matches(isDisplayed()));
    }
}
