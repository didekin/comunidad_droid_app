package com.didekindroid.comunidad;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.exception.UiException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkRegComuFrViewEmpty;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkSpinnersDoInViewerOffNull;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.typeComunidadData;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchResultsListLayout;
import static com.didekindroid.testutil.ActivityTestUtil.checkBack;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.lib_one.usuario.testutil.UserMenuTestUtils.LOGIN_AC;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.REG_COMU_USERCOMU_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.REG_COMU_USER_USERCOMU_AC;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 15/05/15
 * Time: 09:53
 */
@RunWith(AndroidJUnit4.class)
public class ComuSearchAcTest {

    @Rule
    public ActivityTestRule<ComuSearchAc> activityRule = new ActivityTestRule<>(ComuSearchAc.class, true, false);

    ComuSearchAc activity;

    @Test
    public void test_OnCreate()
    {
        activity = activityRule.launchActivity(new Intent());

        assertThat(activity.acView, notNullValue());
        assertThat(activity.viewerAc, notNullValue());
        assertThat(activity.viewerDrawer, notNullValue());
        assertThat(activity.regComuFrg, notNullValue());

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(allOf(
                withContentDescription(R.string.navigate_up_txt),
                isClickable())).check(matches(isDisplayed()));

        checkRegComuFrViewEmpty();

        // Parent injection.
        assertThat(activity.regComuFrg.viewerInjector, is(activity));
        assertThat(activity.regComuFrg.viewer.getParentViewer(), is(activity.viewerAc));
        checkSpinnersDoInViewerOffNull(activity.regComuFrg.viewer);
    }

    @Test
    public void testWithResultsAndUp() throws InterruptedException, UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_JUAN);
        activity = activityRule.launchActivity(new Intent());
        typeComunidadData();

        onView(withId(R.id.searchComunidad_Bton)).perform(click());
        // Check the view for comunidades list fragment.
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(comuSearchResultsListLayout));

        checkUp(comuSearchAcLayout);

        cleanOneUser(USER_JUAN);
    }

    @Test
    public void testWithResultsAndBack() throws InterruptedException, UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_JUAN);
        activity = activityRule.launchActivity(new Intent());
        typeComunidadData();

        onView(withId(R.id.searchComunidad_Bton)).perform(click());
        // Check the view for comunidades list fragment.
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(comuSearchResultsListLayout));
        // Back.
        checkBack(onView(withId(comuSearchResultsListLayout)), comuSearchAcLayout);

        cleanOneUser(USER_JUAN);
    }

    //    ============================ MENU ==============================

    @Test
    public void testLogin_Registered() throws InterruptedException, UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_JUAN);
        activity = activityRule.launchActivity(new Intent());

        LOGIN_AC.checkItem(activity);
        cleanOneUser(USER_JUAN);
    }

    @Test
    public void testLogin_Unregistered() throws InterruptedException
    {
        activity = activityRule.launchActivity(new Intent());
        assertThat(activity.viewerAc.getController().isRegisteredUser(), is(false));

        LOGIN_AC.checkItem(activity);
        checkUp(comuSearchAcLayout);
    }

    @Test
    public void testMenuNuevaComunidad_NotRegistered() throws InterruptedException
    {
        activity = activityRule.launchActivity(new Intent());
        assertThat(activity.viewerAc.getController().isRegisteredUser(), is(false));
        REG_COMU_USER_USERCOMU_AC.checkItem(activity);

        checkUp(comuSearchAcLayout);
    }

    @Test
    public void testMenuNuevaComunidad_Registered() throws InterruptedException, UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_JUAN);
        activity = activityRule.launchActivity(new Intent());
        REG_COMU_USERCOMU_AC.checkItem(activity);

        checkUp(comuSearchAcLayout);
        cleanOneUser(USER_JUAN);
    }

    //  =========================  TESTS FOR ACTIVITY/FRAGMENT LIFECYCLE  ===========================

    @Test
    public void test_OnStop()
    {
        activity = activityRule.launchActivity(new Intent());
        checkSubscriptionsOnStop(activity, activity.viewerAc.getController(), activity.viewerDrawer.getController());
    }
}