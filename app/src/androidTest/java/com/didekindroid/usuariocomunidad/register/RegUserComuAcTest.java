package com.didekindroid.usuariocomunidad.register;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekinlib.model.comunidad.Comunidad;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.lib_one.testutil.UiTestUtil.checkChildInViewer;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetAuthTk;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.clickNavigateUp;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.RolUi.PRO;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.typeUserComuData;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regUserComuAcLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpMockGcmGetComu;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 20/08/15
 * Time: 17:23
 */
@RunWith(AndroidJUnit4.class)
public class RegUserComuAcTest {

    private static Comunidad comunidad;
    private RegUserComuAc activity;

    @Rule
    public IntentsTestRule<RegUserComuAc> intentRule = new IntentsTestRule<RegUserComuAc>(RegUserComuAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            return new Intent().putExtra(COMUNIDAD_LIST_OBJECT.key, comunidad);
        }
    };

    @BeforeClass
    public static void setUpStatic() throws Exception
    {
        comunidad = signUpMockGcmGetComu(COMU_PLAZUELA5_JUAN, "juan_gcm_tk");
        regComuUserUserComuGetAuthTk(COMU_TRAV_PLAZUELA_PEPE);
    }

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
    }

    @AfterClass
    public static void tearDown()
    {
        cleanOptions(CLEAN_JUAN_AND_PEPE);
    }

    @Test
    public void testOnclick_1()
    {
        // test_OnCreate
        assertThat(activity.regUserComuFr, notNullValue());
        assertThat(activity.acView, notNullValue());
        assertThat(activity.viewer, isA(ViewerRegUserComuAc.class));

        onView(withId(regUserComuAcLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).perform(scrollTo()).check(matches(isDisplayed()));

        // test_SetChildInViewer.
        checkChildInViewer(activity);

        // Validation errors.
        typeUserComuData("portal?", "select *", "planta!", "puerta_1");
        onView(withId(R.id.reg_usercomu_button)).check(matches(isDisplayed())).perform(click());

        waitAtMost(4, SECONDS).until(isToastInView(
                R.string.error_validation_msg,
                activity, R.string.reg_usercomu_portal_rot,
                R.string.reg_usercomu_escalera_rot,
                R.string.reg_usercomu_planta_rot,
                R.string.reg_usercomu_role_rot));

        clickNavigateUp();
    }

    @Test
    public void testOnclick_2()
    {
        // Data input OK; the next manager is initiated.

        typeUserComuData("portalA", "escC", "plantaB", "puerta_1", PRO, PRE);
        onView(withId(R.id.reg_usercomu_button)).check(matches(isDisplayed())).perform(click());

        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
        checkUp(regUserComuAcLayout);
    }
}