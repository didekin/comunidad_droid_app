package com.didekindroid.usuariocomunidad.register;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.typeComunidadData;
import static com.didekindroid.testutil.ActivityTestUtils.checkChildInViewer;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.testutil.ActivityTestUtils.focusOnButton;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.RolUi.ADM;
import static com.didekindroid.usuariocomunidad.RolUi.INQ;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.RolUi.PRO;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.typeUserComuData;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regComu_UserComuAcLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 09/07/15
 * Time: 09:56
 */
@RunWith(AndroidJUnit4.class)
public class RegComuAndUserComuAcTest {

    @Rule
    public ActivityTestRule<RegComuAndUserComuAc> mActivityRule = new ActivityTestRule<>(RegComuAndUserComuAc.class, true, false);
    RegComuAndUserComuAc activity;
    int buttonId;

    @Before
    public void setUp() throws Exception
    {
        // Preconditions: the user is already registered.
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        activity = mActivityRule.launchActivity(new Intent());
        buttonId = R.id.reg_comu_usuariocomunidad_button;
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOneUser(USER_PEPE);
    }

    //    ================================================================================

    @Test
    public void testRegisterComuAndUserComu_1()
    {
        // Wrong data. UsuarioComunidad wrong data have no effect because its validation requires previous FULL comunidad data validation.
        typeUserComuData("port2", "escal ?? b", "planta-N", "puerta5", PRE, ADM, INQ, PRO);
        onView(withId(buttonId)).perform(scrollTo(), click());

        checkToastInTest(R.string.error_validation_msg, activity,
                R.string.tipo_via,
                R.string.nombre_via,
                R.string.municipio);
    }

    @Test
    public void testRegisterComuAndUserComu_2() throws InterruptedException
    {

        typeUserComuData("port2", "escale_b", "planta-N", "puerta5", PRE, ADM, INQ);
        focusOnButton(activity, buttonId);
        typeComunidadData();
        onView(withId(buttonId)).perform(scrollTo(), click());

        waitAtMost(4,SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
        checkUp(regComu_UserComuAcLayout);
    }

    //    =================================== Life cycle ===================================

    @Test
    public void testOnCreate() throws UiException
    {
        assertThat(activity.regComuFr, notNullValue());
        assertThat(activity.regUserComuFr, notNullValue());
        assertThat(activity.acView, notNullValue());
        assertThat(activity.viewer, isA(ViewerRegComuUserComuAc.class));

        onView(withId(regComu_UserComuAcLayout));
        onView(withId(R.id.reg_comunidad_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));
        onView(withId(buttonId)).perform(scrollTo()).check(matches(isDisplayed()));

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
}