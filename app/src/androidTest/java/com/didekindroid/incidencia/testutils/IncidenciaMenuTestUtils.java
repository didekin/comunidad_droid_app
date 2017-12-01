package com.didekindroid.incidencia.testutils;

import android.app.Activity;

import com.didekindroid.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.testutil.ActivityTestUtils.checkAppBarMenu;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 15:16
 */
public enum IncidenciaMenuTestUtils {

    INCID_COMMENT_REG_AC {
        @Override
        public void checkMenuItem(Activity activity)
        {
            checkAppBarMenu(activity, R.string.incid_comment_reg_ac_mn, R.id.incid_comment_reg_ac_layout);
        }
    },

    INCID_COMMENTS_SEE_AC {
        @Override
        public void checkMenuItem(Activity activity)
        {
            checkAppBarMenu(activity, R.string.incid_comments_see_ac_mn, R.id.incid_comments_see_fr_layout);
        }
    },

    INCID_REG_AC {
        @Override
        public void checkMenuItem(Activity activity)
        {
            checkAppBarMenu(activity, R.string.incid_reg_ac_mn, R.id.incid_reg_ac_layout);
        }
    },

    INCID_RESOLUCION_REG_EDIT_AC {
        @Override
        public void checkMenuItem(Activity activity)
        {
            onView(withText(R.string.incid_resolucion_ac_mn)).check(matches(isDisplayed())).perform(click());
            waitAtMost(2, SECONDS).until(isResourceIdDisplayed(R.id.incid_resolucion_fragment_container_ac));
        }
    },

    INCID_SEE_OPEN_BY_COMU_AC {
        @Override
        public void checkMenuItem(Activity activity)
        {
            checkAppBarMenu(activity, R.string.incid_open_see_by_comu_ac_mn, R.id.incid_see_open_by_comu_ac);
        }
    },

    INCID_SEE_CLOSED_BY_COMU_AC {
        @Override
        public void checkMenuItem(Activity activity)
        {
            checkAppBarMenu(activity, R.string.incid_closed_see_by_comu_ac_mn, R.id.incid_see_closed_by_comu_ac);
        }
    },;

    public abstract void checkMenuItem(Activity activity) throws InterruptedException;

}
