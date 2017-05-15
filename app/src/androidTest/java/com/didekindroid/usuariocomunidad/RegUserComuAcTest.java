package com.didekindroid.usuariocomunidad;

import android.content.Intent;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.comunidad.utils.ComuBundleKey;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil;
import com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil;
import com.didekinlib.model.comunidad.Comunidad;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanTwoUsers;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.RolUi.PRO;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 20/08/15
 * Time: 17:23
 */
@RunWith(AndroidJUnit4.class)
public class RegUserComuAcTest {

    @Rule
    public IntentsTestRule<RegUserComuAc> mActivityRule = new IntentsTestRule<>(RegUserComuAc.class, true, false);
    Comunidad comunidad;
    private RegUserComuAc activity;
    private Intent intent;
    private int activityLayoutId = R.id.reg_usercomu_ac_layout;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(2000);
    }

    @Before
    public void setUp() throws Exception
    {
        UserComuDataTestUtil.signUpAndUpdateTk(UserComuDataTestUtil.COMU_REAL_JUAN);
        List<Comunidad> comunidadesUserOne = userComuDaoRemote.getComusByUser();
        comunidad = comunidadesUserOne.get(0);

        // We use that comunidad as the one to associate to the present user.
        intent = new Intent();
        intent.putExtra(COMUNIDAD_LIST_OBJECT.key, comunidad);
        // Segundo usuarioComunidad.
        UserComuDataTestUtil.signUpAndUpdateTk(UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanTwoUsers(UsuarioDataTestUtils.USER_JUAN, UsuarioDataTestUtils.USER_PEPE);
    }

    @Test
    public void testOnCreate() throws Exception
    {
        activity = mActivityRule.launchActivity(intent);

        assertThat(TKhandler.isRegisteredUser(), is(true));
        List<Comunidad> comunidadesUserOne = userComuDaoRemote.getComusByUser();
        assertThat(comunidadesUserOne.size(), is(1));
        Comunidad comunidad2 = comunidadesUserOne.get(0);
        assertThat(comunidad2, Matchers.is(UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE.getComunidad()));

        assertThat(activity, notNullValue());
        assertThat(activity.getFragmentManager().findFragmentById(R.id.reg_usercomu_frg), notNullValue());
        Comunidad comunidadIntent = (Comunidad) intent.getSerializableExtra(COMUNIDAD_LIST_OBJECT.key);
        assertThat(comunidadIntent.getC_Id(), is(comunidad.getC_Id()));
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.appbar)).perform(scrollTo()).check(matches(isDisplayed()));
        clickNavigateUp();
    }

    @Test
    public void testOnclick_1()
    {
        // Validation errors.
        activity = mActivityRule.launchActivity(intent);

        UserComuEspressoTestUtil.typeUserComuData("portal?", "select *", "planta!", "puerta_1");
        onView(ViewMatchers.withId(R.id.reg_usercomu_button)).check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.error_validation_msg, activity, R.string.reg_usercomu_portal_rot,
                R.string.reg_usercomu_escalera_rot,
                R.string.reg_usercomu_planta_rot, R.string.reg_usercomu_role_rot);
    }

    @Test
    public void testOnclick_2()
    {
        // Data input OK; the next manager is initiated.
        activity = mActivityRule.launchActivity(intent);

        UserComuEspressoTestUtil.typeUserComuData("portalA", "escC", "plantaB", "puerta_1", PRO, PRE);
        onView(ViewMatchers.withId(R.id.reg_usercomu_button)).check(matches(isDisplayed())).perform(click());

        onView(ViewMatchers.withId(R.id.see_usercomu_by_comu_frg)).check(matches(isDisplayed()));
        intended(IntentMatchers.hasExtra(ComuBundleKey.COMUNIDAD_ID.key, comunidad.getC_Id()));
        checkUp(activityLayoutId);
    }
}