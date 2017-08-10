package com.didekindroid.usuariocomunidad.register;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComuSearchResultsAc;
import com.didekindroid.exception.UiException;
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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchResultsListLayout;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkBack;
import static com.didekindroid.testutil.ActivityTestUtils.checkChildInViewer;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typeUserDataFull;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.LOGIN_AC;
import static com.didekindroid.usuario.testutil.UserNavigationTestConstant.loginAcResourceId;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.RolUi.PRO;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.typeUserComuData;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regUser_UserComuAcLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
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
@SuppressWarnings("ConstantConditions")
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

            TaskStackBuilder.create(getTargetContext())
                    .addParentStack(SeeUserComuByUserAc.class)  // Includes ComuSearchAc in stack.
                    .addNextIntent(intent) // Includes ComuSearchResultsAc in stack.
                    .startActivities();
        }

        @Override
        protected Intent getActivityIntent()
        {
            try {
                comunidad = signUpWithTkGetComu(COMU_PLAZUELA5_JUAN);
            } catch (UiException | IOException e) {
                fail();
            }
            Intent intent = new Intent();
            intent.putExtra(COMUNIDAD_LIST_OBJECT.key, comunidad);
            return intent;
        }
    };

    RegUserAndUserComuAc activity;
    boolean isClean;

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        if (isClean) {
            return;
        }
        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void testRegisterUserAndUserComu_1() throws UiException
    {
        // Usuario data.
        typeUserDataFull(
                USER_PEPE.getUserName(),
                USER_PEPE.getAlias(),
                USER_PEPE.getPassword(),
                USER_PEPE.getPassword());

        // UsurioComunidad data.
        typeUserComuData("portalA", "escC", "plantaB", "puerta_1", PRO, PRE);
        onView(withId(R.id.reg_user_usercomu_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        waitAtMost(5, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
        waitAtMost(4, SECONDS).untilAtomic(TKhandler.getTokenCache(), notNullValue());

        checkUp(comuSearchAcLayout);

        cleanOptions(CLEAN_JUAN_AND_PEPE);
        isClean = true;
    }

    @Test
    public void testRegisterUserAndUserComu_2() throws UiException
    {
        // Usuario data.
        typeUserDataFull(
                USER_PEPE.getUserName(),
                USER_PEPE.getAlias(),
                USER_PEPE.getPassword(),
                USER_PEPE.getPassword());

        // UsurioComunidad data.
        typeUserComuData("WRONG**", "escC", "plantaB", "puerta_1", PRO, PRE);
        onView(withId(R.id.reg_user_usercomu_button)).perform(scrollTo()).perform(click());

        waitAtMost(4, SECONDS).until(isToastInView(R.string.error_validation_msg, activity,
                R.string.reg_usercomu_portal_rot));

        checkUp(comuSearchResultsListLayout);
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
        clickNavigateUp();
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
        checkUp(comuSearchAcLayout);
    }

    @Test
    public void testLoginMn_UnRegUser_Back() throws InterruptedException, UiException
    {
        doLoginUnRegUser();
        checkBack(onView(withId(loginAcResourceId)), regUser_UserComuAcLayout);
    }

    @Test
    public void testLoginMn_RegUser() throws InterruptedException, UiException, IOException
    {
        assertThat(TKhandler.isRegisteredUser(), is(true));
        LOGIN_AC.checkItemRegisterUser(activity);
    }

    //    =================================== HELPERS ===================================

    @SuppressWarnings("RedundantThrowsDeclaration")
    private void doLoginUnRegUser() throws InterruptedException
    {
        activity.viewer.getController().updateIsRegistered(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.onPrepareOptionsMenu(activity.acMenu);
            }
        });
        LOGIN_AC.checkItemNoRegisterUser(activity);
    }
}

