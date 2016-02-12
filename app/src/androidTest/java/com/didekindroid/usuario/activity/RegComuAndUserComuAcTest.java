package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekin.usuario.dominio.Municipio;
import com.didekin.usuario.dominio.Provincia;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.UiException;
import com.didekindroid.common.utils.ActivityTestUtils;
import com.didekindroid.usuario.dominio.ComunidadBean;
import com.didekindroid.usuario.dominio.UsuarioComunidadBean;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.TokenHandler.TKhandler;
import static com.didekindroid.common.utils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUserComuBeanFromView;
import static com.didekindroid.common.utils.ActivityTestUtils.cleanOneUser;
import static com.didekindroid.common.utils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.typeComunidadData;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_PEPE;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 09/07/15
 * Time: 09:56
 */
@RunWith(AndroidJUnit4.class)
public class RegComuAndUserComuAcTest {

    private RegComuAndUserComuAc activity;
    private RegUserComuFr regUserComuFr;

    @Rule
    public ActivityTestRule<RegComuAndUserComuAc> mActivityRule = new ActivityTestRule<>(RegComuAndUserComuAc.class, true, false);

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Before
    public void setUp() throws Exception
    {
        Thread.sleep(4000);
        // Preconditions: the user is already registered.
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOneUser(USER_PEPE);
    }

    @Test
    public void testPreconditions() throws UiException
    {
        activity = mActivityRule.launchActivity(new Intent());
        RegComuFr regComuFr = (RegComuFr) activity.getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        regUserComuFr = (RegUserComuFr) activity.getFragmentManager().findFragmentById(R.id
                .reg_usercomu_frg);

        assertThat(activity, notNullValue());

        assertThat(isRegisteredUser(activity), is(true));
        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());

        assertThat(regComuFr, notNullValue());
        assertThat(regUserComuFr, notNullValue());

        onView(withId(R.id.reg_comu_and_usercomu_layout));
        onView(withId(R.id.reg_comunidad_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo()).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withContentDescription("Navigate up")).check(matches(isDisplayed()));
        onView(CoreMatchers.allOf(
                        withContentDescription("Navigate up"),
                        isClickable())
        ).check(matches(isDisplayed())).perform(click());
    }

    @Test
    public void testMakeUsuarioComunidadBeanFromView() throws Exception
    {
        activity = mActivityRule.launchActivity(new Intent());
        Resources resources = activity.getResources();
        regUserComuFr = (RegUserComuFr) activity.getFragmentManager().findFragmentById(R.id
                .reg_usercomu_frg);

        View usuarioComunidadRegView = activity.findViewById(R.id.reg_usercomu_frg);

        //UsuarioComunidadBean data.
        onView(withId(R.id.reg_usercomu_portal_ed)).perform(typeText("port2"));
        onView(withId(R.id.reg_usercomu_escalera_ed)).perform(typeText("escale_b"));
        onView(withId(R.id.reg_usercomu_planta_ed)).perform(typeText("planta-N"));
        onView(withId(R.id.reg_usercomu_puerta_ed)).perform(typeText("puerta5"), closeSoftKeyboard());
        onView(withId(R.id.reg_usercomu_checbox_pre)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usercomu_checbox_admin)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usercomu_checbox_inq)).perform(scrollTo(), click());

        // ComunidadBean data: we do not introduce the data in the screen.
        ComunidadBean comunidadBean = new ComunidadBean("ataxo", "24 de Otoño", "001", "bis",
                new Municipio((short) 162, new Provincia((short) 10)));

        UsuarioComunidadBean usuarioComunidadBean =
                makeUserComuBeanFromView(usuarioComunidadRegView, comunidadBean, null);
        assertThat(usuarioComunidadBean.getPortal(), is("port2"));
        assertThat(usuarioComunidadBean.getEscalera(), is("escale_b"));
        assertThat(usuarioComunidadBean.getPlanta(), is("planta-N"));
        assertThat(usuarioComunidadBean.getPuerta(), is("puerta5"));
        assertThat(usuarioComunidadBean.isPresidente(), is(true));
        assertThat(usuarioComunidadBean.isAdministrador(), is(true));
        assertThat(usuarioComunidadBean.isPropietario(), is(false));
        assertThat(usuarioComunidadBean.isInquilino(), is(true));

        usuarioComunidadBean.validate(resources, new StringBuilder(resources.getText(R.string.error_validation_msg)));
        UsuarioComunidad usuarioComunidad = usuarioComunidadBean.getUsuarioComunidad();
        assertThat(usuarioComunidad.getPortal(), is("port2"));
        assertThat(usuarioComunidad.getEscalera(), is("escale_b"));
        assertThat(usuarioComunidad.getPlanta(), is("planta-N"));
        assertThat(usuarioComunidad.getPuerta(), is("puerta5"));
        assertThat(usuarioComunidad.getRoles(), is("adm,pre,inq"));
    }

    @Test
    public void testRegisterComuAndUserComu_1() throws InterruptedException
    {
        activity = mActivityRule.launchActivity(new Intent());

        // NO data.
        onView(withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo(), click());

        ActivityTestUtils.checkToastInTest(R.string.error_validation_msg, activity,
                R.string.tipo_via,
                R.string.nombre_via,
                R.string.municipio,
                R.string.reg_usercomu_role_rot);

    }

    @Test
    public void testRegisterComuAndUserComu_2()
    {
        activity = mActivityRule.launchActivity(new Intent());

        // Wrong data.
        onView(withId(R.id.reg_usercomu_checbox_pre)).perform(click());
        onView(withId(R.id.reg_usercomu_checbox_admin)).perform(click());
        onView(withId(R.id.reg_usercomu_checbox_inq)).perform(click());
        onView(withId(R.id.reg_usercomu_checbox_pro)).perform(click());

        onView(withId(R.id.reg_usercomu_escalera_ed)).perform(typeText("escal ?? b"), closeSoftKeyboard());

        onView(withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo(), click());
        checkToastInTest(R.string.error_validation_msg, activity,
                R.string.tipo_via,
                R.string.nombre_via,
                R.string.municipio,
                R.string.reg_usercomu_role_rot,
                R.string.reg_usercomu_escalera_hint);
    }

    @Test
    public void testRegisterComuAndUserComu_3()
    {
        activity = mActivityRule.launchActivity(new Intent());

        typeComunidadData();

        onView(withId(R.id.reg_usercomu_portal_ed)).perform(typeText("port2"));
        onView(withId(R.id.reg_usercomu_escalera_ed)).perform(typeText("escale_b"));
        onView(withId(R.id.reg_usercomu_planta_ed)).perform(typeText("planta-N"));
        onView(withId(R.id.reg_usercomu_puerta_ed)).perform(typeText("puerta5"), closeSoftKeyboard());
        onView(withId(R.id.reg_usercomu_checbox_pre)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usercomu_checbox_admin)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usercomu_checbox_inq)).perform(scrollTo(), click());

        onView(withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo(), click());
        onView(withId(R.id.see_usercomu_by_user_ac_frg_container)).check(matches(isDisplayed()));
    }
}