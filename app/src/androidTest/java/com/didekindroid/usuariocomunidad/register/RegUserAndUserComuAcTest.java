package com.didekindroid.usuariocomunidad.register;

import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComuSearchResultsAc;
import com.didekindroid.usuariocomunidad.listbyuser.SeeUserComuByUserAc;
import com.didekinlib.model.comunidad.Comunidad;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
import static com.didekindroid.lib_one.testutil.UiTestUtil.checkChildInViewer;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_TK_HANDLER;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.loginAcResourceId;
import static com.didekindroid.testutil.ActivityTestUtil.checkBack;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.usuario.testutil.UserMenuTestUtil.LOGIN_AC;
import static com.didekindroid.usuariocomunidad.register.RegComuAndUserAndUserComuAcTest.execCheckRegisterError;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regUser_UserComuAcLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetComu;
import static java.util.Objects.requireNonNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 13/09/15
 * Time: 11:30
 */
@RunWith(AndroidJUnit4.class)
public class RegUserAndUserComuAcTest {

    private static Comunidad comunidad;
    private RegUserAndUserComuAc activity;

    @Rule
    public IntentsTestRule<RegUserAndUserComuAc> intentRule = new IntentsTestRule<RegUserAndUserComuAc>(RegUserAndUserComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            Intent intent = new Intent(getInstrumentation().getTargetContext(), ComuSearchResultsAc.class)
                    .putExtra(COMUNIDAD_SEARCH.key, comunidad);

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
            return new Intent().putExtra(COMUNIDAD_LIST_OBJECT.key, comunidad);
        }
    };

    @BeforeClass
    public static void setUpStatic() throws Exception
    {
        comunidad = signUpGetComu(COMU_PLAZUELA5_JUAN);
        cleanOptions(CLEAN_TK_HANDLER);
    }

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
        // Precondition:
        assertThat(requireNonNull(activity.viewer.getController()).isRegisteredUser(), is(false));
    }

    @After
    public void tearDown() throws Exception
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cleanTasks(activity);
        }
    }

    @AfterClass
    public static void cleanStatic()
    {
        cleanOptions(CLEAN_JUAN);
    }

    //    =================================== Tests ===================================

    @Test
    public void test_OnCreate()
    {
        assertThat(activity.regUserComuFr, notNullValue());
        assertThat(activity.regUserFr, notNullValue());
        assertThat(activity.acView, notNullValue());
        assertThat(activity.viewer, isA(ViewerRegUserAndUserComuAc.class));

        onView(withId(regUser_UserComuAcLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_user_frg)).perform(scrollTo()).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).perform(scrollTo()).check(matches(isDisplayed()));

        // testRegisterUserAndUserComu_NotOk
        execCheckRegisterError(activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(comuSearchResultsListLayout);     // TODO: fail.
        }
    }

    @Test
    public void test_OnStop()
    {
        // test_SetChildInViewer
        checkChildInViewer(activity);

        checkSubscriptionsOnStop(activity, activity.viewer.getController());
    }

    //    =================================== MENU ===================================

    @Test
    public void testLoginMn_Up()
    {
        doLoginUnRegUser();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(comuSearchAcLayout);
        }
    }

    @Test
    public void testLoginMn_Back()
    {
        doLoginUnRegUser();
        checkBack(onView(withId(loginAcResourceId)), regUser_UserComuAcLayout);
    }

    //    =================================== HELPERS ===================================

    private void doLoginUnRegUser()
    {
        // Precondition.
        assertThat(requireNonNull(activity.viewer.getController()).isRegisteredUser(), is(false));
        activity.runOnUiThread(() -> activity.onPrepareOptionsMenu(activity.acMenu));
        LOGIN_AC.checkItem(activity);
    }
}

