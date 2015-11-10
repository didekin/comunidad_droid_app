package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.serviceone.domain.Comunidad;
import com.didekindroid.R;
import com.didekindroid.usuario.dominio.FullComunidadIntent;

import org.hamcrest.CoreMatchers;
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
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PRESIDENTE;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PROPIETARIO;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.checkToastInTest;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanTwoUsers;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.typeRegUserComuData;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_JUAN;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_PEPE;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 20/08/15
 * Time: 17:23
 */
@RunWith(AndroidJUnit4.class)
public class RegUserComuAcTest {

    private RegUserComuAc activity;
    private Intent intent;
    Comunidad comunidad;

    @Rule
    public ActivityTestRule<RegUserComuAc> mActivityRule =
            new ActivityTestRule<>(RegUserComuAc.class, true, false);

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Before
    public void setUp() throws Exception
    {
        signUpAndUpdateTk(COMU_REAL_JUAN);
        List<Comunidad> comunidadesUserOne = ServOne.getComusByUser();
        comunidad = comunidadesUserOne.get(0);

        // We use that comunidad as the one to associate to the present user.
        intent = new Intent();
        intent.putExtra(COMUNIDAD_LIST_OBJECT.extra, new FullComunidadIntent(comunidad));
        // Segundo usuarioComunidad.
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
    }

    @Test
    public void testOnCreate() throws Exception
    {
        activity = mActivityRule.launchActivity(intent);

        assertThat(isRegisteredUser(activity), is(true));
        List<Comunidad> comunidadesUserOne = ServOne.getComusByUser();
        assertThat(comunidadesUserOne.size(), is(1));
        Comunidad comunidad2 = comunidadesUserOne.get(0);
        assertThat(comunidad2, not(is(COMU_REAL_JUAN.getComunidad())));
        assertThat(comunidad2, is(COMU_TRAV_PLAZUELA_PEPE.getComunidad()));

        assertThat(activity, notNullValue());
        assertThat(activity.getFragmentManager().findFragmentById(R.id.reg_usercomu_frg), notNullValue());
        FullComunidadIntent comunidadIntent = (FullComunidadIntent) intent.getSerializableExtra(COMUNIDAD_LIST_OBJECT.extra);
        assertThat(comunidadIntent.getComunidad().getC_Id(), is(comunidad.getC_Id()));
        onView(withId(R.id.reg_usercomu_ac_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withContentDescription("Navigate up")).check(matches(isDisplayed()));
        onView(CoreMatchers.allOf(
                        withContentDescription("Navigate up"),
                        isClickable())
        ).check(matches(isDisplayed())).perform(click());
    }

    @Test
    public void testOnclick_1()
    {
        // Validation errors.
        activity = mActivityRule.launchActivity(intent);

        typeRegUserComuData("portal?", "select *", "planta!", "puerta_1");
        onView(withId(R.id.reg_usercomu_button)).check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.error_validation_msg, activity, R.string.reg_usercomu_portal_hint,
                R.string.reg_usercomu_escalera_hint,
                R.string.reg_usercomu_planta_hint, R.string.reg_usercomu_role_rot);
    }

    @Test
    public void testOnclick_2()
    {
        // Data input OK; the next activity is initiated.
        activity = mActivityRule.launchActivity(intent);
        typeRegUserComuData("portalA", "escC", "plantaB", "puerta_1", PROPIETARIO, PRESIDENTE);
        onView(withId(R.id.reg_usercomu_button)).check(matches(isDisplayed())).perform(click());

        onView(withId(R.id.see_usercomu_by_comu_ac_frg_container)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception
    {
        cleanTwoUsers(USER_JUAN, USER_PEPE);
    }
}