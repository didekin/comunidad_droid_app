package com.didekindroid.usuariocomunidad.register;

import android.content.Intent;
import android.os.Build;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.TaskStackBuilder.create;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.typeComunidadData;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.lib_one.testutil.UiTestUtil.checkChildInViewer;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.lib_one.testutil.UiTestUtil.focusOnView;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetAuthTk;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.typeUserComuData;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regComu_UserComuAcLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_TRAV_PLAZUELA_PEPE;
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
    private RegComuAndUserComuAc activity;
    private int buttonId;

    @BeforeClass
    public static void setUpStatic() throws Exception
    {
        regComuUserUserComuGetAuthTk(COMU_TRAV_PLAZUELA_PEPE);
    }

    @Before
    public void setUp() throws Exception
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            create(getTargetContext()).addParentStack(RegComuAndUserComuAc.class).startActivities();
        }
        activity = mActivityRule.launchActivity(new Intent());
        buttonId = R.id.reg_comu_usuariocomunidad_button;
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
        cleanOneUser(USER_PEPE.getUserName());
    }

    //    ================================================================================

    @Test
    public void testRegisterComuAndUserComu_2()
    {
        // testOnCreate
        assertThat(activity.regComuFr, notNullValue());
        assertThat(activity.regUserComuFr, notNullValue());
        assertThat(activity.acView, notNullValue());
        assertThat(activity.viewer, isA(ViewerRegComuUserComuAc.class));

        onView(withId(regComu_UserComuAcLayout));
        onView(withId(R.id.reg_comunidad_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));
        onView(withId(buttonId)).perform(scrollTo()).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).perform(scrollTo()).check(matches(isDisplayed()));

        // test_SetChildInViewer
        checkChildInViewer(activity);

        // Wrong data both in comunidad and usuarioComunidad.
        typeUserComuData("port2", "escal ?? b", "planta-N", "puerta5", PRE);
        onView(withId(buttonId)).perform(scrollTo(), click());
        // Check: usuarioComunidad wrong data have no effect because its validation requires previous FULL comunidad data validation.
        checkToastInTest(R.string.error_validation_msg, activity,
                R.string.tipo_via,
                R.string.nombre_via,
                R.string.municipio);

        // OK.
        onView(withId(R.id.reg_usercomu_escalera_ed)).perform(replaceText("escaleraB"));
        focusOnView(activity, buttonId);
        typeComunidadData();
        onView(withId(buttonId)).perform(scrollTo(), click());

        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
        checkUp(comuSearchAcLayout);
    }

    //    =================================== Life cycle ===================================

    @Test
    public void test_OnStop()
    {
        checkSubscriptionsOnStop(activity, activity.viewer.getController());
    }
}