package com.didekindroid.usuariocomunidad.register;

import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComuSearchResultsAc;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.usuariocomunidad.listbyuser.SeeUserComuByUserAc;
import com.didekinlib.model.comunidad.Comunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.v4.app.TaskStackBuilder.create;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchResultsListLayout;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.testutil.ActivityTestUtil.checkBack;
import static com.didekindroid.testutil.ActivityTestUtil.checkChildInViewer;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.cleanTasks;
import static com.didekindroid.lib_one.usuario.testutil.UserMenuTestUtils.LOGIN_AC;
import static com.didekindroid.usuario.UserTestNavigation.loginAcResourceId;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_TK_HANDLER;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.usuariocomunidad.register.RegComuAndUserAndUserComuAcTest.execCheckRegisterError;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpWithTkGetComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regUser_UserComuAcLayout;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 13/09/15
 * Time: 11:30
 */
@RunWith(AndroidJUnit4.class)
public class RegUserAndUserComuAcTest {

    Comunidad comunidad;

    @Rule
    public IntentsTestRule<RegUserAndUserComuAc> intentRule = new IntentsTestRule<RegUserAndUserComuAc>(RegUserAndUserComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            Intent intent = new Intent(getInstrumentation().getTargetContext(), ComuSearchResultsAc.class);
            intent.putExtra(COMUNIDAD_SEARCH.key, comunidad);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                create(getTargetContext())
                        .addParentStack(SeeUserComuByUserAc.class)  // Includes ComuSearchAc in stack.
                        .addNextIntent(intent) // Includes ComuSearchResultsAc in stack.
                        .startActivities();
            }
        }

        @Override
        protected Intent getActivityIntent()
        {
            try {
                comunidad = signUpWithTkGetComu(COMU_PLAZUELA5_JUAN);
                cleanOptions(CLEAN_TK_HANDLER);
            } catch (UiException | IOException e) {
                fail();
            }
            Intent intent = new Intent();
            intent.putExtra(COMUNIDAD_LIST_OBJECT.key, comunidad);
            return intent;
        }
    };

    RegUserAndUserComuAc activity;

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
        // Precondition:
        assertThat(activity.viewer.getController().isRegisteredUser(), is(false));
    }

    @After
    public void tearDown() throws Exception
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cleanTasks(activity);
        }
        cleanOptions(CLEAN_JUAN);
    }

    //    =================================== Tests ===================================

    @Test
    public void testRegisterUserAndUserComu_NotOk() throws UiException
    {
        execCheckRegisterError(activity);
    }

    //    =================================== Life cycle ===================================

    @Test
    public void test_OnCreate() throws Exception
    {
        assertThat(activity.regUserComuFr, notNullValue());
        assertThat(activity.regUserFr, notNullValue());
        assertThat(activity.acView, notNullValue());
        assertThat(activity.viewer, isA(ViewerRegUserAndUserComuAc.class));

        onView(withId(regUser_UserComuAcLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_user_frg)).perform(scrollTo()).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).perform(scrollTo()).check(matches(isDisplayed()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(comuSearchResultsListLayout);
        }
    }

    @Test
    public void test_OnStop() throws Exception
    {
        checkSubscriptionsOnStop(activity, activity.viewer.getController());
    }

    @Test
    public void test_SetChildInViewer()
    {
        checkChildInViewer(activity);
    }

    //    =================================== MENU ===================================

    @Test
    public void testLoginMn_UnRegUser_Up() throws InterruptedException, UiException
    {
        doLoginUnRegUser();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(comuSearchAcLayout);
        }
    }

    @Test
    public void testLoginMn_UnRegUser_Back() throws InterruptedException, UiException
    {
        doLoginUnRegUser();
        checkBack(onView(withId(loginAcResourceId)), regUser_UserComuAcLayout);
    }

    //    =================================== HELPERS ===================================

    @SuppressWarnings("RedundantThrowsDeclaration")
    private void doLoginUnRegUser() throws InterruptedException
    {
        activity.runOnUiThread(() -> activity.onPrepareOptionsMenu(activity.acMenu));
        LOGIN_AC.checkItem(activity);
    }
}

